package metridoc.ezproxy

import grails.test.mixin.Mock
import org.junit.Test

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 1/10/13
 * Time: 4:39 PM
 * To change this template use File | Settings | File Templates.
 */
@Mock(EzParserProperties)
class EzParserPropertiesTests {

    @Test
    void "test that instance is correct"() {
        assert EzParserProperties.instance()
        assert 1 == EzParserProperties.list().size()
    }
}
