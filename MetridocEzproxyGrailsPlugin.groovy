class MetridocEzproxyGrailsPlugin {
    // the plugin version
    def version = "0.2-SNAPSHOT"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.1 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    def title = "Metridoc Ezproxy Plugin" // Headline display name of the plugin
    def author = "Tommy Barker"

    def description = '''\
Plugin to handle the ingestion of ezproxy files
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/metridoc-ezproxy"

}
