/*
  *Copyright 2013 Trustees of the University of Pennsylvania. Licensed under the
  *	Educational Community License, Version 2.0 (the "License"); you may
  *	not use this file except in compliance with the License. You may
  *	obtain a copy of the License at
  *
  *http://www.osedu.org/licenses/ECL-2.0
  *
  *	Unless required by applicable law or agreed to in writing,
  *	software distributed under the License is distributed on an "AS IS"
  *	BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
  *	or implied. See the License for the specific language governing
  *	permissions and limitations under the License.  */

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
