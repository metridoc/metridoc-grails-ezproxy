package metridoc.ezproxy

class EzDoiIsbn {

    String isbn
    String isbn_type

    static constraints = {
        isbn(blank: false, maxSize: 64)
        isbn_type(blank: false, maxSize: 20, nullable: true)
    }
}
