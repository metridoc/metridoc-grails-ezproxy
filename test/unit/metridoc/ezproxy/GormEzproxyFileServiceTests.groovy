package metridoc.ezproxy

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.junit.Test

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(GormEzproxyFileService)
@Mock([EzproxyHosts, EzFileMetaData, EzDoi])
class GormEzproxyFileServiceTests {

    @Test
    void "test full blown implementation"() {
        def file = File.createTempFile("ezproxy", "log")

        file.write(EzproxyUtils.DEFAULT_LOG_DATA, "utf-8")
        def ezproxyService = new EzproxyService()
        def parser = ezproxyService.buildParser(EzproxyUtils.DEFAULT_PARSER, EzproxyUtils.DEFAULT_PARSER_TEMPLATE)
        service.grailsApplication = [
                domainClasses: [
                        [newInstance: { new EzproxyHosts() }],
                        [newInstance: { new EzDoi() }]
                ]
        ]
        service.processFile(file, parser)
        def ezproxyIds = ["96CV6QQh0Mclz5Z", "vO07NtNOHwIciIH"] as Set
        def hosts = EzproxyHosts.list()
        assert 2 == hosts.size()
        hosts.each {
            assert ezproxyIds.remove(it.ezproxyId)
            assert it.valid
            assert null == it.validationError
        }

        assert 1 == EzDoi.count()
        assert EzDoi.list().get(0).valid
        assert ezproxyIds.isEmpty()

        //do it again, result should be the same since everything has already been processed
        file = File.createTempFile("ezproxy", "log")
        file.write(EzproxyUtils.DEFAULT_LOG_DATA, "utf-8")
        service.processFile(file, parser)
        hosts = EzproxyHosts.list()
        assert 2 == hosts.size()
        assert 1 == EzDoi.count()
        assert EzDoi.list().get(0).valid
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
