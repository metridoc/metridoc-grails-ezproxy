package metridoc.ezproxy

import metridoc.core.MetridocJob



class EzproxyJob extends MetridocJob {

    static HALF_HOUR = 1000 * 60 * 30
    def ezproxyService
    def gormEzproxyFileService

    static triggers = {
        simple repeatInterval: HALF_HOUR, name: "parse ezproxy files"
    }

    @Override
    def doExecute() {

        def files = ezproxyService.ezproxyFiles.findAll{
            !it.done
        }

        if (!files) {
            log.info "there are no ezproxy files to parse"
            return
        }

        def hasParser = ezproxyService.hasParser()
        if(!hasParser) {
            log.info "no parser has been set, can't parse ezproxy files yet"
            return
        }

        def fileToProcess = files[0].file //only load one file at a time
        profile("processing file $fileToProcess") {
            gormEzproxyFileService.processFile(fileToProcess)
        }
    }
}
