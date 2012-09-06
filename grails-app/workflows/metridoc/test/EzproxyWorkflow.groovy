package metridoc.test

import metridoc.core.FileToBatchWorkflow

class EzproxyWorkflow extends FileToBatchWorkflow {

    def grailsApplication

    def run() {
        target(runEzproxy: "main target to run ezproxy") {
            grailsConsole.info "ezproxy ran"
            if (grailsApplication == null) {
                grailsConsole.info "it is null"
            }

            def beans = appCtx.beanDefinitionNames as TreeSet

            beans.each {
                grailsConsole.info it
            }
        }
    }
}




