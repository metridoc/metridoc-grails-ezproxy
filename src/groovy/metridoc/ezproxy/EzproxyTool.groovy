package metridoc.ezproxy

import metridoc.core.tools.FileProcessingTool
import metridoc.core.tools.Record

import java.text.SimpleDateFormat

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 3/18/13
 * Time: 11:58 AM
 * To change this template use File | Settings | File Templates.
 */
class EzproxyTool extends FileProcessingTool {

    File ezproxyDirectory
    File ezproxyFile
    Closure ezproxyParser

    File getDirectory() {
        ezproxyDirectory
    }

    File getFile() {
        ezproxyFile
    }

    def getLineProcessor() {
        if (lineProcessor) return lineProcessor

        lineProcessor = { Record record ->
            def delegate = new LineDelegate(line: record.data.line)
            ezproxyParser.delegate = delegate
            ezproxyParser.call()
            def transformed = new Record()
            transformed.tool = record.tool
            transformed.sourceMetaData = record.sourceMetaData
            def result = delegate.result
            transformed.data = result
            result.fileName = record.data.fileName
            result.lineNumber = record.lineNumber

            return transformed
        }
    }

    Closure getEzproxyParser() {
        if (ezproxyParser) return ezproxyParser

        ezproxyParser = {
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
    }
}

class LineDelegate {
    String line
    Map result = [:]
}