package metridoc.ezproxy

import grails.test.mixin.TestFor
import org.junit.Test

import java.text.SimpleDateFormat

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 11/27/12
 * Time: 9:22 AM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(EzproxyHosts)
class EzproxyHostsTests {

    def ezproxyHosts = new EzproxyHosts()

    @Test
    void "the default invalid record should be valid"() {
        def invalidRecord = ezproxyHosts.createTestRecord()
        invalidRecord.validate()
        assert invalidRecord.errors.allErrors.size() == 0
    }

    @Test
    void "transform record creates a host name for the incoming url"() {
        ezproxyHosts.loadValues([url: "http://blah.blam.com/foo/bar"])
        assert "blah.blam.com" == ezproxyHosts.urlHost
    }

    @Test
    void "one to one paramaters are set to null if they don't exist in record to domain object"() {
        ezproxyHosts.loadValues([:])

        EzproxyHosts.DEFAULT_ONE_TO_ONE_PROPERTIES.each {
            assert ezproxyHosts."${it}" == null
        }
    }

    @Test
    void "urlHost is not added if there is a malformed url exception"() {
        ezproxyHosts.loadValues([url: "blah.blam.com/foo/bar"])
        assert !ezproxyHosts.urlHost
    }

    @Test
    void "one to one properties are carried over from the record to domain object"() {
        ezproxyHosts.loadValues([patronId: "foo"])
        assert "foo" == ezproxyHosts.patronId
    }

    @Test
    void "no date parameters are added IF proxy date is null"() {
        ezproxyHosts.loadValues([:])
        assert !ezproxyHosts.proxyMonth
        assert !ezproxyHosts.proxyDay
        assert !ezproxyHosts.proxyYear
    }

    @Test
    void "year, month and day are added if proxy date is available"() {
        def date = new SimpleDateFormat("yyyy-MM-dd").parse("2000-01-01")
        ezproxyHosts.loadValues([proxyDate: date])
        assert 1 == ezproxyHosts.proxyDay
        assert 1 == ezproxyHosts.proxyMonth
        assert 2000 == ezproxyHosts.proxyYear
    }

    @Test
    void "urlHost is not added if url does not exist in record to transform"() {
        ezproxyHosts.loadValues([:])
        assert !ezproxyHosts.urlHost
    }

    @Test
    void "test already processed when item is NOT in cache but in the database"() {
        def record = new EzproxyHosts().createTestRecord()
        record.ezproxyId = "foo"
        record.urlHost = "http://foo.com"
        record.save()

        assert 1 == EzproxyHosts.count()
        def cache = [:]
        assert new EzproxyHosts().alreadyProcessed(cache, "foo", "urlHost", "http://foo.com")
        assert 1 == cache.size()
    }

    @Test
    void "test toString"() {
        def record = new EzproxyHosts().createTestRecord().toString()
        assert record.contains("ezproxyId")
        assert record.contains("urlHost")
    }

    @Test
    void "test that the default error record retains the ezproxy id"() {
        def record = new EzproxyHosts(ezproxyId: "foo").createTestRecord()
        assert "foo" == record.ezproxyId
    }
}
