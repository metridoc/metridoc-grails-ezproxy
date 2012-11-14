
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.repos.metridocRepo.url = "https://metridoc.googlecode.com/svn/plugins"
grails.project.repos.default = "metridocRepo"

grails.project.dependency.resolution = {

    def resolveMetridocCoreRemotely = true
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsCentral()
        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        //mavenLocal()
        //mavenCentral()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
        grailsRepo "https://metridoc.googlecode.com/svn/plugins/"
    }

    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        // runtime 'mysql:mysql-connector-java:5.1.18'
    }

    def fileSeparator = System.getProperty("file.separator")
    def metridocCoreDirectory = new File("..${fileSeparator}metridoc-core")

    def metridocCoreDirectoryExists = metridocCoreDirectory.exists() && metridocCoreDirectory.isDirectory()

    if (metridocCoreDirectoryExists) {
        grails.plugin.location.'metridoc-core' = "../metridoc-core"
        resolveMetridocCoreRemotely = false
        println "metridoc-core exists locally, will use the local version of metridoc-core instead of downloading it"
    } else {
        println "Could not find metridoc-core locally, will use the standard dependency mechanism to get it"
    }

    plugins {
        if (resolveMetridocCoreRemotely) {
            compile (":metridoc-core:0.52-SNAPSHOT") {
                exclude "xmlbeans"
                changing = true
            }
        }
        build(":tomcat:$grailsVersion",
              ":release:2.0.3",
              ":rest-client-builder:1.0.2") {
            export = false
        }
    }
}
