import org.springframework.core.io.ClassPathResource
import org.codehaus.groovy.grails.compiler.injection.GrailsAwareClassLoader
import org.codehaus.groovy.grails.compiler.injection.ClassInjector
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.CompilationUnit
import java.security.CodeSource
import org.codehaus.groovy.grails.compiler.injection.GrailsAwareInjectionOperation
import org.codehaus.groovy.control.Phases
import org.springframework.util.ReflectionUtils
import org.codehaus.groovy.grails.commons.DefaultGrailsApplication
import grails.util.Holders
import org.codehaus.groovy.grails.compiler.injection.DefaultGrailsDomainClassInjector
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.control.SourceUnit

class MetridocEzproxyBootStrap {

    def grailsApplication
    def pluginManager

    def init = { servletContext ->
        def template = """
            class EzMantle {
                String name
                int age
            }
        """

        def clazz = new DynamicClassLoader().parseClass(template)
        def domainPlugin = pluginManager.getGrailsPlugin("domainClass")
        def hibernatePlugin = pluginManager.getGrailsPlugin("hibernate")
        def rawDomainPlugin = domainPlugin.pluginBean.wrappedInstance
        def rawHibernatePlugin = hibernatePlugin.pluginBean.wrappedInstance

        def event = [
                application: grailsApplication,
                manager: pluginManager,
                source: clazz,
                ctx: grailsApplication.mainContext,
                plugin:domainPlugin
        ]

        rawDomainPlugin.onChange.delegate = domainPlugin
        rawHibernatePlugin.onChange.delegate = hibernatePlugin
        rawDomainPlugin.onChange.call(event)
        rawHibernatePlugin.onChange.call(event)
    }
    def destroy = {
    }
}

class DynamicClassLoader extends GrailsAwareClassLoader {

    private final ClassInjector[] _classInjectors = [new DynamicDomainClassInjector()]

    DynamicClassLoader() {
        super(Holders.grailsApplication.classLoader, CompilerConfiguration.DEFAULT)
        classInjectors = _classInjectors
        setClassLoader()
    }

    @Override
    protected CompilationUnit createCompilationUnit(CompilerConfiguration config, CodeSource source) {
        CompilationUnit cu = super.createCompilationUnit(config, source)
        cu.addPhaseOperation(new GrailsAwareInjectionOperation(
                getResourceLoader(), _classInjectors), Phases.CANONICALIZATION)
        cu
    }

    // Register class to classLoader's private loadedClasses field
    private void setClassLoader() {
        def field = ReflectionUtils.findField(DefaultGrailsApplication.class, "cl")
        field.accessible = true
        // def classLoader = field.get(ApplicationHolder.application)
        field.set Holders.grailsApplication, this
    }
}

class DynamicDomainClassInjector extends DefaultGrailsDomainClassInjector {

    // always true since we're only compiling dynamic domain classes
    @Override
    boolean shouldInject(URL url) { true }

    // always true since we're only compiling dynamic domain classes
    @Override
    protected boolean isDomainClass(ClassNode cn, SourceUnit su) { true }
}