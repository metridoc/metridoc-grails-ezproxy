package metridoc.ezproxy

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.junit.Before
import org.junit.Test

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(EzproxyService)
@Mock([EzProperties, EzproxyHosts, EzFileMetaData])
class EzproxyServiceTests {

    @Test
    void "if the hash is incorrect, the file data is deleted"() {
        def record = new EzproxyHosts().createDefaultInvalidRecord()
        def file = File.createTempFile("foo", "bar")
        service.grailsApplication = [
                domainClasses: [EzproxyHosts]
        ]

        record.fileName = file.name
        record.save(failOnError: true)

        record = new EzproxyHosts().createDefaultInvalidRecord()
        record.fileName = "bar"
        record.save(failOnError: true)

        assert 2 == EzproxyHosts.count()
        new EzFileMetaData(fileName: file.name, sha256: "kashjdf").save(failOnError: true)
        assert 1 == EzFileMetaData.count()

        file.write("some random text")
        service.deleteDataForFileIfHashNotCorrect(file)

        assert 1 == EzproxyHosts.count()
        assert 0 == EzFileMetaData.count()
    }

    @Before
    void "create a mock quartzScheduler"() {
        service.quartzScheduler = [
                resumeJob: { jobKey -> /* do nothing */ }
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
        def template = { parserText ->
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
    void "activating the job will toggle the the ezproperty to true"() {
        assert !service.isJobActive()
        service.activateEzproxyJob()
        assert service.isJobActive()
    }
}
