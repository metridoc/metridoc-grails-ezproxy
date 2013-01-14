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
                ezproxyFileFilter: ezproxyService.ezproxyFileFilter,
                ezproxyJobIsActive: ezproxyService.isJobActive(),
                storePatronId: ezproxyService.storePatronId
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

    def activateJob() {
        ezproxyService.activateEzproxyJob()
        redirect(action: "index")
    }

    def deactivateJob() {
        ezproxyService.deactivateEzproxyJob()
        redirect(action: "index")
    }

    def updateEzproxyParser() {
        //TODO: remove once done
        log.info params
        if ("on" == params.storePatronId) {
            ezproxyService.updateStorePatronId(true)
        } else {
            ezproxyService.updateStorePatronId(false)
        }
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

    def deleteFileData() {
        String fileName = params.id
        if (fileName) {
            log.info "attempting to delete data for ezproxy file $params.id"
            ezproxyService.deleteDataForFile(fileName)
        }

        redirect(action: "index")
    }
}

