package metridoc.ezproxy

import org.junit.Test
import org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsDomainBinder

class EzproxyTableResolutionTests {

    @Test
    void testSomething() {
        assert GrailsDomainBinder.getMapping(EzproxyHosts).table
    }
}
