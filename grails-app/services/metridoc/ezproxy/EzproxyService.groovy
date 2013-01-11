package metridoc.ezproxy

import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.IOUtils
import org.quartz.JobKey

import static org.apache.commons.lang.SystemUtils.FILE_SEPARATOR
import static org.apache.commons.lang.SystemUtils.USER_HOME

/**
 * base ezproxy service that handles maintaining / storing parsers and raw data
 */
class EzproxyService {

    def grailsApplication
    def parserText
    def parserObject
    def parserException
    def quartzScheduler
    def _applicationContext

    static final JOB_ACTIVE_PROPERTY_NAME = "metridoc.ezproxy.job.enabled"

    def getApplicationContext() {
        if (_applicationContext) {
            return _applicationContext
        }

        _applicationContext = grailsApplication?.mainContext
    }

    void setApplicationContext(def _applicationContext) {
        this._applicationContext = _applicationContext
    }

    def getRawSampleData() {
        return EzParserProperties.instance().sampleLog
    }

    def getRawParser() {
        return EzParserProperties.instance().sampleParser
    }

    def updateParser(parserText) {
        def instance = EzParserProperties.instance()
        instance.sampleParser = parserText
        instance.save()
    }

    def updateSampleData(rawData) {
        def instance = EzParserProperties.instance()
        instance.sampleLog = rawData
        instance.save()
    }

    def hasParser() {
        EzParserProperties.instance().sampleParser ? true : false
    }

    def hasData() {
        EzParserProperties.instance().sampleLog ? true : false
    }

    def getParsedData() {

        def parserObject = getParserObject()
        def results = [:]
        results.rows = []
        results.headers = []

        getRawSampleData().eachLine { line, lineNumber ->
            def row = parserObject.parse(line, lineNumber + 1, "ezproxy.file") as Map
            if (!results.headers) {
                results.headers = row.keySet()
            }
            results.rows << row.values()
        }

        return results
    }

    def getParserObject() {
        parserException = null
        def storedText = EzParserProperties.instance().sampleParser

        if (storedText) {
            if (shouldRebuildParser(storedText)) {
                parserText = storedText
                try {
                    rebuildParser(storedText)
                } catch (Throwable t) {
                    parserText = null
                    throw t
                }
            }
        }

        return parserObject
    }

    def rebuildParser(String parserText) {
        def parserTemplate = EzproxyUtils.DEFAULT_PARSER_TEMPLATE
        parserObject = buildParser(parserText, parserTemplate)
    }

    def buildParser(String parserText, Closure parserTemplate) {
        String code = parserTemplate.call(parserText)
        def classLoader = Thread.currentThread().contextClassLoader
        def parser = new GroovyClassLoader(classLoader).parseClass(code).newInstance()
        parser.applicationContext = applicationContext

        return parser
    }

    def shouldRebuildParser(String storedparser) {
        parserText != storedparser
    }

    def getEzproxyFiles() {
        def result = []
        def ezproxyDirectory = new File(getEzproxyDirectory())
        def ezproxyFiles = ezproxyDirectory.listFiles()
        if (ezproxyFiles) {
            ezproxyFiles.sort { it.name }.each {
                if (it.isFile()) {
                    def filter = getEzproxyFileFilter()
                    if (it.name ==~ filter) {
                        def fileData = EzFileMetaData.findByFileName(it.name)

                        def itemToAdd = [file: it]
                        if (fileData) {
                            itemToAdd.done = true
                        } else {
                            itemToAdd.done = false
                        }

                        def errors = EzproxyHosts.findAllByFileNameAndError(it.name, true, [max: 1])
                        if (errors) {
                            itemToAdd.error = true
                        }

                        result << itemToAdd
                    }
                }
            }
        }

        return result
    }

    def getEzproxyFileFilter() {
        EzParserProperties.instance().fileFilter
    }

    def getEzproxyDirectory() {
        EzParserProperties.instance().directory
    }

    def updateFileFilter(String newFileFilter) {
        def instance = EzParserProperties.instance()
        instance.fileFilter = newFileFilter
        instance.save()
    }

    def updateDirectory(String newEzproxyDirectory) {
        def instance = EzParserProperties.instance()
        instance.directory = newEzproxyDirectory
        instance.save()
    }

    def isJobActive() {
        EzParserProperties.instance().jobActivated
    }

    def activateEzproxyJob() {
        if (!isJobActive()) {
            log.info "activating ezproxy job"
            setActiveJobProperty(true)
            quartzScheduler.resumeJob(jobKey)
        }
    }

    def deactivateEzproxyJob() {
        if (isJobActive()) {
            log.info "pausing ezproxy job"
            setActiveJobProperty(false)
            quartzScheduler.pauseJob(jobKey)
        }
    }

    def getJobKey() {
        new JobKey(EzproxyJob.class.name)
    }

    private setActiveJobProperty(Boolean value) {
        def instance = EzParserProperties.instance()
        instance.jobActivated = value
        instance.save()
    }

    void deleteDataForFileIfHashNotCorrect(File file) {
        def stream = file.newInputStream()
        def hex = DigestUtils.sha256Hex(stream)
        IOUtils.closeQuietly(stream)

        def fileName = file.name
        def data = EzFileMetaData.findByFileName(fileName)
        if (data) {
            if (hex != data.sha256) {
                deleteDataForFile(fileName)
            }
        }

    }

    void deleteDataForFile(String nameOfFileToDelete) {
        EzFileMetaData.withNewTransaction {
            def data = EzFileMetaData.findByFileName(nameOfFileToDelete)
            if (data) {
                EzFileMetaData.get(data.id).delete()
            }
            def domainClasses = grailsApplication.domainClasses
            domainClasses.each {
                def gormRecord = it.newInstance()

                if (gormRecord instanceof EzproxyBase) {
                    List recordsToDelete = gormRecord.getClass().findAllByFileName(nameOfFileToDelete)
                    recordsToDelete.each {
                        it.delete()
                    }
                }
            }
        }
    }
}

