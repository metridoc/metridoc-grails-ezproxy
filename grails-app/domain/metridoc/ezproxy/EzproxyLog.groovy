package metridoc.ezproxy

class EzproxyLog {

    String ipAddress
    String fileName
    String lineNumber

    static constraints = {
        fileName: index: 'idx_ez_log_file_name'
    }
}
