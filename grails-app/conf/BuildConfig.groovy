grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
//location of the release repository
grails.project.repos.metridocRepo.url = "svn:https://metridoc.googlecode.com/svn/maven/repository"
//name of the repository
grails.project.repos.default = "metridocRepo"


grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
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
    dependencies {
        compile("org.jasypt:jasypt:1.9.0")
        build("com.google.code.maven-svn-wagon:maven-svn-wagon:1.4")
    }

    plugins {
        compile(":metridoc-core:0.54.4-SNAPSHOT")
        runtime ":hibernate:$grailsVersion"
        build(":tomcat:$grailsVersion",
                ":release:$grailsVersion",
                ":rest-client-builder:1.0.2") {
            export = false
        }
        build ":release:$grailsVersion"
    }
}
