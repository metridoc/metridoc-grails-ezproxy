package metridoc.ezproxy

class EzproxyHostsFileService extends GormEzproxyFileService<EzproxyHosts> {

    static final ONE_TO_ONE_PROPERTIES = ["patron_id", "ipAddress", "lineNumber", "state", "country", "city", "ezproxyId", "proxyDate", "fileName"]
    Map<String, Set<String>> hostsByEzproxyId = [:]


    @Override
    Class<EzproxyHosts> getGormClass() {
        EzproxyHosts
    }

    @Override
    boolean accept(Map<String, Object> record) {
        def ezproxyId = record.ezproxyId
        def hasEzproxyId = ezproxyId != null && ezproxyId.trim() != "-" && ezproxyId.trim().size() > 1
        def hasUrl
        def url
        try {
            url = new URL(record.url)
            hasUrl = true
        } catch (MalformedURLException) {
            //do nothing
        }

        def alreadyProcessed = false
        if(hasUrl) {
            def host = url.host
            alreadyProcessed = hostsByEzproxyId[ezproxyId] ? hostsByEzproxyId[ezproxyId].contains(host) : false
            if(!alreadyProcessed) {
                alreadyProcessed = EzproxyHosts.findByEzproxyIdAndUrlHost(ezproxyId, host) ? true : false
                def hostSet = hostsByEzproxyId[ezproxyId]
                if(hostSet == null) {
                    hostsByEzproxyId[ezproxyId] = [] as Set
                    hostSet = hostsByEzproxyId[ezproxyId]
                }

                hostSet.add(host)
            }
        }
        return hasEzproxyId && hasUrl && !alreadyProcessed
    }

    @Override
    EzproxyHosts getNewDefaultRecord(EzproxyHosts ezproxyHosts) {
        getNewDefaultRecord()
    }

    EzproxyHosts getNewDefaultRecord() {
        new EzproxyHosts(
                ipAddress: "ERROR",
                urlHost: "ERROR",
                valid: false,
                lineNumber: -1,
                fileName: "ERROR",
                ezproxyId: "ERROR",
                proxyDate : new Date(Long.MIN_VALUE),
                processed: false
        )
    }

    @Override
    Map transformRecord(Map<String, Object> record) {
        def transformedRecord = [:]

        addOneToOneProperties(record, transformedRecord)

        try {
            transformedRecord.urlHost = new URL(record.url).host
        } catch (MalformedURLException e) {
            //do nothing, just let urlHost be null
        }

        addDateParameters(transformedRecord)
        return transformedRecord
    }

    static addOneToOneProperties(Map record, Map transformedRecord) {
        ONE_TO_ONE_PROPERTIES.each {
            transformedRecord[it] = record[it]
        }
    }
}
