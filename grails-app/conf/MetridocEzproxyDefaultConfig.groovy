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
                        result.pmid = false
                        result.doi = false
                        def url = result.url ? result.url : ""
                        def refUrl = result.refUrl ? result.refUrl : ""

                        if(url.contains("pmid") || refUrl.contains("pmid")) {
                            result.pmid = true
                        }

                        if(url.contains("doi") || refUrl.contains("doi")) {
                            result.doi = true
                        }

                        result.archive = result.pmid || result.doi
                        return result
                    }
            }
        """
        }
    }
}