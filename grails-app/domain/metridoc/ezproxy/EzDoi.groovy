package metridoc.ezproxy

import java.util.regex.Pattern

class EzDoi extends EzproxyBase<EzDoi> {

    static Map<String, Set<String>> doiByEzproxyId = Collections.synchronizedMap([:])
    static transients = ["refUrl", "ipAddress", "patronId", "state", "country", "city", "refUrlHost", "dept", "organization", "rank", "url"]
    String doi
    public static final EZ_DOI_ONE_TO_ONE
    Boolean resolvableDoi = false
    Boolean processedDoi = false
    public static final DOI_PREFIX_PATTERN = "10."
    public static final DOI_PROPERTY_PATTERN = "doi=10."
    public static final APACHE_NULL = "-"
    public static final DOI_FULL_PATTERN = Pattern.compile(/10\.\d+\//)

    static {
        EZ_DOI_ONE_TO_ONE = ["doi"]
        EZ_DOI_ONE_TO_ONE.addAll(DEFAULT_ONE_TO_ONE_PROPERTIES)
    }

    static mapping = {
        fileName(index: 'idx_ez_doi_file_name')
        valid(index: "idx_ez_doi_valid")
        urlHost(index: "idx_ez_doi_url_host")
        ezproxyId(index: "idx_ez_doi_ezproxy_id")
        doi(index: "idx_ez_doi_doi")
    }

    static constraints = {
        ezproxyId(maxSize: 50, unique: 'doi')
        validationError(maxSize: Integer.MAX_VALUE, nullable: true)
        doi(maxSize: 150)
    }

    @Override
    void loadValues(Map record) {
        super.loadValues(record, EZ_DOI_ONE_TO_ONE)    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    boolean accept(Map record) {
        boolean hasEzproxyIdAndUrl = super.accept(record)
        boolean notProcessed = false
        boolean hasDoi = hasDoi(record)
        if (hasEzproxyIdAndUrl && hasDoi) {
            try {
                record.doi = extractDoi(record.url as String)
            } catch (Exception e) {
                log.warn "There was an unexpected exception trying to extract the doi from ${record.url}", e
                return false
            }
            notProcessed = !alreadyProcessed(doiByEzproxyId, record.ezproxyId, "doi", record.doi)
        }
        def result = hasEzproxyIdAndUrl && hasDoi && notProcessed

        return result
    }

    @Override
    EzDoi createDefaultInvalidRecord() {
        EzDoi doi = (EzDoi) super.createDefaultInvalidRecord()
        doi.processedDoi = true
        doi.doi = UUID.randomUUID().toString() //to make it unique against ezproxy_id

        return doi
    }

    @Override
    void finishedFile(String fileName) {
        doiByEzproxyId.clear()
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
        String doiAtStart = null
        int indexOfDoiPrefix = url.indexOf(DOI_PREFIX_PATTERN)
        if (indexOfDoiPrefix > -1) {
            doiAtStart = url.substring(indexOfDoiPrefix)
            try {
                doiAtStart = URLDecoder.decode(doiAtStart)
            } catch (IllegalArgumentException ex) {

            }
            def doiMatcher = DOI_FULL_PATTERN.matcher(doiAtStart)
            return doiMatcher.lookingAt()
        }

        return false
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
        boolean
        if (idxBegin > -1) {
            String doiBegin = url.substring(idxBegin + 4)
            int idxEnd = doiBegin.indexOf('&') > 0 ? doiBegin.indexOf('&') : doiBegin.size()
            result = URLDecoder.decode(URLDecoder.decode(doiBegin.substring(0, idxEnd), "utf-8"), "utf-8") //double encoding
        } else {
            idxBegin = url.indexOf(DOI_PREFIX_PATTERN)
            if (idxBegin > -1) {
                String doiBegin = url.substring(idxBegin)
                //find index of 2nd slash
                int slashInd = doiBegin.indexOf("/");
                slashInd = slashInd > -1 ? doiBegin.indexOf("/", slashInd + 1) : -1;
                int idxEnd = doiBegin.indexOf('?')
                if (idxEnd == -1) {
                    //case where doi is buried in embedded url
                    doiBegin = URLDecoder.decode(doiBegin, "utf-8")
                    idxEnd = doiBegin.indexOf('&')
                    slashInd = slashInd > -1 ? doiBegin.indexOf("/", slashInd + 1) : -1; // compute again in case of encoding
                }
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

        if (result && result.contains("/")) {
            int startIndex = result.indexOf("/")
            String suffix = result.substring(startIndex + 1, result.length())
            int nextSlash = suffix.indexOf("/")
            if (nextSlash > -1) {
                result = result.substring(0, startIndex + nextSlash + 1)
            }
        } else {
            result = null //must be garbage
        }
        return result
    }
}
