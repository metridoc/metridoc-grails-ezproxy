package metridoc.ezproxy



import grails.test.mixin.*
import org.junit.*
import java.text.SimpleDateFormat

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(GormEzproxyFileService)
@Mock(EzproxyHosts)
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
        EzproxyHosts.list().each {
            assert ezproxyIds.remove(it.ezproxyId)
            assert it.valid
            assert null == it.validationError
        }

        assert ezproxyIds.isEmpty()
    }
}
