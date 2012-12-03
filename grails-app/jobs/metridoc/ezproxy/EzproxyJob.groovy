package metridoc.ezproxy

import metridoc.core.MetridocJob
import org.apache.shiro.crypto.hash.Sha256Hash



class EzproxyJob extends MetridocJob {

    static HALF_HOUR = 1000 * 60 * 30
    def ezproxyService
    def gormEzproxyFileService

    static triggers = {
        simple repeatInterval: HALF_HOUR, name: "parse ezproxy files"
    }

    private getFiles(Closure condition) {
        ezproxyService.ezproxyFiles.findAll {
            return condition.call(it)
        }
    }

    @Override
    def doExecute() {

        target(ezMaintenance: "checking md5 of files") {
            getFiles {it.done}.each {
                def file = it.file
                def hex = new Sha256Hash(file).toHex()
                EzFileMetaData.withNewTransaction {
                    def fileName = file.name
                    def data = EzFileMetaData.findByFileName(fileName)
                    if (data) {
                        if (hex != data.sha256) {
                            EzFileMetaData.get(data.id).delete()
                            EzproxyHosts.executeUpdate('delete EzproxyHosts e where e.fileName = :fileName', [fileName: fileName])
                        }
                    }
                }
                hex = null
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
