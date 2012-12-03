package metridoc.ezproxy

import java.util.zip.GZIPInputStream
import org.apache.shiro.crypto.hash.Sha256Hash
import groovy.sql.Sql
import org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsDomainBinder

class GormEzproxyFileService implements EzproxyFileService {

    def ezproxyService
    def grailsApplication
    def dataSource

    void handleFatalError(File file, String line, int lineNumber, Throwable error) {
        EzproxyHosts invalidRecord = EzproxyHostsLoading.newInstance().createDefaultInvalidRecord()
        invalidRecord.fileName = file.name
        invalidRecord.lineNumber = lineNumber
        invalidRecord.validationError = error.message

        if (!error instanceof AssertionError) {
            invalidRecord.error = true
        }

        EzproxyHostsLoading.withNewTransaction {
            invalidRecord.save(failOnError: true)
        }

        if (!error instanceof AssertionError) {
            throw error
        }

    }

    void handleValidationError(EzproxyHosts object) {
        if (object.errors.fieldErrorCount) {
            def error = object.errors.fieldErrors[0]
            def message = "Field error in object '" + error.objectName + "' on field '" + error.field +
                    "': rejected value [" + error.rejectedValue + "]"
            def invalidRecord = object.createDefaultInvalidRecord()


            invalidRecord.validationError = message
            invalidRecord.save(failOnError: true)
            log.warn "saved invalid record ${object} for file ${object.fileName} at line ${object.lineNumber}"
        } else {
            object.save(failOnError: true)
        }
    }

    @Override
    void processFile(File file) {
        processFile(file, ezproxyService.parserObject)
    }

    void processFile(File file, parser) {

        try {
            def hex = new Sha256Hash(file).toHex()

            EzproxyHosts.withNewTransaction {
                new EzFileMetaData(fileName: file.name, sha256: hex).save(failOnError: true)
                log.info "stored metaData of file $file"
                def stream = new FileInputStream(file)
                if (file.name.endsWith(".gz")) {
                    stream = new GZIPInputStream(stream)
                }

                stream.eachLine("utf-8") {String line, int lineNumber ->
                    try {
                        def record = parser.parse(line, lineNumber, file.name)
                        process(record)
                    } catch (Throwable throwable) {
                        log.error "error occurred for file $file at line $lineNumber with line $line", throwable
                        handleFatalError(file, line, lineNumber, throwable)
                    }
                }
            }


            log.info "finished processing file $file"
        } catch (Throwable e) {
            def data = EzFileMetaData.findByFileName(file.name)
            if (data) {
                EzFileMetaData.get(data.id).delete()
            }
            EzproxyHosts.executeUpdate('delete EzproxyHosts e where e.fileName = :fileName', [fileName : file.name])
            throw e
        }
    }

    protected void process(Map<String, Object> record) {


        def gormRecord = new EzproxyHosts()
        if (gormRecord.accept(record)) {
            gormRecord.loadValues(record)
            boolean valid = gormRecord.validate()
            if (!valid) {
                handleValidationError(gormRecord)
            } else {
                gormRecord.save(failOnError: true)
                if (log.isDebugEnabled()) {
                    log.debug "saved record ${gormRecord} for file ${gormRecord.fileName} and line ${gormRecord.lineNumber}"
                }
            }
        }

        def lineNumber = record.lineNumber
        def fileName = record.fileName

        if (lineNumber % 10000 == 0) {
            log.info "$lineNumber lines have been processed for file $fileName"
        }


    }


}
