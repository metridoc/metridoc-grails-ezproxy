package metridoc.ezproxy

import groovy.sql.Sql
import metridoc.core.MetridocJob
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.IOUtils
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.text.StrBuilder
import org.hibernate.SessionFactory
import org.hibernate.metadata.ClassMetadata
import org.slf4j.LoggerFactory

import javax.sql.DataSource
import java.text.SimpleDateFormat
import java.util.zip.GZIPInputStream

class EzproxyJob extends MetridocJob {

    public static final log = LoggerFactory.getLogger(EzproxyJob)
    public static final String ALL_FILES = ".*"
    SessionFactory sessionFactory

    @SuppressWarnings("GroovyAssignabilityCheck")
    public static final Closure<Object> DEFAULT_PARSER = {
        def data = line.split(/\|\|/)
        assert data.size() >= 14: "there should be at least 14 data fields"
        result.ipAddress = data[0]
        result.city = data[1]
        result.state = data[2]
        result.country = data[3]
        result.patronId = data[5]
        result.proxyDate = new SimpleDateFormat("[dd/MMM/yyyy:hh:mm:ss Z]").parse(data[6])
        result.url = data[8]
        result.ezproxyId = data[13]
    }

    List<Class<? extends EzproxyBase>> defaultEzEntities = [EzproxyHosts.class, EzDoi.class] as List

    /**
     * added any additional entities you want here
     */
    List<Class<? extends EzproxyBase>> ezEntities = []
    def doiService
    def ezproxyParser = DEFAULT_PARSER
    int doiResolutionSize = 2000
    String ezproxyEncoding = "utf-8"
    File ezproxyDirectory = EzproxyUtils.DEFAULT_FILE_LOCATION
    String ezproxyFileFilter = ALL_FILES
    String ezproxySampleData = EzproxyUtils.DEFAULT_LOG_DATA
    String crossRefUsername
    String crossRefPassword
    File ezproxyFile
    def dataSource

    def getDoiService() {
        if (doiService == null) {
            doiService = new DoiService()
        }

        doiService.crossRefUsername = crossRefUsername
        doiService.crossRefPassword = crossRefPassword

        return doiService
    }

    private getFiles(Closure condition) {
        ezproxyFiles.findAll {
            return condition.call(it)
        }
    }

    def getEzproxyFiles() {
        def result = []
        if (ezproxyDirectory.exists()) {
            def ezproxyFiles = ezproxyDirectory.listFiles()
            if (ezproxyFiles) {
                ezproxyFiles.sort { it.name }.each {
                    if (it.isFile()) {
                        def filter = getEzproxyFileFilter()
                        if (it.name ==~ filter) {
                            def fileData = EzFileMetaData.findByFileName(it.name)
                            def now = new Date()

                            if (fileData) {
                                if (fileData.processing && (now - fileData.processStarted) > 2) {
                                    deleteDataForFile(it.name)
                                    fileData = null
                                }
                            }

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
        }

        return result
    }

    List<EzproxyBase> getAllEntities() {
        def result = []
        result.addAll(defaultEzEntities)
        result.addAll(ezEntities)

        return result
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

            allEntities.each {
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

    @Override
    def configure() {

        target(printEzConfig: "prints various stats on the configuration of the ezproxy job") {
            boolean ezproxyDirectoryExists = ezproxyDirectory.exists()


            log.info ""
            log.info "=== EZPROXY CONFIG ==="
            log.info "    ezproxyDirectory: ${ezproxyDirectory ?: 'not set'}"
            if (ezproxyDirectory != null) {
                log.info "        ezproxy directory ${ezproxyDirectoryExists ? 'exists' : 'does not exist'}"
                if (ezproxyDirectory.exists()) {
                    log.info "        ezproxy file count: ${getEzproxyFiles().size()}"
                }
                log.info ""
            }
            log.info "    ezproxyFile: ${ezproxyFile ?: 'not set'}"
            log.info "    ezproxy encoding: ${ezproxyEncoding}"
            log.info "    crossRefUsername: ${crossRefUsername ?: 'not set'}"
            log.info "    crossRefPassword: ${crossRefPassword ? 'set' : 'not set'}"
            log.info "    doiResolutionSize: ${doiResolutionSize ?: 'not set'}"
            log.info "=== EZPROXY CONFIG ==="
            log.info ""
        }

        target(ezMaintenance: "checking md5 of files") {
            if (ezproxyFile) {
                deleteDataForFileIfHashNotCorrect(ezproxyFile)
            } else {
                getFiles { it.done }.each {
                    File fileToDelete = it.file
                    deleteDataForFileIfHashNotCorrect(fileToDelete)
                }
            }
        }

        target(processingEzproxyFiles: "processing ezproxy files") {
            def files = getFiles { !it.done && !it.error }

            boolean hasFilesAndParser = true
            if (!files) {
                if (ezproxyFile && ezproxyFile.exists()) {
                    log.info "using single file processing instead of directory processing"
                } else {
                    log.info "there are no ezproxy files to parse"
                    hasFilesAndParser = false
                }
            }

            if (ezproxyParser == null) {
                log.info "no parser has been set, can't parse ezproxy files yet"
                hasFilesAndParser = false
            }

            if (hasFilesAndParser) {
                def fileToProcess = ezproxyFile ?: files[0].file //only load one file at a time
                try {
                    processFile(fileToProcess)
                } catch (Exception e) {
                    log.error "exception occurred processing file $fileToProcess, rolling back all data"
                    deleteDataForFile(fileToProcess.name)
                    throw e
                }
            }
        }

        target(resolveEzproxyDois: "resolving ezproxy dois") {
            def stats = getDoiService().populateDoiInformation(doiResolutionSize)
            //failure will occurr if stats are wrong
            if (stats) {
                stats.testStats()
                log.info "ezproxy doi resolution completed with the following stats: ${stats}"
            }
        }

        target(preview: "parses the sample data, or a specified file and prints the data") {
            if (ezproxyFile) {
                log.info "running preview on file $ezproxyFile"
                printParsedSampleData(ezproxyFile.getText(ezproxyEncoding))
            } else {
                log.info "running preview on sample data"
                printParsedSampleData(ezproxySampleData)
            }
        }

        target(default: "runs maintenance and processes ezproxy files and dois") {
            depends("printEzConfig", "ezMaintenance", "processingEzproxyFiles", "resolveEzproxyDois")
        }

        target(truncateTables: "truncates all ezproxy tables") {
            //TODO: add an are you sure prompt
            performSqlOnAllTables { "truncate table ${it}" }
        }

        target(dropTables: "drops all ezproxy tables") {
            //TODO: add an are you sure prompt
            performSqlOnAllTables { "drop table if exists ${it}" }
        }
    }

    private void performSqlOnAllTables(Closure tableBasedSql) {
        def entities = []
        entities.addAll(getAllEntities())
        entities.add(EzSkip)
        entities.add(EzFileMetaData)
        entities.add(EzDoiJournal)
        entities.each { Class clazz ->
            ClassMetadata metaData = sessionFactory.getClassMetadata(clazz)
            def table = metaData.tableName
            def sql = new Sql(dataSource as DataSource)
            String sqlQuery = tableBasedSql.call(table)
            sql.execute(sqlQuery)
        }
    }

    private void printParsedSampleData(String data) {
        log.info ""
        log.info "===== BEGIN PREVIEW ====="
        getPreviewRecords(data).each {
            log.info it as String
        }
        log.info "====== END PREVIEW ======"
        log.info ""
    }

    void handleValidationError(EzproxyBase object) {
        if (object.errors.fieldErrorCount) {
            //noinspection GroovyAssignabilityCheck
            def error = object.errors.fieldErrors[0]
            //noinspection GroovyAssignabilityCheck
            def message = "Field error in object '" + error.objectName + "' on field '" + error.field +
                    "': rejected value [" + error.rejectedValue + "] with error code [" + object.errors.fieldErrors[0].code + "]"

            def skip = new EzSkip(
                    fileName: object.fileName,
                    lineNumber: object.lineNumber,
                    type: object.getClass().simpleName,
                    error: message
            )

            skip.save(failOnError: true)
            log.warn "saved invalid record ${object} for file ${object.fileName} at line ${object.lineNumber} with validation error message: ${message}"
        } else {
            //if it is not a field error, then something really bad happened, let's just make it fail
            object.save(failOnError: true)
        }
    }

    List<Map<String, Object>> getPreviewRecords(String sampleData) {
        return getPreviewRecords(sampleData, getParserObject())
    }

    List<Map<String, Object>> getPreviewRecords(String sampleData, parser) {
        def result = []
        sampleData.trim().eachLine { String line, int lineNumber ->
            result.add(parser.parse(line.trim(), lineNumber, "sample"))
        }

        return result
    }

    void processFile(File file) {
        processFile(file, parserObject)
    }

    def getParserObject(parser) {
        if (parser instanceof Closure) {
            return new EzproxyParserWrapper(parser: parser)
        }

        return parser
    }

    def getParserObject() {
        if (ezproxyParser instanceof Closure) {
            return new EzproxyParserWrapper(parser: ezproxyParser)
        }

        return ezproxyParser
    }

    void processFile(File file, parser) {
        def stats = [:]

        allEntities.each { Class aClass ->
            stats[aClass.name] = [valid: 0, invalid: 0, rejected: 0]
        }

        def streamToDigest = file.newInputStream()
        def hex = DigestUtils.sha256Hex(streamToDigest)
        IOUtils.closeQuietly(streamToDigest)

        def fileName = file.name

        try {
            new EzFileMetaData(fileName: fileName, sha256: hex).save(failOnError: true, flush: true)
        } catch (Throwable e) {
            log.warn "error occurred trying to store meta data for $fileName, $fileName is probably being processed in parallel, this job will be terminated prematurely", e
            return
        }

        EzproxyHosts.withNewTransaction {

            log.info "stored metaData of file $file"
            def stream = new FileInputStream(file)
            if (file.name.endsWith(".gz")) {
                stream = new GZIPInputStream(stream)
            }

            //noinspection GroovyMissingReturnStatement
            stream.eachLine(ezproxyEncoding) { String line, int lineNumber ->
                def record = null
                try {
                    record = parser.parse(line, lineNumber, file.name)
                    process(record, stats)
                } catch (Throwable throwable) {
                    if (record) {
                        log.error "fatal error occurred for file $file at line $lineNumber with record $record", throwable
                    } else {
                        log.error "fatal error occurred for file $file at line $lineNumber with line $line", throwable
                    }
                    throw throwable
                }
            }
        }
        postProcessEntities(fileName)
        log.info "finished processing file $file with loading stats ${getStatOutput(stats)}"
        def ezFileMetaData = EzFileMetaData.findByFileName(fileName)
        ezFileMetaData.processing = false
        ezFileMetaData.save(flush: true)
    }

    private void postProcessEntities(fileName) {
        allEntities.each {
            def instance = it.newInstance()
            instance.postProcess(fileName)
        }
    }

    private static String getStatOutput(Map stats) {
        def builder = new StrBuilder()
        builder.appendln(StringUtils.EMPTY)
        builder.appendln("[")
        stats.each {
            builder.appendln("    $it")
        }
        builder.appendln("]")
        return builder.toString()
    }

    protected void process(Map<String, Object> record, Map stats) {

        def linesLogged = [] as Set

        allEntities.each { Class<EzproxyBase> aClass ->
            EzproxyBase gormRecord = aClass.newInstance()
            def gormClassName = aClass.name

            if (gormRecord.accept(record)) {
                gormRecord.loadValues(record)
                boolean valid = gormRecord.validate()
                if (!valid) {
                    handleValidationError(gormRecord)
                    stats[gormClassName].invalid++
                } else {
                    gormRecord.save(failOnError: true)
                    stats[gormClassName].valid++
                    if (log.isDebugEnabled()) {
                        log.debug "saved record ${gormRecord} for file ${gormRecord.fileName} and line ${gormRecord.lineNumber} for gorm object ${gormRecord.getClass()}"
                    }
                }
            } else {
                stats[gormClassName].rejected++
            }

            def lineNumber = record.lineNumber
            def fileName = record.fileName
            if (lineNumber % 10000 == 0 && !linesLogged.contains(lineNumber)) {
                log.info "$lineNumber lines have been processed for file $fileName"
                linesLogged << lineNumber
            }
        }
    }
}

class EzproxyParserWrapper {
    Closure parser

    Map parse(String line, int lineNumber, String fileName) {
        def result = [:]
        def delegate = new EzproxyParserDelegate(fileName: fileName, line: line, result: result, lineNumber: lineNumber)
        parser.delegate = delegate
        parser.resolveStrategy = Closure.DELEGATE_FIRST
        parser.call()
        result.lineNumber = lineNumber
        result.fileName = fileName
        return result
    }
}

class EzproxyParserDelegate {
    def result = [:]
    String line
    String fileName
    int lineNumber
}

