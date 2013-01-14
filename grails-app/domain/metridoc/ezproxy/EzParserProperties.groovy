package metridoc.ezproxy

import org.apache.commons.lang.RandomStringUtils

class EzParserProperties {

    String crossRefEncryptionKey = RandomStringUtils.randomAlphanumeric(100)
    String crossRefUserName = "user"
    String crossRefPassword = "password"
    Boolean storePatronId = false
    String fileFilter = ".*"
    String directory = EzproxyUtils.DEFAULT_FILE_LOCATION
    Boolean jobActivated = false
    String sampleLog = EzproxyUtils.DEFAULT_LOG_DATA
    String sampleParser = EzproxyUtils.DEFAULT_PARSER
    String encoding = "utf-8"
    private static final EzParserProperties instance;

    static mapping = {

    }

    static constraints = {
        sampleParser(maxSize: Integer.MAX_VALUE, nullable: true)
        sampleLog(maxSize: Integer.MAX_VALUE, nullable: true)
        directory(nullable: true)
        fileFilter(nullable: true)
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
        new EzParserProperties().save(flush: true, failOnError: true)
    }


}
