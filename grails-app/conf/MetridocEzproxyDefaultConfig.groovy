import org.apache.commons.lang.SystemUtils

metridoc {
    ezproxy {
        directory = "${SystemUtils.USER_HOME}/.metridoc/files/ezproxy"
        encoding = "utf-8"
        ezproxyParserTemplate = {parserText ->
        """
            import java.text.SimpleDateFormat

            class EzproxyParser {
                    def parse(String line, int lineNumber, String fileName) {
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