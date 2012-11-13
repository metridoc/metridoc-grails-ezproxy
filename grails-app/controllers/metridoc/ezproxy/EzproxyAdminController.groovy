package metridoc.ezproxy

class EzproxyAdminController {

    def ezproxyService

    static homePage = [
            title: "Ezproxy Admin Panel",
            adminOnly: true,
            description: """
                Creates the parser and sets the general setting for Ezproxy
            """
    ]

    def index() {
        return getBasicModel()
    }

    private getBasicModel() {
        def model = [
                rawSampleData: ezproxyService.rawSampleData,
                ezproxyParser: ezproxyService.rawParser,
                ezproxyFiles: ezproxyService.ezproxyFiles,
                ezproxyDirectory: ezproxyService.ezproxyDirectory,
                ezproxyFileFilter: ezproxyService.ezproxyFileFilter
        ]

        if (ezproxyService.parserException) {

            model << [parseException: ezproxyService.parserException]

        } else if (ezproxyService.hasParser() && ezproxyService.hasData()) {
            def parsedData
            try {
                parsedData = ezproxyService.parsedData
                model << [
                        ezproxyTestData: parsedData,
                        headers: parsedData.headers,
                        rows: parsedData.rows
                ]
            } catch (Throwable throwable) {
                log.error "error occurred parsing ezproxy", throwable
                model << [parseException: throwable]
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

        if (params.ezproxyFileRegex) {
            ezproxyService.updateFileFilter(params.ezproxyFileRegex)
        } else {
            log.warn "there was no file filter to store, parameters are ${params}"
        }

        if (params.ezproxyDirectory) {
            ezproxyService.updateDirectory(params.ezproxyDirectory)
        } else {
            log.warn "there was no directory specified"
        }

        if (params.rawEzproxyData) {
            ezproxyService.updateSampleData(params.rawEzproxyData)
        } else {
            log.warn "there was no ezproxy data to save"
        }

        render(view: "index", model: getBasicModel())
    }



    def testData() {
        [
                headers: ezproxyService.parsedData.headers,
                rows: ezproxyService.parsedData.rows
        ]
    }

    def listFiles() {
        [
                ezproxyFiles: ezproxyService.ezproxyFiles,
                ezproxyDirectory: ezproxyService.ezproxyDirectory
        ]

    }
}

