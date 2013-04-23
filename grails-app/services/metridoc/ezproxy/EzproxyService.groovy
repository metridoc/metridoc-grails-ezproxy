package metridoc.ezproxy

import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.IOUtils

/**
 * base ezproxy service that handles maintaining / storing parsers and raw data
 */
class EzproxyService {

    static final String EZPROXY_CONFIG_TEMPLATE = "ezproxyConfig.gtmpl"
    def grailsApplication
    String ezproxyDirectory
    String ezproxyFileFilter

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

                if (gormRecord instanceof EzproxyBase || gormRecord instanceof EzSkip) {
                    List recordsToDelete = gormRecord.getClass().findAllByFileName(nameOfFileToDelete)
                    recordsToDelete.each {
                        it.delete()
                    }
                }
            }
        }
    }
}



