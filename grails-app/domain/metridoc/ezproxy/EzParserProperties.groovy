package metridoc.ezproxy

import grails.util.Holders

class EzParserProperties {

    String directory = EzproxyUtils.DEFAULT_FILE_LOCATION
    Boolean jobActivated = false
    String ezproxyParserTemplate = EzproxyUtils.DEFAULT_PARSER_TEMPLATE
    String sampleLog = EzproxyUtils.DEFAULT_LOG_DATA
    String sampleParser = EzproxyUtils.DEFAULT_PARSER
    String encoding = "utf-8"
    private static final EzParserProperties instance;

    static mapping = {

    }

    static constraints = {
        ezproxyParserTemplate(maxSize: Integer.MAX_VALUE)
        sampleParser(maxSize: Integer.MAX_VALUE, nullable: true)
        sampleLog(maxSize: Integer.MAX_VALUE, nullable: true)
        directory(nullable: true)
    }

    synchronized static EzParserProperties instance() {
        int count = EzParserProperties.count()
        if (count == 0) {
            initializeEzParserProperties()
        }
        assert 1 == EzParserProperties.count(): "there should only be one instance of EzParserProperties, but there were $count"
        EzParserProperties.list().get(0)
    }

    private static initializeEzParserProperties() {
        if (Holders.grailsApplication?.mergedConfig) {
            def ezproxyConfig = Holders.grailsApplication.mergedConfig.metridoc.ezproxy
            if (ezproxyConfig) {
                if (EzParserProperties.count() == 0) {
                    new EzParserProperties(
                            sampleParser: ezproxyConfig.ezproxyParserTemplate ?: EzproxyUtils.DEFAULT_PARSER,
                            sampleLog: ezproxyConfig.sampleLog ?: EzproxyUtils.DEFAULT_LOG_DATA,
                            ezproxyParserTemplate: ezproxyConfig.ezproxyParserTemplate ?: EzproxyUtils.DEFAULT_PARSER_TEMPLATE,
                            encoding: ezproxyConfig.encoding ?: "utf-8",
                            directory: ezproxyConfig.directory ?: EzproxyUtils.DEFAULT_FILE_LOCATION
                    ).save(flush: true, failOnError: true)
                }

            } else {
                new EzParserProperties().save(flush: true, failOnError: true)
            }
        } else {
            new EzParserProperties().save(flush: true, failOnError: true)
        }
    }


}
