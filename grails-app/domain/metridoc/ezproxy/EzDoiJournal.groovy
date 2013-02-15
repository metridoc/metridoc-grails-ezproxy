package metridoc.ezproxy

import groovy.transform.ToString
import org.xml.sax.SAXParseException

class EzDoiJournal {

    String doi
    String articleTitle
    String journalTitle
    String givenName
    String surName
    String volume
    String issue
    String firstPage
    String lastPage
    Integer printYear
    Integer electronicYear
    Integer onlineYear
    String printIssn
    String electronicIssn
    String printIsbn
    String electronicIsbn

    static final int TITLE_SIZE = 400
    static String ENCODING = "utf-8"
    static final DOI_URL = {String userName, String password, String doi ->
        def doiBase = "http://www.crossref.org/openurl/?noredirect=true&pid="
        def doiEncoded = URLEncoder.encode(doi, ENCODING)
        return new URL("${doiBase}${userName}:${password}&id=${doiEncoded}")
    }

    static constraints = {
        doi(unique: true)
        journalTitle(nullable: true, maxSize: TITLE_SIZE)
        articleTitle(nullable: true, maxSize: TITLE_SIZE)
        givenName(nullable: true)
        surName(nullable: true)
        volume(nullable: true)
        issue(nullable: true)
        firstPage(nullable: true)
        lastPage(nullable: true)
        printYear(nullable: true)
        electronicYear(nullable: true)
        onlineYear(nullable: true)
        printIssn(nullable: true)
        electronicIssn(nullable: true)
        printIsbn(nullable: true)
        electronicIsbn(nullable: true)
    }

    static mapping = {

    }
}
