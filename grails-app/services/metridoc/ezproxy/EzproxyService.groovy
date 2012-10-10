package metridoc.ezproxy

import org.apache.commons.lang.StringUtils

class EzproxyService {

    def grailsApplication
    def parserText
    final static PARSER_TEMPLATE = {parserText ->
        """
        class EzproxyParser {
                def parse(line, lineNumber, fileName) {
                    def result = [:] as TreeMap
                    result.lineNumber = lineNumber
                    result.fileName = fileName
                    ${parserText}

                    return result
                }
        }
        """
    }

    def availableFiles() {
        ezproxySampleDirectory.listFiles()
    }

    def getEzproxySampleDirectory() {
        def directory = grailsApplication.mergedConfig.metridoc.ezproxy.directory
        assert directory: "the metridoc.ezproxy.directory property MUST be specified"
        def file = new File(directory)
        createDirectory(file)
        def sampleDirectory = new File("${file.canonicalPath}/samples")
        createDirectory(sampleDirectory)

        return sampleDirectory
    }

    def createDirectory(directory) {
        if (!directory.exists()) {
            assert directory.mkdirs(): "Could not create the ezproxy directory at $directory"
        }
    }

    def getParser() {
        EzProperties.find {
            propertyName == "metridoc.ezproxy.parser"
        }
    }

    def getParsedData() {
        def parserText = parser.propertyValue

        def parserObject = buildParser(parserText)
        def results = [:]
        results.rows = []
        results.headers = []

        ["foo", "blah", "blammo", "blockblammofooaaaaaalkjdalksjadslkjasldkjaslkdjalksjdlakjsdlkjasldkjalksjdlakjsdlkajsdlkjasldkjalskjdlahsdflkjahsdflkjhasdflkhjasdfkljhasdflkjhasdlfkjhasdlkfjhaslkdfhjlkajhdsflkjhasdflkjhasdlfkjhbar"].each {
            def row = parserObject.parse(it, 5, "foobar") as Map
            if (!results.headers) {
                results.headers = row.keySet()
            }
            results.rows << row.values()
        }

        return results
    }

    def getParsedData(File file, int startLine) {

    }

    def buildParser(String parserText) {
        def code = PARSER_TEMPLATE.call(parserText)
        def classLoader = Thread.currentThread().contextClassLoader
        new GroovyClassLoader(classLoader).parseClass(code).newInstance()
    }

    def shouldRebuildParser(String storedparser) {
        parserText != storedparser
    }
}
