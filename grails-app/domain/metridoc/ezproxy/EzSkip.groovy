package metridoc.ezproxy

class EzSkip {

    Date dateCreated
    String fileName
    String type
    Integer lineNumber
    String error

    static constraints = {
        fileName(unique: ["lineNumber", "type"])
        error(maxSize: Integer.MAX_VALUE)
    }
}
