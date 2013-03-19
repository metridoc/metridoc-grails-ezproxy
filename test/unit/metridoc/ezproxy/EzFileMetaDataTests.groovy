package metridoc.ezproxy

import grails.test.mixin.TestFor
import org.junit.Test

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 3/19/13
 * Time: 3:38 PM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(EzFileMetaData)
class EzFileMetaDataTests {

    @Test
    void "file name should be unique"() {

        def record = {
            new EzFileMetaData(
                fileName: "blah",
                sha256: "blah")
        }

        record.call().save(failOnError: true)

        def badRecord = record.call()
        assert !badRecord.validate()
        assert "unique" == badRecord.errors.allErrors[0].code
    }
}
