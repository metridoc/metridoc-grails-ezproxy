grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
//location of the release repository
grails.project.repos.metridocRepo.url = new File("../maven/repository").toURI().toURL().toString()
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
        grailsPlugins()
        grailsHome()
        grailsCentral()
        mavenCentral()
        mavenRepo "https://oss.sonatype.org/content/repositories/snapshots"
        mavenRepo "https://metridoc.googlecode.com/svn/trunk/maven/repository"
    }
    dependencies {
        //this is only needed to make things work in intellij, it won't be included in the built war or running application
        build("org.tmatesoft.svnkit:svnkit:1.3.5") {
            excludes "jna", "trilead-ssh2", "sqljet"
        }
        compile("org.jasypt:jasypt:1.9.0")
    }

    plugins {
        compile(":metridoc-core:0.53-SNAPSHOT")
        runtime ":hibernate:$grailsVersion"
        build(":tomcat:$grailsVersion",
                ":release:$grailsVersion",
                ":rest-client-builder:1.0.2") {
            export = false
        }
    }
}
