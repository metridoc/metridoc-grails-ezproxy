package metridoc.ezproxy

import org.springframework.util.StringUtils

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
                    "': rejected value [" + error.rejectedValue + "]; codes: ${StringUtils.arrayToDelimitedString(error.codes, ",")}"
            object.createDefaultInvalidRecord()
            def invalidResult = object.createDefaultInvalidRecord(object)

            invalidResult.validationError = message
            invalidResult.save(failOnError: true)
        } else {
            object.save(failOnError: true)
        }
    }

    @Override
    void processFile(File file) {
        processFile(file, ezproxyService.parserObject)
    }

    void processFile(File file, parser) {
        file.eachLine("utf-8") {String line, int lineNumber ->
            def record = parser.parse(line, lineNumber, file.name)
            process(record)
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
                }
            }
        }
    }


}
