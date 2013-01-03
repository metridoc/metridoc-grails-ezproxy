package metridoc.ezproxy



import grails.test.mixin.*
import org.junit.*
import java.text.SimpleDateFormat

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(GormEzproxyFileService)
@Mock([EzproxyHosts, EzFileMetaData])
class GormEzproxyFileServiceTests {

    @Test
    void "test full blown implementation" () {
        def file = File.createTempFile("ezproxy", "log")
        file.deleteOnExit()
        file.write(EzproxyUtils.DEFAULT_LOG_DATA, "utf-8")
        def ezproxyService = new EzproxyService()
        def parser = ezproxyService.buildParser(EzproxyUtils.DEFAULT_PARSER, EzproxyUtils.DEFAULT_PARSER_TEMPLATE)
        service.grailsApplication = [
                domainClasses: [
                    [clazz:EzproxyHosts]
                ]
        ]
        service.processFile(file, parser)
        def ezproxyIds = ["96CV6QQh0Mclz5Z", "vO07NtNOHwIciIH"] as Set
        def hosts = EzproxyHosts.list()
        hosts.each {
            assert ezproxyIds.remove(it.ezproxyId)
            assert it.valid
            assert null == it.validationError
        }

        assert ezproxyIds.isEmpty()
    }

    @Test
    void "encoding is pulled from config from property metridoc_ezproxy_encoding, utf-8 by default"() {
        def grailsApplication = [
                mergedConfig: new ConfigObject()
        ]
        service.grailsApplication = grailsApplication
        assert "utf-8" == service.encoding: "default encoding should be utf-8"
        grailsApplication = [
                mergedConfig: [
                        metridoc: [
                                ezproxy: [
                                    encoding: "foo"
                                ]
                        ]
                ]
        ]

        service.grailsApplication = grailsApplication
        assert "foo" == service.encoding
    }
}
