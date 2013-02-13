package metridoc.ezproxy

import groovy.transform.ToString
import groovy.xml.QName
import org.springframework.uaa.client.util.Assert

import static org.apache.commons.lang.StringUtils.EMPTY

class DoiService {

    static final String CROSS_REF_BASE_URL = "http://www.crossref.org/openurl/?noredirect=true&pid="
    static String ENCODING = "utf-8"
    private static final String NULL_MESSAGE = { String item ->
        "$item cannot be null or blank"
    }

    DoiStats populateDoiInformation(int amountToResolve) {
        def ezProperties = EzParserProperties.instance()
        String userName = ezProperties.crossRefUserName
        String password = EzParserProperties.decryptedCrossRefPassword

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
                    doi.resolvableDoi = true
                    stats.alreadyExsists ++
                } else {

                    def url = createCrossRefUrl(userName, password, doiId)
                    def resultStr = url.text
                    Node node
                    try {
                        def result = parseXml(resultStr)
                        def status = result.status
                        if (status == 'resolved') {
                            def ezDoiJournal = new EzDoiJournal(result)
                            doi.resolvableDoi = true
                            ezDoiJournal.save(failOnError: true)
                            stats.resolved ++
                        } else if (status == 'unresolved') {
                            doi.resolvableDoi = false
                            stats.unresolved ++
                            log.warn("CrossRef did not have information for doi ${doiId} using url ${url}")
                        } else {
                            throw new RuntimeException("Unexpected response occurred from CrossRef, should have a status of resolved or unresolved")
                        }
                        doi.save(failOnError: true)
                    } catch (Exception e) {
                        log.error("Could not parse doi ${doi.doi} with url from ${url} and response ${resultStr}", e)
                        throw e
                    }
                }

            }
        }

        return stats

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

        result.status = bodyQuery["@status"].text()
        result.givenName = getItem(contributor.given_name)
        result.surName = getItem(contributor.surname)
        result.volume = getItem(bodyQuery.volume)
        result.issue = getItem(bodyQuery.issue)
        result.firstPage = getItem(bodyQuery.first_page)
        result.lastPage = getItem(bodyQuery.last_page)

        result.journalTitle = truncateValue(bodyQuery.journal_title.text())
        result.articleTitle = truncateValue(getItem(bodyQuery.article_title))

        multiValueSearch(bodyQuery.year as NodeList, "media_type", result) {Integer.valueOf(it)}
        multiValueSearch(bodyQuery.issn as NodeList, "type", result)
        multiValueSearch(bodyQuery.isbn as NodeList, "type", result)

        return result
    }

    private static void multiValueSearch(NodeList items, String typeAttribute, Map result) {
        multiValueSearch(items, typeAttribute, result, null)
    }

    private static void multiValueSearch(NodeList items, String typeAttribute, Map result, Closure closure) {
        items.each {Node it ->
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

    private static truncateValue(val){
        if(val != null && val.length() > EzDoiJournal.TITLE_SIZE)
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
