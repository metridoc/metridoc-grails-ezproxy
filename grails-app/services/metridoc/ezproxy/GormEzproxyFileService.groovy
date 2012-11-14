package metridoc.ezproxy

import org.springframework.util.StringUtils

abstract class GormEzproxyFileService<T> implements EzproxyFileService {

    def ezproxyService
    abstract Class<T> getGormClass()
    abstract boolean accept(Map<String, Object> record)
    abstract Map transformRecord(Map<String, Object> record)
    abstract T getNewDefaultRecord(T objectWithValidationError)

    void handleValidationError(T object) {
        def error = object.errors.fieldErrors[0]
        def message =  "Field error in object '" + error.objectName + "' on field '" + error.field +
                "': rejected value [" + error.rejectedValue + "]; codes: ${StringUtils.arrayToDelimitedString(error.codes, ",")}"
        def invalidResult = getNewDefaultRecord(object)

        if(object.fileName) {
            invalidResult.fileName = object.fileName
        }

        if(object.lineNumber) {
            invalidResult.lineNumber = object.lineNumber
        }

        invalidResult.validationError = message

        invalidResult.save(failOnError: true)
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
        if(accept(record)) {
            def transformedRecord = transformRecord(record)
            addDateParameters(record)
            def gormRecord = getGormClass().newInstance(transformedRecord)
            boolean valid = gormRecord.validate()
            if(!valid) {
                handleValidationError(gormRecord)
            } else {
                gormRecord.save(failOnError: true)
            }
        }
    }

    protected void addDateParameters(Map<String, Object> record) {
        def proxyDate = record.proxyDate
        if (proxyDate) {
            def calendar = new GregorianCalendar()
            calendar.setTime(proxyDate)
            record.proxyMonth = calendar.get(Calendar.MONTH) + 1
            record.proxyYear = calendar.get(Calendar.YEAR)
            record.proxyDay = calendar.get(Calendar.DAY_OF_MONTH)
        }
    }
}
