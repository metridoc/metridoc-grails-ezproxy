package metridoc.ezproxy



import grails.test.mixin.*
import org.junit.*
import org.springframework.core.io.ClassPathResource

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
