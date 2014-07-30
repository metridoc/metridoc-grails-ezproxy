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
import org.jasypt.util.text.BasicTextEncryptor
import org.junit.Test

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 1/10/13
 * Time: 4:39 PM
 * To change this template use File | Settings | File Templates.
 */
@Mock([EzParserProperties.class])
class EzParserPropertiesTests {

    @Test
    void "test that instance is correct"() {
        assert EzParserProperties.instance()
        assert 1 == EzParserProperties.list().size()
    }

    @Test
    void "test updating the password and storing it encrypted"() {
        def password = "somePassword"
        EzParserProperties.updatePassword(password)
        def crossRefPassword = EzParserProperties.instance().crossRefPassword
        assert "somePassword" != crossRefPassword
        def encryptor = new BasicTextEncryptor()
        encryptor.password = EzParserProperties.instance().crossRefEncryptionKey
        assert "somePassword" == encryptor.decrypt(crossRefPassword)
    }
}
