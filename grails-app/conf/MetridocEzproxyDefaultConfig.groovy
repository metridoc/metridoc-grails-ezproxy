import org.apache.commons.lang.SystemUtils

metridoc {
    ezproxy {
        directory = "${SystemUtils.USER_HOME}/.metridoc/files/ezproxy"
        encoding = "utf-8"
        ezproxyParserTemplate = {parserText ->
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
    }
}