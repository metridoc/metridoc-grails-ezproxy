package metridoc.ezproxy

import grails.util.Holders

class EzProperties {

    Date dateCreated
    String propertyName
    String propertyValue

    static mapping = {
        propertyValue type: "text"
    }

    static constraints = {
        propertyValue(size: 1..Integer.MAX_VALUE)
    }
}
