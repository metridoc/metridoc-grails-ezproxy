package metridoc.ezproxy

class EzFileMetaData {

    String fileName
    String sha256

    static mapping = {
        fileName(index: 'idx_ez_hosts_file_name')
    }

    static constraints = {
        sha256(maxSize: 64)
        fileName(unique: true)
    }
}
