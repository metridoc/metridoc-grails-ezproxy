package metridoc.ezproxy

import org.junit.Test

/**
 * Created with IntelliJ IDEA on 4/11/13
 * @author Tommy Barker
 */
class EzproxyJobTest {

    def job = new EzproxyJob()

    @Test
    void "test previewing the default data"() {
        def sampleData = job.getPreviewRecords(job.ezproxySampleData)
        assert 10 == sampleData.size()
    }

}
