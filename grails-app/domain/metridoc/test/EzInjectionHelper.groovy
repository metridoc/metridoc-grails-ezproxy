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

package metridoc.test

import metridoc.ezproxy.EzproxyBase

/**
 * Class is used for testing if injection works when the ezproxy job does a post process
 */
class EzInjectionHelper extends EzproxyBase {
    def dataSource

    String foo
    int bar

    static transients = ['dataSource']
    static constraints = {
    }

    @Override
    void postProcess(String fileName) {
        assert dataSource: "dataSource should have been injected"
    }
}
