package metridoc.ezproxy

import org.apache.commons.lang.math.RandomUtils

class EzproxyHosts extends EzproxyBase<EzproxyHosts> {

    static final ONE_TO_ONE_PROPERTIES = ["patronId", "ipAddress", "lineNumber", "state", "country", "city", "ezproxyId", "proxyDate", "fileName", "url", "refUrl"]
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
    EzproxyHosts createDefaultInvalidRecord() {
        new EzproxyHosts(
                ipAddress : "ERROR",
                urlHost : "ERROR-${RandomUtils.nextInt(100000)}",
                url : "ERROR",
                valid : false,
                lineNumber : lineNumber ?: -1,
                fileName : fileName ?: "ERROR",
                ezproxyId : "ERROR",
                proxyDate : new Date()
        )

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
