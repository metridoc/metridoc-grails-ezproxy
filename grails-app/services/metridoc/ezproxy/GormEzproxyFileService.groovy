package metridoc.ezproxy

import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.IOUtils

import java.util.zip.GZIPInputStream

class GormEzproxyFileService implements EzproxyFileService {

    def ezproxyService
    def grailsApplication

    void handleFatalError(File file, int lineNumber, Throwable error) {
        EzproxyHosts invalidRecord = EzproxyHosts.newInstance().createDefaultInvalidRecord()
        invalidRecord.fileName = file.name
        invalidRecord.lineNumber = lineNumber
        invalidRecord.validationError = error.message

        if (!(error instanceof AssertionError)) {
            invalidRecord.error = true
        }

        EzproxyHosts.withNewTransaction {
            invalidRecord.save(failOnError: true)
        }

        if (!(error instanceof AssertionError)) {
            throw error
        }

    }

    void handleValidationError(EzproxyBase object) {
        if (object.errors.fieldErrorCount) {
            def error = object.errors.fieldErrors[0]
            def message = "Field error in object '" + error.objectName + "' on field '" + error.field +
                    "': rejected value [" + error.rejectedValue + "]"
            def invalidRecord = object.createDefaultInvalidRecord()


            invalidRecord.validationError = message
            invalidRecord.save(failOnError: true)
            log.warn "saved invalid record ${object} for file ${object.fileName} at line ${object.lineNumber} with validation error message: ${message}"
        } else {
            object.save(failOnError: true)
        }
    }

    @Override
    void processFile(File file) {
        processFile(file, ezproxyService.parserObject)
    }

    String getEncoding() {
        grailsApplication?.mergedConfig?.metridoc?.ezproxy?.encoding ?: "utf-8"
    }

    void processFile(File file, parser) {

        try {
            def streamToDigest = file.newInputStream()
            def hex = DigestUtils.sha256Hex(streamToDigest)
            IOUtils.closeQuietly(streamToDigest)

            def fileName = file.name
            EzproxyHosts.withNewTransaction {
                new EzFileMetaData(fileName: fileName, sha256: hex).save(failOnError: true)
                log.info "stored metaData of file $file"
                def stream = new FileInputStream(file)
                if (file.name.endsWith(".gz")) {
                    stream = new GZIPInputStream(stream)
                }

                stream.eachLine(getEncoding()) { String line, int lineNumber ->
                    try {
                        def record = parser.parse(line, lineNumber, file.name)
                        process(record)
                    } catch (Throwable throwable) {
                        log.error "error occurred for file $file at line $lineNumber with line $line", throwable
                        handleFatalError(file, lineNumber, throwable)
                    }
                }
            }
            //TODO: use event based programming here instead?
            grailsApplication.domainClasses.each {
                def instance = it.newInstance()
                if (instance instanceof EzproxyBase) {
                    instance.finishedFile(fileName)
                }
            }
            log.info "finished processing file $file"
        } catch (Throwable e) {
            def data = EzFileMetaData.findByFileName(file.name)
            if (data) {
                EzFileMetaData.get(data.id).delete()
            }
            throw e
        }
    }

    protected void process(Map<String, Object> record) {


        def linesLogged = [] as Set
        grailsApplication.domainClasses.each {
            def gormRecord = it.newInstance()
            if (gormRecord instanceof EzproxyBase) {

                if (gormRecord.accept(record)) {
                    gormRecord.loadValues(record)
                    boolean valid = gormRecord.validate()
                    if (!valid) {
                        handleValidationError(gormRecord)
                    } else {
                        gormRecord.save(failOnError: true)
                        if (log.isDebugEnabled()) {
                            log.debug "saved record ${gormRecord} for file ${gormRecord.fileName} and line ${gormRecord.lineNumber} for gorm object ${gormRecord.getClass()}"
                        }
                    }
                }

                def lineNumber = record.lineNumber
                def fileName = record.fileName
                if (lineNumber % 10000 == 0  && !linesLogged.contains(lineNumber)) {
                    log.info "$lineNumber lines have been processed for file $fileName"
                    linesLogged << lineNumber
                }
            }
        }



    }


}
