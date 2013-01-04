package metridoc.ezproxy

class EzproxyHosts extends EzproxyBase<EzproxyHosts> {

    static Map<String, Set<String>> hostsByEzproxyId = Collections.synchronizedMap([:])

    static transients = ["hostsByEzproxyId", "url", "refUrl"]
    static mapping = {
        fileName(index: 'idx_ez_hosts_file_name')
        ezproxyId(index: "idx_ez_hosts_ezproxy_id")
        valid(index: "idx_ez_hosts_valid")
        urlHost(index: "idx_ez_hosts_url_host")
        tablePerHierarchy(false)
    }

    static constraints = {
        //size 64 accommodates for sha256 hashing
        ipAddress(maxSize: 64)
        //size 64 accommodates for sha256 hashing
        patronId(maxSize: 64, nullable: true)
        country(maxSize: 50, nullable: true)
        state(maxSize: 50, nullable: true)
        city(maxSize: 50, nullable: true)
        ezproxyId(maxSize: 50)
        validationError(maxSize: Integer.MAX_VALUE, nullable: true)
        dept(nullable: true)
        rank(nullable: true)
        organization(nullable: true)
        refUrlHost(nullable: true)
    }

    @Override
    boolean accept(Map record) {
        def ezproxyId = record.ezproxyId
        def hasEzproxyId = ezproxyId != null && ezproxyId.trim() != "-" && ezproxyId.trim().size() > 1
        def hasUrl
        def url = null
        try {
            url = new URL(record.url)
            hasUrl = true
        } catch (MalformedURLException ex) {
            //do nothing
        }

        def alreadyProcessed = false
        if (hasUrl && hasEzproxyId) {
            def host = url.host
            def localHostsByEzproxyId = hostsByEzproxyId
            alreadyProcessed = localHostsByEzproxyId[ezproxyId] ? localHostsByEzproxyId[ezproxyId].contains(host) : false
            if (!alreadyProcessed) {
                alreadyProcessed = EzproxyHosts.findByEzproxyIdAndUrlHost(ezproxyId, host) ? true : false
                def hostSet = localHostsByEzproxyId[ezproxyId]
                if (hostSet == null) {
                    localHostsByEzproxyId[ezproxyId] = [] as Set
                    hostSet = localHostsByEzproxyId[ezproxyId]
                }

                hostSet.add(host)
            }
        }
        def result = hasEzproxyId && hasUrl && !alreadyProcessed

        if (result) {
            if (log.isDebugEnabled()) {
                log.debug "record ${record} has been accepted"
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug "record ${record} has been rejected"
            }
        }
        return result
    }
}
