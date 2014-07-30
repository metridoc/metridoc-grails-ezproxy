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

import grails.test.mixin.TestFor
import org.junit.Test

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 3/19/13
 * Time: 3:38 PM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(EzFileMetaData)
class EzFileMetaDataTests {

    @Test
    void "file name should be unique"() {

        def record = {
            new EzFileMetaData(
                fileName: "blah",
                sha256: "blah")
        }

        record.call().save(failOnError: true)

        def badRecord = record.call()
        assert !badRecord.validate()
        assert "unique" == badRecord.errors.allErrors[0].code
    }
}
