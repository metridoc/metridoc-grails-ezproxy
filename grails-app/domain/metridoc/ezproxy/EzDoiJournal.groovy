package metridoc.ezproxy

class EzDoiJournal {

    String doi
    String articleTitle
    String journalTitle
    static final int TITLE_SIZE = 400
    static String ENCODING = "utf-8"
    static final DOI_URL = {String userName, String password, String doi ->
        def doiBase = "http://www.crossref.org/openurl/?noredirect=true&pid="
        def doiEncoded = URLEncoder.encode(doi, ENCODING)
        return new URL("${doiBase}${userName}:${password}id=${doiEncoded}")
    }


    static constraints = {
        doi(unique: true)
        journalTitle(nullable: true, maxSize: TITLE_SIZE)
        articleTitle(nullable: true, maxSize: TITLE_SIZE)
    }

    static mapping = {

    }

    static void resolveDois(int amountToResolve) {
        EzDoiJournal.withNewTransaction {
            def ezDois = EzDoi.findAll(max: amountToResolve) {
                processedDoi == true
            }

            def userName = EzParserProperties.instance().crossRefUserName
            def password = EzParserProperties.decryptedCrossRefPassword

            ezDois.each { doi ->
                def doiId = doi.doi
                def journal = EzDoiJournal.findByDoi(doiId)
                doi.processedDoi = true
                if (journal) {
                    doi.resolvableDoi = true
                } else {

                    def resultStr = DOI_URL(userName, password, doiId).text
                    Node node = new XmlParser().parseText(resultStr);
                    def bodyQuery = node.query_result.body.query
                    if (bodyQuery["@status"].text() == 'resolved') {
                        def ezDoiJournal = new EzDoiJournal()
                        ezDoiJournal.journalTitle = truncateValue(bodyQuery.journal_title.text())
                        ezDoiJournal.articleTitle = truncateValue(bodyQuery.article_title.text())
                        ezDoiJournal.doi = doiId
                        doi.resolvableDoi = true
                        ezDoiJournal.save(failOnError: true)
                    } else {
                        doi.resolvableDoi = false
                    }
                }
                doi.save(failOnError: true)
            }
        }
    }

    private static truncateValue(val){
        if(val != null && val.length() > TITLE_SIZE)
            return val.substring(0, TITLE_SIZE)
        else
            return val
    }
}
