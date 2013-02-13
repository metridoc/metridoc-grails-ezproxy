package metridoc.ezproxy

import metridoc.core.MetridocJob
import org.quartz.JobKey

/**
 * Executes doi resolutions on a periodic basis
 */
class EzproxyDoiJob extends MetridocJob {

    static TEN_MINUTES = 1000 * 60 * 10
    final static TRIGGER_NAME = "resolve dois"
    def doiService
    int doiResolutionSize = 100
    def ezproxyService
    def quartzScheduler

    static triggers = {
        simple repeatInterval: TEN_MINUTES, name: TRIGGER_NAME
    }

    @Override
    def doExecute() {

        if (!ezproxyService.isJobActive()) {
            def jobKey = new JobKey(this.class.name)
            assert jobKey: "Unexpected exception, could not find the job key for EzproxyJob"
            quartzScheduler.pauseJob(jobKey)
            log.info "ezproxy job is not active, pausing it for now.  It can be reactivated either in the ezproxy admin panel or the job list"

            return
        }

        target(default:"resolving ezproxy dois"){
            def stats = doiService.populateDoiInformation(doiResolutionSize)
            //failure will occurr if stats are wrong
            stats.testStats()
            log.info "ezproxy doi resolution completed with the following stats: ${stats}"
        }
    }
}
