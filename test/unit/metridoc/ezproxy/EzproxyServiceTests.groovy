package metridoc.ezproxy

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.junit.Test

@TestFor(EzproxyService)
@Mock([EzproxyHosts, EzFileMetaData, EzParserProperties])
class EzproxyServiceTests {

    @Test
    void "if the hash is incorrect, the file data is deleted"() {
        def record = new EzproxyHosts().createTestRecord()
        def file = File.createTempFile("foo", "bar")
        service.grailsApplication = [
                domainClasses: [EzproxyHosts]
        ]

        record.fileName = file.name
        record.save(failOnError: true)

        record = new EzproxyHosts().createTestRecord()
        record.fileName = "bar"
        record.save(failOnError: true)

        assert 2 == EzproxyHosts.count()
        new EzFileMetaData(fileName: file.name, sha256: "kashjdf").save(failOnError: true)
        assert 1 == EzFileMetaData.count()

        file.write("some random text")
        service.deleteDataForFileIfHashNotCorrect(file)

        assert 1 == EzproxyHosts.count()
        assert 0 == EzFileMetaData.count()
    }
}
