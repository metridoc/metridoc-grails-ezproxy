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
    String hashUrl
    String hashRefUrl
    String url
    String refUrl
    String httpMethod
    Integer httpStatus
    String ezproxyId

    Boolean archive
    Boolean doi
    Boolean pmid
    Boolean login
    Boolean forceArchive

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
        hashRefUrl(index: 'idx_ez_log_hashrefurl')
        hashUrl(index: 'idx_ez_log_hashurl')
        doi(index: 'idx_ez_doi')
        pmid(index: 'idx_ez_pmid')
        login(index: 'idx_ez_login')
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
        url(size: 1..Integer.MAX_VALUE)
        refUrl(size: 1..Integer.MAX_VALUE)
        hashRefUrl(maxSize: 64)
        hashUrl(maxSize: 64)
        httpMethod(maxSize: 10)
        ezproxyId(maxSize: 50)
    }
}
