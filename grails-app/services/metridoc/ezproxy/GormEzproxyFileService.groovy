package metridoc.ezproxy

import org.springframework.util.StringUtils
import java.util.zip.GZIPInputStream

class GormEzproxyFileService implements EzproxyFileService {

    def ezproxyService
    def grailsApplication
    private List<Class<EzproxyBase>> _gormClasses = []

    List<EzproxyBase> getGormClasses() {
        if(_gormClasses) return _gormClasses

        grailsApplication.domainClasses.each {
            def clazz = it.clazz
            if(clazz == EzproxyHosts) {
                _gormClasses << clazz
            }
        }

        return _gormClasses
    }

    void handleValidationError(EzproxyHosts object) {
        if (object.errors.fieldErrorCount) {
            def error = object.errors.fieldErrors[0]
            def message =  "Field error in object '" + error.objectName + "' on field '" + error.field +
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

        def stream = new FileInputStream(file)
        if(file.name.endsWith(".gz")) {
            stream = new GZIPInputStream(stream)
        }

        stream.eachLine("utf-8") {String line, int lineNumber ->
            try {
                def record = parser.parse(line, lineNumber, file.name)
                process(record)
            } catch (Throwable throwable) {
                log.error "error occurred for file $file at line $lineNumber with line $line", throwable
                throw throwable
            }
        }
    }

    protected void process(Map<String, Object> record) {

        gormClasses.each {Class<EzproxyBase> ezBase ->
            def gormRecord = ezBase.newInstance()
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

            if(lineNumber % 10000 == 0) {
                log.info "$lineNumber lines have been processed for file $fileName"
            }
        }
    }


}
