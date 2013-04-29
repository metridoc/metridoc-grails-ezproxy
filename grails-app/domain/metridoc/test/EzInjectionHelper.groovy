package metridoc.test

import metridoc.ezproxy.EzproxyBase

/**
 * Class is used for testing if injection works when the ezproxy job does a post process
 */
class EzInjectionHelper extends EzproxyBase {
    def dataSource

    String foo
    int bar

    static transients = ['dataSource']
    static constraints = {
    }

    @Override
    void postProcess(String fileName) {
        assert dataSource: "dataSource should have been injected"
    }
}
