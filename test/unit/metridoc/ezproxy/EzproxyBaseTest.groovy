package metridoc.ezproxy

import org.junit.Test

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 1/9/13
 * Time: 2:30 PM
 * To change this template use File | Settings | File Templates.
 */
class EzproxyBaseTest {

    @Test
    void "test cache updates"() {
        Map<String, Set<String>> cache = [:]
        assert EzproxyBase.notInCache(cache, "foo", "bar")
        assert 1 == cache.size()
        assert cache.containsKey("foo")
        assert "bar" == cache.foo.iterator().next()
        assert 1 == cache.foo.size()
    }
}
