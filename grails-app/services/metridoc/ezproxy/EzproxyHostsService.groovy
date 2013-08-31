package metridoc.ezproxy

import metridoc.core.MetridocJob
import metridoc.core.InjectArg

class EzproxyHostsService extends MetridocJob{

    @InjectArg
    int patronId

    String getUsage() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    def configure() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }
}
