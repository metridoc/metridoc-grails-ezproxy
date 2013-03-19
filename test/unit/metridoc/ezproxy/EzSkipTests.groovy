package metridoc.ezproxy



import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(EzSkip)
class EzSkipTests {

    @Test
    void "file, lineNumber and type should be unique"() {
        new EzSkip(fileName: "foo", type: "bar", lineNumber: 1, error: "blah").save(failOnError: true)
        assert !new EzSkip(fileName: "foo", type: "bar", lineNumber: 1, error: "blah").validate()
    }


}
