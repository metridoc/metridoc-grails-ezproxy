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

import org.apache.commons.lang.math.RandomUtils
import org.slf4j.LoggerFactory

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 11/20/12
 * Time: 3:20 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class EzproxyBase<T extends EzproxyBase> {
    static final log = LoggerFactory.getLogger(EzproxyBase)
    static final DEFAULT_ONE_TO_ONE_PROPERTIES = ["patronId", "ipAddress", "lineNumber", "state", "country", "city", "ezproxyId", "proxyDate", "fileName", "url", "refUrl"]
    Date dateCreated
    Date proxyDate
    int proxyMonth
    int proxyYear
    int proxyDay
    String ipAddress
    String fileName
    Integer lineNumber
    String patronId
    String state
    String country
    String city
    String urlHost
    String refUrlHost
    String url
    String refUrl
    String ezproxyId
    Boolean valid = true
    Boolean error = false
    String validationError
    String dept
    String organization
    String rank
    public static final String APACHE_NULL = "-"

    /**
     *
     * @return a valid test record that is typically used for a unit / integration test
     */
    T createTestRecord() {
        (T) this.class.newInstance(
                ipAddress: "123.123.123",
                urlHost: "www.foo.com-${RandomUtils.nextInt(100000)}",
                url: "www.foo.com",
                valid: false,
                lineNumber: lineNumber ?: -1,
                fileName: fileName ?: "foo",
                ezproxyId: (ezproxyId != null && ezproxyId.length() < 50) ? ezproxyId : "123abc",
                proxyDate: new Date()
        )
    }

    abstract void postProcess(String fileName)

    boolean accept(Map record) {
        String ezproxyId = record.ezproxyId
        def hasEzproxyId = ezproxyId != null && ezproxyId.trim() != "-" && ezproxyId.trim().size() > 1
        def hasUrl
        //noinspection GroovyUnusedCatchParameter
        try {
            //noinspection GroovyResultOfObjectAllocationIgnored
            new URL(record.url as String)
            hasUrl = true
        } catch (MalformedURLException ex) {
            //do nothing
        }

        return hasUrl && hasEzproxyId
    }

    void loadValues(Map record) {
        loadValues(record, DEFAULT_ONE_TO_ONE_PROPERTIES)
    }

    void loadValues(Map record, List oneToOne) {
        oneToOne.each {
            this."${it}" = record[it]
        }

        addDateParameters(record)
        //noinspection GroovyUnusedCatchParameter
        try {
            urlHost = new URL(url).host
            refUrlHost = new URL(refUrl).host
        } catch (MalformedURLException e) {
            //do nothing, just let urlHost be null
        }
    }

    protected void addDateParameters(Map<String, Object> record) {
        if (record.proxyDate) {
            assert record.proxyDate instanceof Date: "proxy date must be of type Date"
            Date proxyDate = record.proxyDate as Date
            if (proxyDate) {
                def calendar = new GregorianCalendar()
                calendar.setTime(proxyDate)
                proxyMonth = calendar.get(Calendar.MONTH) + 1
                proxyYear = calendar.get(Calendar.YEAR)
                proxyDay = calendar.get(Calendar.DAY_OF_MONTH)
            }
        }
    }

    protected boolean alreadyProcessed(Map<String, Set<String>> cache, String ezproxyId, String itemName,
                                       String item) {
        assert item != null && item.trim() != APACHE_NULL: "the index item must not be null"
        if (notInCache(cache, ezproxyId, item)) {
            def query = "findByEzproxyIdAnd${itemName.capitalize()}"
            try {
                def storedItem = this.getClass()."${query}"(ezproxyId, item)
                return storedItem != null
            } catch (Exception e) {
                log.warn("Could not run query ${query} due to an unexpected exception")
                throw e
            }
        }

        return true
    }

    protected static boolean notInCache(Map<String, Set<String>> cache, String ezproxyId,
                                        String item) {

        boolean result
        def processedItems = cache[ezproxyId]
        if (processedItems) {
            if (processedItems.contains(item)) {
                result = false
            } else {
                result = true
                processedItems.add(item)
            }
        } else {
            processedItems = [] as Set<String>
            cache[ezproxyId] = processedItems
            processedItems.add(item)
            result = true
        }

        return result
    }
}
