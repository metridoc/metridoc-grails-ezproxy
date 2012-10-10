package metridoc.ezproxy



import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(EzLog)
class EzLogTests {

    @Test
    void "ip address cannot be null"() {
        nullErrorCheck(logForConstraints, "ipAddress", "111.111.111.111")
    }

    @Test
    void "fileName cannot be null"() {
        nullErrorCheck(logForConstraints, "fileName", "ezproxy.log")
    }

    @Test
    void "patronId cannot be null"() {
        nullErrorCheck(logForConstraints, "patronId", "bar")
    }

    @Test
    void "proxyDate cannot be null"() {
        nullErrorCheck(logForConstraints, "proxyDate", new Date())
    }

    @Test
    void "httpMethod cannot be null"() {
        nullErrorCheck(logForConstraints, "httpMethod", "GET")
    }

    @Test
    void "url cannot be null"() {
        nullErrorCheck(logForConstraints, "url", "http://foo.org")
    }

    @Test
    void "refUrl cannot be null"() {
        nullErrorCheck(logForConstraints, "refUrl", "http://foo.org")
    }

    @Test
    void "httpStatus cannot be null"() {
        nullErrorCheck(logForConstraints, "httpStatus", 200)
    }

    @Test
    void "agent cannot be null"() {
        nullErrorCheck(logForConstraints, "agent", "chrome")
    }

    @Test
    void "ezproxyId cannot be null"() {
        nullErrorCheck(logForConstraints, "ezproxyId", "blah")
    }

    void nullErrorCheck(instance, field, value) {
        assert !instance.validate()
        assert "nullable" == instance.errors[field]
        def values = [:]
        values.put(field, value)

        assert null == getLogForConstraints(values).errors[field]
    }

    def getLogForConstraints() {
        def log = new EzLog()
        mockForConstraintsTests(EzLog, [log])

        return log
    }

    def getLogForConstraints(values) {
        def log = new EzLog(values)
        mockForConstraintsTests(EzLog, [log])

        return log
    }
}
