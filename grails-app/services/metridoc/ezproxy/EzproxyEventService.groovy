package metridoc.ezproxy

interface EzproxyEventService {
    /**
     * processes the record.  This should only be called if {@link EzproxyEventService#accept} returns true
     */
    void process(Map<String, Object> record)

}
