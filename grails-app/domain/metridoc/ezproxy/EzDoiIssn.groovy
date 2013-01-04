package metridoc.ezproxy

class EzDoiIssn {
    String issn
    String issn_type

    static constraints = {
        issn(blank: false, maxSize: 24)
        issn_type(blank: false, maxSize: 20, nullable: true)
    }
}
