package metridoc.ezproxy

import org.apache.commons.lang.math.RandomUtils

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 11/20/12
 * Time: 3:20 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class EzproxyBase<T extends EzproxyBase> {

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

    T createDefaultInvalidRecord() {
        (T) this.class.newInstance(
                ipAddress: "ERROR",
                urlHost: "ERROR-${RandomUtils.nextInt(100000)}",
                url: "ERROR",
                valid: false,
                lineNumber: lineNumber ?: -1,
                fileName: fileName ?: "ERROR",
                ezproxyId: "ERROR",
                proxyDate: new Date()
        )
    }

    abstract boolean accept(Map record)

    void loadValues(Map record) {
        loadValues(record, DEFAULT_ONE_TO_ONE_PROPERTIES)
    }

    void loadValues(Map record, List oneToOne) {
        oneToOne.each {
            this."${it}" = record[it]
        }

        addDateParameters(record)
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
}
