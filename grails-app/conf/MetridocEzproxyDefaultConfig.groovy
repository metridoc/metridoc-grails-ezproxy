import org.apache.commons.lang.SystemUtils
import metridoc.ezproxy.EzproxyUtils

metridoc {
    ezproxy {
        directory = "${SystemUtils.USER_HOME}/.metridoc/files/ezproxy"
        encoding = "utf-8"
        ezproxyParserTemplate = EzproxyUtils.DEFAULT_PARSER_TEMPLATE
        sampleLog = EzproxyUtils.DEFAULT_LOG_DATA
        sampleParser= EzproxyUtils.DEFAULT_PARSER
    }
}


