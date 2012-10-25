package metridoc.ezproxy



import grails.test.mixin.*
import org.junit.*
import org.springframework.core.io.ClassPathResource
import org.springframework.util.ClassUtils

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(EzproxyService)
class EzproxyServiceTests {

    @Test
    void "should only rebuild parser if parser text has changed"() {
        service.parserText = "foo"
        assert service.shouldRebuildParser("bar")
        assert !service.shouldRebuildParser("foo")
    }

    @Test
    void "the parser should take care of doi and pmid events, along with setting the archive flag if the event contains those"() {
        def slurper = new ConfigSlurper()
        def clazz = ClassUtils.forName("MetridocEzproxyDefaultConfig")
        def configObject = slurper.parse(clazz)
        def template = configObject.metridoc.ezproxy.ezproxyParserTemplate
        def result = service.buildParser("", template).parse("foobar", 2, "bar")
        assert result.doi == false
        assert result.pmid == false
        assert result.archive == false

        result = service.buildParser("result.url = 'pmid'", template).parse("foobar", 2, "bar")
        assert result.doi == false
        assert result.pmid == true
        assert result.archive == true

        result = service.buildParser("result.url = 'doi'", template).parse("foobar", 2, "bar")
        assert result.doi == true
        assert result.pmid == false
        assert result.archive == true
    }

    @Test
    void "creating a parser to parse data should be executable"() {
        def template = {parserText ->
            """
            class EzproxyParser {
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
        def parser = service.buildParser("result.foo = line", template);
        def result = parser.parse("foobar", 2, "bar")
        assert "foobar" == result.foo
        assert 2 == result.lineNumber
        assert "bar" == result.fileName
    }


}
