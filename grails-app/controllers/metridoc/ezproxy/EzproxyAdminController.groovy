package metridoc.ezproxy

class EzproxyAdminController {

    def ezproxyService

    static accessControl = {
        role(name: "ROLE_ADMIN")
    }

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
        def instance = EzParserProperties.instance()
        def model = [
                rawSampleData: instance.sampleLog,
                ezproxyParser: instance.sampleParser,
                ezproxyFiles: ezproxyService.ezproxyFiles,
                ezproxyDirectory: instance.directory,
                ezproxyFileFilter: instance.fileFilter,
                ezproxyJobIsActive: instance.jobActivated,
                crossRefUserName: instance.crossRefUserName,
                crossRefPassword: EzParserProperties.decryptedCrossRefPassword
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

        if (log.debugEnabled) {
            log.debug "parameters for the ezproxy update are $params"
        }
        def instance = EzParserProperties.instance()
        params.remove("_action_updateEzproxyParser")
        params.remove("action")
        params.remove("controller")
        params.remove("crossRefEncryptionKey")
        def password = params.remove("crossRefPassword")

        params.each {
            instance."$it.key" = it.value
        }
        instance.save()
        if (password) {
            EzParserProperties.updatePassword(password)
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

