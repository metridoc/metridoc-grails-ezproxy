package metridoc.ezproxy

abstract class GormEzproxyEventService implements EzproxyEventService {

    def batch = []

    abstract def getGormClass()
    abstract boolean accept(Map<String, Object> record)

    @Override
    void process(Map<String, Object> record) {
        if(accept(record)) {
            def transformedRecord = transformRecord(record)
            def gormRecord = getGormClass().newInstance(transformedRecord)
            batch << gormRecord
        }
    }

    void fileDone(String fileName) {
        getGormClass().newTransaction {
            batch.each {
                it.save()
            }
        }
    }

    Map transformRecord(Map<String, Object> record) {
        return record
    }
}
