package metridoc.ezproxy

import grails.test.mixin.TestFor
import org.apache.commons.lang.StringUtils
import org.junit.Test

@TestFor(EzDoi)
class EzDoiTests {

    @Test
    void "default invalid record should be valid if inserted twice"() {
        new EzDoi().createDefaultInvalidRecord().save()
        new EzDoi().createDefaultInvalidRecord().save()
    }

    @Test
    void "if the record has a url then has url is true, otherwise false"() {
        hasUrlTest([:], false)
        hasUrlTest([url: StringUtils.EMPTY], false)
        hasUrlTest([url: "   \n  "], false)
        hasUrlTest([url: "-"], false)
        hasUrlTest([url: "   -  "], false)
        hasUrlTest([url: "http://foo.com10."], true)
    }

    @Test
    void "has doi returns true if a doi exists in url, false otherwise, input is a valid non empty string"() {
        hasDoiTest([url: "http://foo.com"], false)
        hasDoiTest([url: "http://foo.com10.1234"], false)
        hasDoiTest([url: "http://foo.com10.1234/"], true)
    }

    @Test
    void "if a url returns true if the url is actually a url, it is assumed that url is a string that contains the doi pattern"() {
        urlIsAUrlTest([url: "http://foo.com10.1234/"], true)
        urlIsAUrlTest([url: "foo.com10."], false)
    }

    @Test
    void "invalid record should be valid"() {
        def invalidRecord = new EzDoi().createDefaultInvalidRecord()
        invalidRecord.validate()
        assert invalidRecord.errors.allErrors.size() == 0
    }

    @Test
    void "test doi extractions"() {
        assert '10.1021/jo0601009' == EzDoi.extractDoi('http://pubs.acs.org:80/doi/full/10.1021/jo0601009')
        assert '10.1002/(ISSN)1531-4995' == EzDoi.extractDoi('http://onlinelibrary.wiley.com:80?doi=10.1002%2F%28ISSN%291531-4995&simpleSearchError=Please+remove')
        assert null == EzDoi.extractDoi('http://foo.com')
        //odd ball cases
        assert '10.1038/nrg2628' == EzDoi.extractDoi('http://www.ncbi.nlm.nih.gov:80/stat?link_href=http%3A%2F%2Fproxy.library.upenn.edu%3A2102%2F10.1038%2Fnrg2628&maxscroll_x=0&maxscroll_y=0')
        assert '10.1234.10/123' == EzDoi.extractDoi('https://www.foo.com/10.1234.10/123/pdf')
        assert '10.1000/456#789' == EzDoi.extractDoi('http://dx.doi.org/10.1000/456%23789')
        assert '10.1000/456#789' == EzDoi.extractDoi('http://dx.doi.org?doi=10.1000/456%2523789')
    }

    @Test
    void "test aready processed"() {
        def cache = [:]
        assert false == new EzDoi().alreadyProcessed(cache, "foo", "doi", "10.1234")
        def ezDoi =  new EzDoi().createDefaultInvalidRecord()
        ezDoi.doi = "10.1212"
        ezDoi.ezproxyId = "foo"
        ezDoi.save(failOnError: true)
        assert new EzDoi().alreadyProcessed(cache, "foo", "doi", "10.1212")
    }

    static void hasUrlTest(Map record, boolean expected) {
        doTest("hasUrl", record, expected)
    }

    static void hasDoiTest(Map record, boolean expected) {
        doTest("hasDoi", record, expected)
    }

    static void urlIsAUrlTest(Map record, boolean expected) {
        doTest("urlIsAUrl", record, expected)
        record.ezproxyId = "foo"
        assert expected == new EzDoi().accept(record)
    }

    static void doTest(String method, Map record, boolean expected) {
        assert expected == EzDoi."$method"(record)
    }
}
