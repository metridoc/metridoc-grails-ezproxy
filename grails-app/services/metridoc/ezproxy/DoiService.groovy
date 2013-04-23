package metridoc.ezproxy

import groovy.transform.ToString
import groovy.xml.QName
import org.slf4j.LoggerFactory
import org.springframework.util.Assert
import org.xml.sax.SAXParseException

import java.sql.SQLException

import static org.apache.commons.lang.StringUtils.EMPTY

class DoiService {

    public static final log = LoggerFactory.getLogger(DoiService)
    static final String CROSS_REF_BASE_URL = "http://www.crossref.org/openurl/?noredirect=true&pid="
    static String ENCODING = "utf-8"
    String crossRefUsername
    String crossRefPassword

    private static void resolvable(DoiStats stats, EzDoi doi) {
        stats.resolved++
        doi.resolvableDoi = true
    }

    private static void unresolvable(DoiStats stats, EzDoi doi) {
        stats.unresolved++
        doi.resolvableDoi = false
    }

    DoiStats populateDoiInformation(int amountToResolve) {
        if (!crossRefUsername || !crossRefPassword) {
            log.warn "doi crossref username and or password information has not been set, dois cannot be resolved"
            return null
        }

        def stats = new DoiStats()
        EzDoiJournal.withNewTransaction {
            def ezDois = EzDoi.findAll(max: amountToResolve) {
                processedDoi == false
                valid == true
                error == false
            }
            stats.total = ezDois.size()

            ezDois.each { doi ->
                def doiId = doi.doi
                def journal = EzDoiJournal.findByDoi(doiId)
                doi.processedDoi = true
                if (journal) {
                    resolvable(stats, doi)
                } else {

                    def url = createCrossRefUrl(crossRefUsername, crossRefPassword, doiId)
                    def resultStr
                    try {
                        resultStr = url.text
                    } catch (IOException ioException) {
                        log.warn("Error occurred trying to extract info from $url", ioException)
                        unresolvable(stats, doi)
                        doi.error = true
                        return
                    }
                    if (resultStr.contains("The login you supplied is not recognized")) {
                        def invalidCrossRefLogin = "Invalid CrossRef login"
                        def exception = new RuntimeException(invalidCrossRefLogin)
                        log.error("Invalid CrossRef login", exception)
                        throw exception
                    }

                    if (resultStr.contains("Malformed DOI")) {
                        unresolvable(stats, doi)
                        log.warn("The doi ${doiId} is malformed")
                        return
                    }

                    try {
                        def result = parseXml(resultStr)
                        def status = result.status
                        if (status == 'resolved') {
                            def ezDoiJournal = new EzDoiJournal(result)
                            ezDoiJournal.doi = doiId
                            try {
                                //since we use it in the lookup above, we must flush it
                                ezDoiJournal.save(failOnError: true, flush: true)
                                resolvable(stats, doi)
                            } catch (SQLException e) {
                                log.warn("Could not store information for doi ${doiId} into database, marking doi as unresolvable subsequant runs will not fail")
                                def id = doi.id
                                def doiWithError = EzDoi.get(id)
                                doiWithError.resolvableDoi = false
                                doiWithError.storageError = true
                                doiWithError.save(flush: true)
                                throw e
                            }
                        } else if (status == 'unresolved') {
                            unresolvable(stats, doi)
                            if (log.isDebugEnabled()) {
                                log.debug("CrossRef did not have information for doi ${doiId} using url ${url}")
                            }
                        } else if (status == 'malformed') {
                            unresolvable(stats, doi)
                            log.warn("The doi ${doiId} is malformed")
                        } else {
                            throw new RuntimeException("Unexpected response occurred from CrossRef, should have a status of resolved or unresolved")
                        }
                        doi.save(failOnError: true)
                    } catch (SAXParseException saxException) {
                        handleSaxError(resultStr, stats, doi, saxException)
                    } catch (SQLException sqlException) {
                        //just throw it without logging since it is already taken care of
                        throw sqlException
                    } catch (Exception e) {
                        log.error("Could not parse doi ${doi.doi} with url from ${url} and response ${resultStr}")
                        throw e
                    }


                }

            }
        }

        return stats

    }

    private void handleSaxError(String xml, DoiStats stats, EzDoi doi, Exception saxException) {
        unresolvable(stats, doi)
        def doiId = doi.doi
        doi.error = true
        if (xml.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")) {
            log.warn("xml for ${doiId} is invalid, considering doi unresolved despite getting a response, the xml was ${xml}", saxException)
        } else {
            log.warn("unparsable response for doi ${doiId}, considering it unresolvable.  The response was ${xml}", saxException)
        }
    }

    static URL createCrossRefUrl(String userName, String password, String doi) {
        checkItem("User name", userName)
        checkItem("Password", password)
        checkItem("Doi", doi)

        def encodedDoi = URLEncoder.encode(doi, ENCODING)
        return new URL("$CROSS_REF_BASE_URL$userName:$password&id=$encodedDoi")
    }

    private static void checkItem(String itemName, String value) {
        Assert.isTrue(value != null && value != EMPTY,
                "$itemName cannot be null or blank")
    }

    private static Map parseXml(String xml) {
        def result = [:]
        def node = new XmlParser().parseText(xml);
        def bodyQuery = node.query_result.body.query

        def contributor = bodyQuery.contributors.contributor.find {
            it["@sequence"] == "first"
        }
        if (contributor) {
            result.givenName = getItem(contributor.given_name)
            result.surName = getItem(contributor.surname)
        }

        result.status = bodyQuery["@status"].text()
        result.volume = getItem(bodyQuery.volume)
        result.issue = getItem(bodyQuery.issue)
        result.firstPage = getItem(bodyQuery.first_page)
        result.lastPage = getItem(bodyQuery.last_page)

        result.journalTitle = truncateValue(bodyQuery.journal_title.text())
        result.articleTitle = truncateValue(getItem(bodyQuery.article_title))

        multiValueSearch(bodyQuery.year as NodeList, "media_type", result) { Integer.valueOf(it) }
        multiValueSearch(bodyQuery.issn as NodeList, "type", result)
        multiValueSearch(bodyQuery.isbn as NodeList, "type", result)

        return result
    }

    private static void multiValueSearch(NodeList items, String typeAttribute, Map result) {
        multiValueSearch(items, typeAttribute, result, null)
    }

    private static void multiValueSearch(NodeList items, String typeAttribute, Map result, Closure closure) {
        items.each { Node it ->
            QName name = it.name()
            def localName = name.localPart.capitalize()
            def usedAttribute = "@$typeAttribute"
            def lookup = "${it[usedAttribute]}$localName"
            def notInResult = result[lookup] == null
            if (notInResult) {
                if (closure) {
                    result[lookup] = closure.call(getItem(it))
                } else {
                    result[lookup] = getItem(it)
                }
            }
        }
    }

    private static String getItem(item) {
        item.text() ?: null
    }

    private static truncateValue(val) {
        if (val != null && val.length() > EzDoiJournal.TITLE_SIZE)
            return val.substring(0, EzDoiJournal.TITLE_SIZE)
        else
            return val
    }

    @ToString(includeNames = true)
    public static class DoiStats {
        int total = 0
        int alreadyExsists = 0
        int resolved = 0
        int unresolved = 0

        void testStats() {
            assert total == alreadyExsists + resolved + unresolved
        }
    }
}
