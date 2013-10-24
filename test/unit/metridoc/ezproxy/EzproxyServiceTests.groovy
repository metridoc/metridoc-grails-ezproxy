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

@TestFor(EzproxyService)
@Mock([EzproxyHosts, EzFileMetaData, EzParserProperties])
class EzproxyServiceTests {

    @Test
    void "if the hash is incorrect, the file data is deleted"() {
        def record = new EzproxyHosts().createTestRecord()
        def file = File.createTempFile("foo", "bar")
        service.grailsApplication = [
                domainClasses: [EzproxyHosts]
        ]

        record.fileName = file.name
        record.save(failOnError: true)

        record = new EzproxyHosts().createTestRecord()
        record.fileName = "bar"
        record.save(failOnError: true)

        assert 2 == EzproxyHosts.count()
        new EzFileMetaData(fileName: file.name, sha256: "kashjdf").save(failOnError: true)
        assert 1 == EzFileMetaData.count()

        file.write("some random text")
        service.deleteDataForFileIfHashNotCorrect(file)

        assert 1 == EzproxyHosts.count()
        assert 0 == EzFileMetaData.count()
    }
}
