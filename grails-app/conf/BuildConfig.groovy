grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
grails.project.repos.metridocRepo.url = "https://api.bintray.com/maven/upennlib/metridoc/metridoc-core"
grails.project.repos.default = "metridocRepo"

grails.project.dependency.resolution = {
    inherits("global")
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        inherits true // Whether to inherit repository definitions from plugins
        mavenLocal()
        grailsPlugins()
        grailsHome()
        grailsCentral()
        mavenCentral()
        mavenRepo "https://oss.sonatype.org/content/repositories/snapshots"
        mavenRepo "https://metridoc.googlecode.com/svn/maven/repository"
    }

    plugins {
        //TODO: more up to date versions of the core do not require the exclusions.  remove once we update the core
        compile(":metridoc-core:0.7.1") {
            excludes "job-runner"
            excludes "database-session"
        }

        runtime(":job-runner:0.6.1")
        build(":tomcat:$grailsVersion",
                ":release:2.2.1",
                ":rest-client-builder:1.0.2") {
            export = false
        }
    }
}
