package metridoc.ezproxy

import grails.util.Holders

class EzproxyHosts {

    Date dateCreated
    String ipAddress
    String fileName
    Integer lineNumber
    String patronId
    String state
    String country
    String city
    String UrlHost
    String ezproxyId

    static mapping = {
        def grailsApplication = Holders.grailsApplication

        if (grailsApplication) {
            if(grailsApplication.mergedConfig.dataSource_ezproxy) {
                datasource('ezproxy')
            }
        }

        fileName(index: 'idx_ez_log_file_name')
        patronId(index: 'idx_ez_log_patron_id')
        country(index: 'idx_ez_log_country')
        state(index: 'idx_ez_log_state')
        city(index: 'idx_ez_log_city')
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
    }
}
