package metridoc.ezproxy

import org.apache.maven.artifact.ant.shaded.ExceptionUtils

class EzproxyAdminController {

    def ezproxyService

    def index() {
        def model = [
                rawSampleData: ezproxyService.rawSampleData,
                ezproxyParser: ezproxyService.rawParser
        ]

        if (ezproxyService.hasParser() && ezproxyService.hasData()) {
            def parsedData
            try {
                parsedData = ezproxyService.parsedData
                model << [
                        ezproxyTestData: parsedData,
                        headers: parsedData.headers,
                        rows: parsedData.rows
                ]
            } catch(Throwable throwable) {
                log.error "error occurred parsing ezproxy", throwable
                model << [parseException: ExceptionUtils.getStackTrace(throwable).encodeAsHTML()]
            }
        }

        return model
    }

    def updateEzproxyParser() {
        if (params.ezproxyParserScript) {
            ezproxyService.updateParser(params.ezproxyParserScript)
        } else {
            log.warn "there was no ezproxy script to save"
        }

        if (params.rawEzproxyData) {
            ezproxyService.updateSampleData(params.rawEzproxyData)
        } else {
            log.warn "there was no ezproxy data to save"
        }

        redirect(action: "index")
    }



    def testData() {
        [
                headers: ezproxyService.parsedData.headers,
                rows: ezproxyService.parsedData.rows
        ]
    }
}

