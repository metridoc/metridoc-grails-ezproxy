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

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.junit.Test

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 1/8/13
 * Time: 1:52 PM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(EzproxyAdminController)
@Mock([EzproxyHosts, EzFileMetaData, EzSkip])
class EzproxyAdminControllerTests {

    @Test
    void "file data and ezhost data should be deleted when deleteFileData is called"() {
        controller.params.id = "foo"
        def grailsApplication = [
                domainClasses: [EzproxyHosts, EzSkip]
        ]
        controller.ezproxyService = new EzproxyService(grailsApplication: grailsApplication)

        def host = new EzproxyHosts().createTestRecord()
        host.fileName = "foo"
        host.save(failOnError: true)

        host = new EzproxyHosts().createTestRecord()
        host.fileName = "bar"
        host.save(failOnError: true)

        def skip = new EzSkip()
        skip.type = EzproxyHosts.name
        skip.fileName = "foo"
        skip.lineNumber = 5
        skip.error = "blah blah blah"
        skip.save(failOnError: true)

        new EzFileMetaData(fileName: "foo", sha256: "blam").save(failOnError: true)
        new EzFileMetaData(fileName: "bar", sha256: "blam").save(failOnError: true)

        assert 1 == EzSkip.count()
        assert 2 == EzproxyHosts.count()
        assert 2 == EzFileMetaData.count()

        controller.deleteFileData()
        assert 1 == EzproxyHosts.count()
        assert 1 == EzFileMetaData.count()
        assert 0 == EzSkip.count()

        assert null == EzproxyHosts.findByFileName("foo")
        assert null == EzFileMetaData.findByFileName("foo")

        assert null != EzproxyHosts.findByFileName("bar")
        assert null != EzFileMetaData.findByFileName("bar")
    }


}
