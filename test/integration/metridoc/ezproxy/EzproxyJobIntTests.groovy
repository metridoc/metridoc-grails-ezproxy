package metridoc.ezproxy

import metridoc.test.EzInjectionHelper
import org.junit.Test

/**
 * Created with IntelliJ IDEA on 4/29/13
 * @author Tommy Barker
 */
class EzproxyJobIntTests {

    def grailsApplication

    @Test
    void "when post processing, ezproxy entities should benefit from injection"() {
        def ezproxyJob = grailsApplication.mainContext.getBean("metridoc.ezproxy.EzproxyJob", EzproxyJob)
        ezproxyJob.defaultEzEntities.clear()
        ezproxyJob.ezEntities = [EzInjectionHelper] as List<Class<EzproxyBase>>
        //noinspection GroovyAccessibility
        ezproxyJob.postProcessEntities("foo")
    }
}


