package metridoc.ezproxy

class EzproxyAdminController {

    def ezproxyService
    def cachedClass

    def index() {
        def model = [
                availableFiles: ezproxyService.availableFiles(),

        ]

        if (ezproxyService.parser) {
            model << [
                    ezproxyParser: ezproxyService.parser.propertyValue,
                    ezproxyTestData: ezproxyService.parsedData,
                    headers: ezproxyService.parsedData.headers,
                    rows: ezproxyService.parsedData.rows
            ]
        }
    }

    def save() {
        def ezproxyFile = request.getFile("file")
        ezproxyFile.transferTo(new File(ezproxyService.ezproxySampleDirectory, ezproxyFile.originalFilename))
        redirect(action: "index")
    }

    def deleteFile() {

        def fileNameToDelete = params.fileToDelete
        if (fileNameToDelete) {
            def fileToDelete = new File(ezproxyService.ezproxySampleDirectory, fileNameToDelete)
            if (fileToDelete.exists()) {
                def deleted = fileToDelete.delete()
                if (!deleted) {
                    def message = "Could not delete file $fileToDelete"
                    log.warn message
                    render message
                } else {
                    render "file $fileToDelete has been deleted"
                }
            } else {
                render "no file was deleted"
            }
        }

        render "file "
    }

    def updateEzproxyParser() {
        if (params.ezproxyParserScript) {
            def parser = ezproxyService.parser
            if (parser) {
                parser.propertyValue = params.ezproxyParserScript
            } else {
                new EzProperties(propertyName: "metridoc.ezproxy.parser", propertyValue: params.ezproxyParserScript).save()
            }

            render """<h2>saved</h2>:</br>
                        <pre>${params.ezproxyParserScript}</pre>
                    </br>"""
        } else {
            log.warn "there was no ezproxy script to save"
        }

    }



    def testData() {
        [
                headers: ezproxyService.parsedData.headers,
                rows: ezproxyService.parsedData.rows
        ]
    }
}

