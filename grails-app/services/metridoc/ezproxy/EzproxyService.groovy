package metridoc.ezproxy

class EzproxyService {

    def grailsApplication
    def parserText
    def parserObject
    static final PARSER_PROPERTY = "metridoc.ezproxy.parser"
    static final RAW_DATA_PROPERTY = "metridoc.ezproxy.rawData"

    def getRawSampleData() {
        def propertyDomain = EzProperties.find {
            propertyName == RAW_DATA_PROPERTY
        }

        if(propertyDomain) {
            return propertyDomain.propertyValue
        }

        return null
    }

    def getRawParser() {
        def propertyDomain = EzProperties.find {
            propertyName == PARSER_PROPERTY
        }

        if(propertyDomain) {
            return propertyDomain.propertyValue
        }

        return null
    }

    def updateParser(parserText) {
        def propertyDomain = EzProperties.find {
            propertyName == PARSER_PROPERTY
        }

        if(propertyDomain) {
            propertyDomain.propertyValue = parserText
        } else {
            new EzProperties(propertyName: PARSER_PROPERTY, propertyValue: parserText).save()
        }
    }

    //TODO: use dry to clean this up
    def updateSampleData(rawData) {
        def propertyDomain = EzProperties.find {
            propertyName == RAW_DATA_PROPERTY
        }

        if(propertyDomain) {
            propertyDomain.propertyValue = rawData
        } else {
            new EzProperties(propertyName: RAW_DATA_PROPERTY, propertyValue: rawData).save()
        }
    }

    def hasParser() {
        EzProperties.find {
            propertyName == PARSER_PROPERTY
        } ? true : false
    }

    def hasData() {
        EzProperties.find {
            propertyName == RAW_DATA_PROPERTY
        } ? true : false
    }

    def getParsedData() {

        def parserObject = getParserObject()
        def results = [:]
        results.rows = []
        results.headers = []

        getRawSampleData().eachLine {line, lineNumber ->
            def row = parserObject.parse(line, lineNumber+1, "ezproxy.file") as Map
            if (!results.headers) {
                results.headers = row.keySet()
            }
            results.rows << row.values()
        }

        return results
    }

    def getParserObject() {

        def storedText = EzProperties.find {
            propertyName == PARSER_PROPERTY
        }.propertyValue

        if(shouldRebuildParser(storedText)) {
            parserText = storedText
            rebuildParser(storedText)
        }

        return parserObject
    }

    def rebuildParser(String parserText) {
        def parserTemplate = grailsApplication.mergedConfig.metridoc.ezproxy.ezproxyParserTemplate
        parserObject = buildParser(parserText, parserTemplate)
    }

    def buildParser(String parserText, Closure parserTemplate) {
        def code = parserTemplate.call(parserText)
        def classLoader = Thread.currentThread().contextClassLoader
        new GroovyClassLoader(classLoader).parseClass(code).newInstance()
    }

    def shouldRebuildParser(String storedparser) {
        parserText != storedparser
    }


}
