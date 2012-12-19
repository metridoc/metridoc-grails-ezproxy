package metridoc.ezproxy



import grails.test.mixin.*
import org.junit.*
import org.springframework.core.io.ClassPathResource
import org.springframework.util.ClassUtils
import org.quartz.core.QuartzScheduler

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(EzproxyService)
@Mock(EzProperties)
class EzproxyServiceTests {

    @Before
    void "create a mock quartzScheduler"() {
        service.quartzScheduler = [
            resumeJob: {jobKey-> /* do nothing */}
        ]
    }

    @Test
    void "should only rebuild parser if parser text has changed"() {
        service.parserText = "foo"
        assert service.shouldRebuildParser("bar")
        assert !service.shouldRebuildParser("foo")
    }

    @Test
    void "creating a parser to parse data should be executable"() {
        def template = {parserText ->
            """
            class EzproxyParser {
                    def applicationContext
                    def parse(line, lineNumber, fileName) {
                        def result = [:] as TreeMap
                        result.lineNumber = lineNumber
                        result.fileName = fileName
                        ${parserText}

                        return result
                    }
            }
        """
        }

        service.applicationContext = "placeholder"
        def parser = service.buildParser("result.foo = line", template);
        assert parser.applicationContext == "placeholder"
        def result = parser.parse("foobar", 2, "bar")
        assert "foobar" == result.foo
        assert 2 == result.lineNumber
        assert "bar" == result.fileName
    }

    @Test
    void "a property entry should be added when checking if the job is activated or not and the property does not currently exist"() {
        assert !service.isJobActive()
        def activeProperty = EzProperties.findByPropertyName(EzproxyService.JOB_ACTIVE_PROPERTY_NAME)
        assert activeProperty.propertyValue == "false"
    }

    @Test
    void "activating the job will toggle the the ezproperty to true" () {
        assert !service.isJobActive()
        service.activateEzproxyJob()
        assert service.isJobActive()
    }
}
