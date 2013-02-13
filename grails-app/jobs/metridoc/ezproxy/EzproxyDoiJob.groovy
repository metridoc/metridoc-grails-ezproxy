package metridoc.ezproxy

import metridoc.core.MetridocJob

/**
 * Executes doi resolutions on a periodic basis
 */
class EzproxyDoiJob extends MetridocJob {

    static TEN_MINUTES = 1000 * 60 * 10
    final static TRIGGER_NAME = "resolve dois"
    def doiService
    int doiResolutionSize = 100

    static triggers = {
        simple repeatInterval: TEN_MINUTES, name: TRIGGER_NAME
    }

    @Override
    def doExecute() {
        target(default:"resolving ezproxy dois"){
            def stats = doiService.populateDoiInformation(100)
            //failure will occurr if stats are wrong
            stats.testStats()
            log.info "ezproxy doi resolution completed with the following stats: ${stats}"
        }
    }
}
