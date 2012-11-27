package metridoc.ezproxy

class EzproxyHosts extends EzproxyBase<EzproxyHosts> {

    static final ONE_TO_ONE_PROPERTIES = ["patronId", "ipAddress", "lineNumber", "state", "country", "city", "ezproxyId", "proxyDate", "fileName", "url", "refUrl"]
    Map<String, Set<String>> hostsByEzproxyId = [:]

    static transients = [ "hostsByEzproxyId", "url", "refUrl"]
    static mapping = {
        fileName(index: 'idx_ez_hosts_file_name')
        patronId(index: 'idx_ez_hosts_patron_id')
        country(index: 'idx_ez_hosts_country')
        state(index: 'idx_ez_hosts_state')
        city(index: 'idx_ez_hosts_city')
        ezproxyId(index: "idx_ez_hosts_ezproxy_id")
        valid(index: "idx_ez_hosts_valid")
        urlHost(index: "idx_ez_hosts_url_host")
        dept(index: "idx_ez_hosts_dept")
        organization(index: "idx_ez_organization")
        rank(index: "idx_rank")
        tablePerHierarchy(false)
    }

    static constraints = {
        //size 64 accommodates for sha256 hashing
        ipAddress(maxSize: 64)
        //size 64 accommodates for sha256 hashing
        patronId(maxSize: 64, nullable: true)
        country(maxSize: 50, nullable: true)
        country(maxSize: 50, nullable: true)
        state(maxSize: 50, nullable: true)
        city(maxSize: 50, nullable: true)
        ezproxyId(maxSize: 50)
        validationError(maxSize: 500, nullable: true)
        dept(nullable: true)
        rank(nullable: true)
        organization(nullable: true)
        urlHost(unique: ["ezproxyId"])
        lineNumber(unique: ["fileName"])
        refUrlHost(nullable: true)
    }

    @Override
    void loadValues(Map record) {
        ONE_TO_ONE_PROPERTIES.each {
            this."${it}" = record[it]
        }

        addDateParameters(record)
        try {
            urlHost = new URL(url).host
            refUrlHost = new URL(refUrl).host
        } catch (MalformedURLException e) {
            //do nothing, just let urlHost be null
        }
    }

    protected void addDateParameters(Map<String, Object> record) {
        def proxyDate = record.proxyDate
        if (proxyDate) {
            def calendar = new GregorianCalendar()
            calendar.setTime(proxyDate)
            proxyMonth = calendar.get(Calendar.MONTH) + 1
            proxyYear = calendar.get(Calendar.YEAR)
            proxyDay = calendar.get(Calendar.DAY_OF_MONTH)
        }
    }

    @Override
    void createDefaultInvalidRecord() {
        ipAddress = "ERROR"
        urlHost = "ERROR"
        url = "ERROR"
        valid = false
        lineNumber = lineNumber ?: -1
        fileName = fileName ?: "ERROR"
        ezproxyId = "ERROR"
        proxyDate = new Date(Long.MIN_VALUE)

    }

    @Override
    boolean accept(Map record) {
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
}
