package metridoc.ezproxy

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.junit.Test
import org.xml.sax.SAXParseException

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(DoiService)
@Mock(EzParserProperties)
class DoiServiceTests {

    def stats = new DoiService.DoiStats()
    def ezDoi = new EzDoi()

    @Test
    void "test url construction based on user name and password"() {
        def url = DoiService.createCrossRefUrl("foo", "bar", "10.1111/(ISSN)1468-3083")
        assert url
        assert "http://www.crossref.org/openurl/?noredirect=true&pid=foo:bar&id=10.1111%2F%28ISSN%291468-3083" == url.toString()
    }

    @Test
    void "test bad inputs"() {
        runBadInput(null, "foo", "bar")
        runBadInput("", "foo", "bar")
        runBadInput("foo", null, "bar")
        runBadInput("foo", "", "bar")
        runBadInput("foo", "bar", null)
        runBadInput("foo", "bar", "")
    }

    @Test
    void "test parsing xml and extracting values"() {
        def values = DoiService.parseXml(validCrossrefXml)
        assert "resolved" == values.status
        assert "British Journal of Dermatology" == values.journalTitle
        assert "K." == values.givenName
        assert "Mosterd" == values.surName
        assert "159" == values.volume
        assert "4" == values.issue
        assert "864" == values.firstPage
        assert EzDoiJournal.TITLE_SIZE == values.articleTitle.size()
        assert "870" == values.lastPage
        assert 2008 == values.printYear
        assert 2011 == values.electronicYear
        assert 2012 == values.onlineYear
        assert "00070963" == values.printIssn
        assert "13652133" == values.electronicIssn
        assert "123123" == values.printIsbn
        assert "123123" == values.electronicIsbn
    }

    @Test
    void "resolvable increments resolvable and makes doi resolvable"() {

        DoiService.resolvable(stats, ezDoi)
        assert 1 == stats.resolved
        assert ezDoi.resolvableDoi
    }

    @Test
    void "unresolvable increments unresolvable and makes doi unresolvable"() {
        DoiService.unresolvable(stats, ezDoi)
        assert 1 == stats.unresolved
        assert !ezDoi.resolvableDoi
    }

    @Test
    void "xml still parsed if contributors does not exist"() {
        def values = DoiService.parseXml(crossRefXmlWithNoContributors)
        assert !values.containsKey("givenName")
        assert !values.containsKey("surName")
    }

    @Test
    void "test handling sax error"() {
        def result = "<xml>some xml</xml>"
        def stats = new DoiService.DoiStats()
        def doi = new EzDoi()

        service.handleSaxError(result, stats, doi, new Exception("oops"))
        assert stats.unresolved == 1
        assert doi.error
        assert !doi.resolvableDoi
    }

    void runBadInput(String userName, String password, String doi) {
        try {
            DoiService.createCrossRefUrl(userName, password, doi)
            assert false: "exception should have occurred"
        } catch (IllegalArgumentException) {

        }
    }

    def validCrossrefXml = """
<crossref_result xmlns="http://www.crossref.org/qrschema/2.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="2.0" xsi:schemaLocation="http://www.crossref.org/qrschema/2.0 http://www.crossref.org/schema/queryResultSchema/crossref_query_output2.0.xsd">
<query_result>
<head>
<doi_batch_id>none</doi_batch_id>
</head>
<body>
<query status="resolved" fl_count="25">
    <doi type="journal_article">10.1111/j.1365-2133.2008.08787.x</doi>
    <issn type="print">00070963</issn>
    <issn type="electronic">13652133</issn>
    <issn type="print">123123</issn>
    <issn type="electronic">123123</issn>
    <isbn type="print">123123</isbn>
    <isbn type="electronic">123123</isbn>
    <isbn type="print">456456</isbn>
    <isbn type="electronic">456456123123</isbn>
    <journal_title>British Journal of Dermatology</journal_title>
    <contributors>
        <contributor sequence="first" contributor_role="author">
        <given_name>K.</given_name>
<surname>Mosterd</surname>
</contributor>
<contributor sequence="additional" contributor_role="author">
<given_name>M.R.T.M.</given_name>
<surname>Thissen</surname>
</contributor>
<contributor sequence="additional" contributor_role="author">
<given_name>P.</given_name>
<surname>Nelemans</surname>
</contributor>
<contributor sequence="additional" contributor_role="author">
<given_name>N.W.J.</given_name>
<surname>Kelleners-Smeets</surname>
</contributor>
<contributor sequence="additional" contributor_role="author">
<given_name>R.L.L.T.</given_name>
<surname>Janssen</surname>
</contributor>
<contributor sequence="additional" contributor_role="author">
<given_name>K.G.M.E.</given_name>
<surname>Broekhof</surname>
</contributor>
<contributor sequence="additional" contributor_role="author">
<given_name>H.A.M.</given_name>
<surname>Neumann</surname>
</contributor>
<contributor sequence="additional" contributor_role="author">
<given_name>P.M.</given_name>
<surname>Steijlen</surname>
</contributor>
<contributor sequence="additional" contributor_role="author">
<given_name>D.I.M.</given_name>
<surname>Kuijpers</surname>
</contributor>
</contributors>
<volume>159</volume>
<issue>4</issue>
<first_page>864</first_page>
<last_page>870</last_page>
<year media_type="print">2008</year>
<year media_type="print">2010</year>
<year media_type="electronic">2011</year>
<year media_type="online">2012</year>
<publication_type>full_text</publication_type>
<article_title>
make sure it has greater than 400 characters
Fractionated 5-aminolaevulinic acid-photodynamic therapy vs. surgical excision in the treatment of nodular basal cell carcinoma: results of a randomized controlled trial
kahsdflkjhasdlfkjhasdlkfjhasldkfjhlkjhasdflkjhasdlfkjhasdlfkjhasdlkfjhasdlfjkhalksjdhflkjahsdflkjhasdlfkjhasldkfhjlkjhasdflkjh
lkjahsdflkjhasdflkjhasdflkjhasdflkjhasdflkjhasdflkjhasdlkfjhasdflkjhasdflkjhasdflkjhasdflkjhasdflkjhasdf
lkjahsdflkjhasdflkjhasdflkjhsdflkjhasdflkjhasdflkjhasdflkjhasdlfkjhasdfljhasdlfkjhasdlfkjhasdflkjhlkjhasdF
LKJHASDFLKJHASLKDFJHLKJHASDFLKJFHJFDHJFDSHJKDFSJKHLDFSHJKLDFSHKLJFSD
</article_title>
</query>
</body>
</query_result>
</crossref_result>



    """

    def crossRefXmlWithNoContributors = """
<crossref_result xmlns="http://www.crossref.org/qrschema/2.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="2.0" xsi:schemaLocation="http://www.crossref.org/qrschema/2.0 http://www.crossref.org/schema/queryResultSchema/crossref_query_output2.0.xsd">
<query_result>
<head>
<doi_batch_id>none</doi_batch_id>
</head>
<body>
<query status="resolved" fl_count="25">
    <doi type="journal_article">10.1111/j.1365-2133.2008.08787.x</doi>
    <issn type="print">00070963</issn>
    <issn type="electronic">13652133</issn>
    <issn type="print">123123</issn>
    <issn type="electronic">123123</issn>
    <isbn type="print">123123</isbn>
    <isbn type="electronic">123123</isbn>
    <isbn type="print">456456</isbn>
    <isbn type="electronic">456456123123</isbn>
    <journal_title>British Journal of Dermatology</journal_title>

<volume>159</volume>
<issue>4</issue>
<first_page>864</first_page>
<last_page>870</last_page>
<year media_type="print">2008</year>
<year media_type="print">2010</year>
<year media_type="electronic">2011</year>
<year media_type="online">2012</year>
<publication_type>full_text</publication_type>
<article_title>
make sure it has greater than 400 characters
Fractionated 5-aminolaevulinic acid-photodynamic therapy vs. surgical excision in the treatment of nodular basal cell carcinoma: results of a randomized controlled trial
kahsdflkjhasdlfkjhasdlkfjhasldkfjhlkjhasdflkjhasdlfkjhasdlfkjhasdlkfjhasdlfjkhalksjdhflkjahsdflkjhasdlfkjhasldkfhjlkjhasdflkjh
lkjahsdflkjhasdflkjhasdflkjhasdflkjhasdflkjhasdflkjhasdlkfjhasdflkjhasdflkjhasdflkjhasdflkjhasdflkjhasdf
lkjahsdflkjhasdflkjhasdflkjhsdflkjhasdflkjhasdflkjhasdflkjhasdlfkjhasdfljhasdlfkjhasdlfkjhasdflkjhlkjhasdF
LKJHASDFLKJHASLKDFJHLKJHASDFLKJFHJFDHJFDSHJKDFSJKHLDFSHJKLDFSHKLJFSD
</article_title>
</query>
</body>
</query_result>
</crossref_result>

"""
}
