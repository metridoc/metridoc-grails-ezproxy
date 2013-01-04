package metridoc.ezproxy

class EzDoi extends EzproxyBase<EzDoi> {

    static transients = ["refUrl", "ipAddress", "patronId", "state", "country", "city", "urlHost", "refUrlHost", "ezproxyId", "dept", "organization", "rank"]
    String doi
    public static final EZ_DOI_ONE_TO_ONE
    Boolean resolvableDoi = false
    Boolean processedDoi = false
    public static final DOI_PATTERN = "10."
    public static final DOI_PROPERTY_PATTERN = "doi=10."
    public static final APACHE_NULL = "-"

    static {
        EZ_DOI_ONE_TO_ONE = ["doi"]
        EZ_DOI_ONE_TO_ONE.addAll(DEFAULT_ONE_TO_ONE_PROPERTIES)
    }

    static constraints = {
        url(maxSize: Integer.MAX_VALUE)
        ezproxyId(maxSize: 50)
        resolvableDoi()
        validationError(maxSize: Integer.MAX_VALUE, nullable: true)
    }

    @Override
    void loadValues(Map record) {
        record.doi = extractDoi(record.url)
        super.loadValues(record, DEFAULT_ONE_TO_ONE_PROPERTIES)    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    boolean accept(Map record) {
        hasUrl(record) && hasDoi(record) && urlIsAUrl(record)
    }

    @Override
    EzDoi createDefaultInvalidRecord() {
        EzDoi doi = (EzDoi) super.createDefaultInvalidRecord()
        doi.processedDoi = true
        if (doi.doi == null) {
            doi.doi = "ERROR"
        }

        return doi
    }

    private static boolean hasUrl(Map record) {
        String url = record.url

        url ?
            url.trim() ?
                url.trim() != APACHE_NULL ?:
                    false : false : false
    }

    private static boolean hasDoi(Map record) {
        String url = record.url

        url.contains(DOI_PATTERN)
    }

    private static boolean urlIsAUrl(Map record) {
        String url = record.url

        try {
            new URL(url)
            return true
        } catch (MalformedURLException e) {
            return false
        }
    }

    private static String extractDoi(String url) {
        String result = null
        int idxBegin = url.indexOf(DOI_PROPERTY_PATTERN)
        if (idxBegin > -1) {
            String doiBegin = url.substring(idxBegin + 4)
            int idxEnd = doiBegin.indexOf('&') > 0 ? doiBegin.indexOf('&') : doiBegin.size()
            result = URLDecoder.decode(doiBegin.substring(0, idxEnd), "utf-8")
        } else {
            idxBegin = url.indexOf(DOI_PATTERN)
            if (idxBegin > -1) {
                String doiBegin = url.substring(idxBegin)
                //find index of 2nd slash
                int slashInd = doiBegin.indexOf("/");
                slashInd = slashInd > -1 ? doiBegin.indexOf("/", slashInd + 1) : -1;
                int idxEnd = doiBegin.indexOf('?')
                if (idxEnd > -1) {
                    if (slashInd > -1) {
                        idxEnd = [slashInd, idxEnd].min()
                    }
                } else if (slashInd > -1) {
                    idxEnd = slashInd
                } else {
                    idxEnd = doiBegin.size()
                }
                result = doiBegin.substring(0, idxEnd)
            }
        }

        return result
    }
}
