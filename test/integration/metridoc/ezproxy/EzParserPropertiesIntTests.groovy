package metridoc.ezproxy

import static org.junit.Assert.*
import org.junit.*

class EzParserPropertiesIntTests {

    @Test
    void "test that instance is correct"() {
        assert EzParserProperties.instance()
        assert 1 == EzParserProperties.list().size()
    }
}
