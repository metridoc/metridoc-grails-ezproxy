package metridoc.ezproxy

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 11/20/12
 * Time: 3:20 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class EzproxyBase<T extends EzproxyBase> {
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



    abstract void loadValues(Map record)
    abstract def createDefaultInvalidRecord()
    abstract boolean accept(Map record)
}
