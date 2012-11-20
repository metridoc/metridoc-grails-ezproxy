package metridoc.ezproxy



import grails.test.mixin.*
import org.junit.*
import java.text.SimpleDateFormat

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(EzproxyHostsFileService)
@Mock(EzproxyHosts)
class EzproxyHostsFileServiceTests {

    @Test
    void "transform record creates a host name for the incoming url"() {
        def transformedRecord = service.transformRecord([url:"http://blah.blam.com/foo/bar"])
        assert "blah.blam.com" == transformedRecord.urlHost
    }

    @Test
    void "urlHost is not added if there is a malformed url exception"() {
        def transformedRecord = service.transformRecord([url:"blah.blam.com/foo/bar"])
        assert !transformedRecord.containsKey("urlHost")
    }

    @Test
    void "urlHost is not added if url does not exist in record to transform"() {
        def transformedRecord = service.transformRecord([:])
        assert !transformedRecord.containsKey("urlHost")
    }

    @Test
    void "one to one paramaters are set to null if they don't exist in record to transform"() {
        def transformedRecord = service.transformRecord([:])

        EzproxyHostsFileService.ONE_TO_ONE_PROPERTIES.each {
            assert transformedRecord.containsKey(it)
            assert transformedRecord.get(it) == null
        }
    }

    @Test
    void "one to one properties are carried over from the record to transform" () {
        def transformedRecord = service.transformRecord([patron_id:"foo"])
        assert "foo" == transformedRecord.patron_id
    }

    @Test
    void "no date parameters are added IF proxy date is null"() {
        def transformedRecord = service.transformRecord([:])
        assert !transformedRecord.containsKey("proxyMonth")
        assert !transformedRecord.containsKey("proxyDay")
        assert !transformedRecord.containsKey("proxyYear")
    }

    @Test
    void "year, month and day are added if proxy date is available"() {
        def date = new SimpleDateFormat("yyyy-MM-dd").parse("2000-01-01")
        def transformedRecord = service.transformRecord([proxyDate:date])
        assert 1 == transformedRecord.proxyDay
        assert 1 == transformedRecord.proxyMonth
        assert 2000 == transformedRecord.proxyYear
    }

    @Test
    void "by default EzproxyHosts is not valid"() {
        def hosts = new EzproxyHosts()
        hosts.validate()
        assert 7 == hosts.errors.errorCount
    }

    @Test
    void "test default record is valid" () {
        def defaultRecord = service.getNewDefaultRecord()
        defaultRecord.validate()
        assert defaultRecord.proxyDate
        assert !defaultRecord.errors.errorCount
    }

    @Test
    void "test full blown implementation" () {
        def file = File.createTempFile("ezproxy", "log")
        file.deleteOnExit()
        file.write(EzproxyUtils.DEFAULT_LOG_DATA, "utf-8")
        def ezproxyService = new EzproxyService()
        def parser = ezproxyService.buildParser(EzproxyUtils.DEFAULT_PARSER, EzproxyUtils.DEFAULT_PARSER_TEMPLATE)
        service.processFile(file, parser)
        def ezproxyIds = ["96CV6QQh0Mclz5Z", "vO07NtNOHwIciIH"] as Set
        EzproxyHosts.list().each {
            ezproxyIds.remove(it.ezproxyId)
            assert it.valid
            assert null == it.validationError
        }

        assert ezproxyIds.isEmpty()
    }
}
