package metridoc.ezproxy

interface EzproxyFileService {
    /**
     * processes an entire ezproxy file.  Should do its best to process an entire file within a transaction
     * @param file file to process
     */
    void processFile(File file)
}
