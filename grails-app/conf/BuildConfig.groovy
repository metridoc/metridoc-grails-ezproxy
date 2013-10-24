/*
  *Copyright 2013 Trustees of the University of Pennsylvania. Licensed under the
  *	Educational Community License, Version 2.0 (the "License"); you may
  *	not use this file except in compliance with the License. You may
  *	obtain a copy of the License at
  *
  *http://www.osedu.org/licenses/ECL-2.0
  *
  *	Unless required by applicable law or agreed to in writing,
  *	software distributed under the License is distributed on an "AS IS"
  *	BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
  *	or implied. See the License for the specific language governing
  *	permissions and limitations under the License.  */

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
        mavenRepo "http://dl.bintray.com/upennlib/metridoc"
        mavenRepo "http://dl.bintray.com/upennlib/maven"
        mavenRepo "http://dl.bintray.com/upennlib/camel"
    }

    dependencies {
        compile("com.github.metridoc:metridoc-job-core:0.8-SNAPSHOT")
    }

    plugins {
        //TODO: more up to date versions of the core do not require the exclusions.  remove once we update the core
        compile(":metridoc-core:0.7.1") {
            excludes "job-runner"
            excludes "database-session"
            excludes ([name:"metridoc-job-core", group:"com.googlecode.metridoc"])
        }

        runtime(":job-runner:0.6.1")
        build(":tomcat:$grailsVersion",
                ":release:2.2.1",
                ":squeaky-clean:0.1.1",
                ":rest-client-builder:1.0.2") {
            export = false
        }
    }
}
