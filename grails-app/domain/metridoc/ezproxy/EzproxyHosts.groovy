package metridoc.ezproxy

import grails.util.Holders

class EzproxyHosts {

    Date dateCreated
    Date proxyDate
    int proxyMonth
    int proxyYear
    int proxyDay
    String ipAddress
    String fileName
    Integer lineNumber
    String patronId
    String state
    String country
    String city
    String urlHost
    String ezproxyId
    Boolean valid = true
    String validationError
    String dept
    String organization
    String rank

    static mapping = {
        def grailsApplication = Holders.grailsApplication

        if (grailsApplication) {
            if(grailsApplication.mergedConfig.dataSource_ezproxy) {
                datasource('ezproxy')
            }
        }

        fileName(index: 'idx_ez_hosts_file_name')
        patronId(index: 'idx_ez_hosts_patron_id')
        country(index: 'idx_ez_hosts_country')
        state(index: 'idx_ez_hosts_state')
        city(index: 'idx_ez_hosts_city')
        ezproxyId(index:  "idx_ez_hosts_ezproxy_id")
        valid(index:  "idx_ez_hosts_valid")
        urlHost(index:  "idx_ez_hosts_url_host")
        dept(index: "idx_ez_hosts_dept")
        organization(index: "idx_ez_organization")
        rank(index: "idx_rank")
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
    }
}
