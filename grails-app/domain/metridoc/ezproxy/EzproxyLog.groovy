package metridoc.ezproxy

import grails.util.Holders

class EzproxyLog {

    Date dateCreated
    String ipAddress
    String fileName
    Integer lineNumber
    String patronId
    String state
    String country
    String city
    String url
    String refUrl
    Boolean archive
    Boolean doi
    Boolean pmid
    Boolean sfx

    static mapping = {
        def grailsApplication = Holders.grailsApplication

        if (grailsApplication) {
            if(grailsApplication.mergedConfig.dataSource_ezproxy) {
                datasource('ezproxy')
            }
        }
    }

    static constraints = {
        fileName: index: 'idx_ez_log_file_name'
        //size 64 accommodates for sha256 hashing
        ipAddress(maxSize: 64)
        //size 64 accommodates for sha256 hashing
        patronId(maxSize: 64, nullable: true)
        patronId: index: 'idx_ez_log_patron_id'
        country(maxSize: 50, nullable: true)
        country: index: 'idx_ez_log_country'
        state(maxSize: 50, nullable: true)
        state: index: 'idx_ez_log_state'
        city(maxSize: 50, nullable: true)
        city: index: 'idx_ez_log_city'
        url(size: 1..Integer.MAX_VALUE)
        refUrl(size: 1..Integer.MAX_VALUE)
    }
}
