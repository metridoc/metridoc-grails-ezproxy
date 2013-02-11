package metridoc.ezproxy

import metridoc.core.MetridocJob

/**
 * Executes doi resolutions on a periodic basis
 */
class EzproxyDoiJob extends MetridocJob {

    static TEN_MINUTES = 1000 * 60 * 10
    final static TRIGGER_NAME = "resolve dois"

    static triggers = {
        simple repeatInterval: TEN_MINUTES, name: TRIGGER_NAME
    }

    @Override
    def doExecute() {
        EzDoiJournal.resolveDois(100)
    }
}
