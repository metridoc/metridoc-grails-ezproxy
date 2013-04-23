package metridoc.ezproxy

import metridoc.core.JobDetails
import metridoc.utils.ConfigObjectUtils
import org.apache.commons.lang.StringUtils
import org.springframework.core.io.ClassPathResource

class EzproxyAdminController {

    public static final String CONFIG_FILE_ENCODING = "utf-8"
    def ezproxyService
    def grailsApplication

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
        def details = JobDetails.findByJobName(EzproxyJob.name)
        def config = details.config
        def configNotSet = config == null || config.trim() == StringUtils.EMPTY

        if (configNotSet) {
            config = storeDefaultConfig(details)
        }

        ConfigObject mergedConfig
        if (grailsApplication) {
            mergedConfig = ConfigObjectUtils.clone(grailsApplication.mergedConfig)
            def parsedConfig = new ConfigSlurper().parse(config)
            mergedConfig.merge(parsedConfig)
        }

        def directory = mergedConfig.ezproxyDirectory
        if (directory) {
            //TODO: this is really ugly, shuold change this
            ezproxyService.ezproxyDirectory = directory
        }

        def fileFilter = mergedConfig.ezproxyFileFilter
        if (fileFilter) {
            ezproxyService.ezproxyFileFilter = fileFilter
        }

        def parseException
        def rows = []
        def headers
        try {
            def job = new EzproxyJob()
            def mappedData = job.getPreviewRecords(mergedConfig.ezproxySampleData as String, job.getParserObject(mergedConfig.ezproxyParser))
            headers = mappedData[0].keySet()
            mappedData.each {
                rows.add(it.values())
            }
        } catch (Throwable throwable) {
            parseException = throwable
        }

        return [
                headers: headers,
                parseException: parseException,
                rows: rows,
                config: config,
                ezproxyDirectory: directory,
                ezproxyFiles: ezproxyService.ezproxyFiles
        ]

    }

    private String storeDefaultConfig(JobDetails details) {
        def configResource = new ClassPathResource(EzproxyService.EZPROXY_CONFIG_TEMPLATE)
        def exists = configResource.exists()
        if (exists) {
            details.config = configResource.file.getText(CONFIG_FILE_ENCODING)
            details.save(flush: true)
        }
        details.config
    }

    def updateEzproxyParser(String config) {

        if (log.debugEnabled) {
            log.debug "parameters for the ezproxy update are $params"
        }

        if (config) {
            def details = JobDetails.findByJobName(EzproxyJob.name)
            details.config = config
            try {
                details.save(failOnError: true)
            } catch (Throwable throwable) {
                def configThrowable = JobDetails.getConfigException(config)
                log.error("exception occurred trying to save configuration", configThrowable)
                flash.alerts << configThrowable.message
            }
        } else {
            flash.alerts << "Ezproxy config cannot be blank"
        }

        redirect(action: "index")
    }

    def resetEzproxyConfig() {
        storeDefaultConfig(JobDetails.findByJobName(EzproxyJob.name))

        redirect(action: "index")
    }

    def testData() {
        [
                headers: ezproxyService.parsedData.headers,
                rows: ezproxyService.parsedData.rows
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

