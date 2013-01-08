package metridoc.ezproxy

import metridoc.core.MetridocJob
import org.apache.commons.codec.digest.DigestUtils
import org.quartz.JobKey
import org.apache.commons.io.IOUtils

class EzproxyJob extends MetridocJob {

    static HALF_HOUR = 1000 * 60 * 30
    def ezproxyService
    def gormEzproxyFileService
    final static TRIGGER_NAME = "parse ezproxy files"
    def quartzScheduler

    static triggers = {
        simple repeatInterval: HALF_HOUR, name: TRIGGER_NAME
    }

    private getFiles(Closure condition) {
        ezproxyService.ezproxyFiles.findAll {
            return condition.call(it)
        }
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

        target(ezMaintenance: "checking md5 of files") {
            getFiles {it.done}.each {
                File fileToDelete = it.file
                ezproxyService.deleteDataForFileIfHashNotCorrect(fileToDelete)
            }
        }

        target(processingEzproxyFiles: "processing ezproxy files") {
            def files = getFiles {!it.done}

            boolean hasFilesAndParser = true
            if (!files) {
                log.info "there are no ezproxy files to parse"
                hasFilesAndParser = false
            }

            def hasParser = ezproxyService.hasParser()

            if (!hasParser) {
                log.info "no parser has been set, can't parse ezproxy files yet"
                hasFilesAndParser = false
            }

            if (hasFilesAndParser) {
                def fileToProcess = files[0].file //only load one file at a time
                gormEzproxyFileService.processFile(fileToProcess)
            }
        }

        target(default: "runs maintenance and processes ezproxy files") {
            depends("ezMaintenance", "processingEzproxyFiles")
        }
    }
}
