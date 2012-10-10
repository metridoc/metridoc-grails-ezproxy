package metridoc.ezproxy



import grails.test.mixin.*
import org.junit.*

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
        def parser = service.buildParser("result.foo = line");
        def result = parser.parse("foobar", 2, "bar")
        assert "foobar" == result.foo
        assert 2 == result.lineNumber
        assert "bar" == result.fileName
    }


}
