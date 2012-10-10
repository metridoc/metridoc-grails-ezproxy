package metridoc.ezproxy

class EzLog {

    String ipAddress
    String city
    String state
    String country
    String patronId
    Date proxyDate
    String httpMethod
    String url
    Integer httpStatus
    Integer fileSize
    String refUrl
    /**
     * this are calculated by the workflow
     */
    String hostName
    /**
     * this are calculated by the workflow
     */
    Integer lineNum
    String agent
    String ezproxyId
    String fileName

    static mapping = {
        datasources(["ezproxy", "DEFAULT"])
        refUrl sqlType: "text"
        url sqlType: "text"
        agent sqlType: "text"
    }
}
