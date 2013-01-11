package metridoc.ezproxy

class EzFileMetaData {

    String fileName
    String sha256
    Boolean processing
    Date processStarted

    static mapping = {
        fileName(index: 'idx_ez_hosts_file_name')
    }

    static constraints = {
        sha256(maxSize: 64)
        fileName(unique: true)
        //TODO: eventually we should get rid of this and make them not nullable
        processing(nullable: true)
        processStarted(nullable: true)
    }
}
