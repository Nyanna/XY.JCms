#
# Feature list
#

-No release cycle, just on core updates the core will deployed
-All usecases and data are loaded at runtime from an db with an adapter
-Its fail save to offline compilation/validation/testrun - Process
-An configuration management checks for an present and valid config
-No patching, usecases get on the fly updated in db
-URL's got independently translated to usecases full featuring UTF-8 human semantic or SEO related requests and I18N
-Usecases are encapsulated in "all in one" independent descriptors, with none sideeffects when developing
-No usecase component is ever known to the core, abstracted interfaces
-Environment independent actual CLI, Java and Servlet clients but can be also webstart, applet and standalone
-Markup independent fragment handling and renderkit support on component level
-Running with no config but configurable to its smallest component
-Fully and very easy unit testable, whole sites or single components
-Featuring an strong typesation
-fully opensource and gpled
-easy distributable to partner cooperations
-own lightweight and specalized single class cache implementation (compared to ehcache)
-97KB lines of code, in ram caching of all static and vital parts
-complete portalkits injection are just an adapter load away
-capable of very strong concurrency features
-very high performance out of the box and an gain of 200% when using output caching
-one cloudable codebase to deliver all ever needed frontends on demand in runtime
-DTO transfer of all configuration via JAXB and JPA(eclipselink)
-an small configset gets expanded to an independent configbase so its easy to configure and rely on
-mvn plugin to commit and convert simple xml <> JAXB xml <> jpa context

#
# Phase 2 will cover
#
-usecase templating esp. inheritance for mainlayout, inheritance for usecases ?
-partial component tree rendering & caching - think about rendering only subcomponents pathes, or init without an use case an litle component tree an render an asingle content list
?single usecase controller example
-Local Test coverage
	1. junit tests for isolated components // whats about no markup checks ?
	3. dbrun to check if UCs would run with local build/changed components - implement usecase validation as maven plugin target
?fully functional jj order

#TODO
-xml cdata für fragmente ausserdem attribute sortierung (http://jaxb.java.net/faq/JaxbCDATASample.java), inline fragments via resource laden, obmitted config  bug for enumset db
-performance optimizations for live db system and loading, caching and flush needed
-weighted textscaling in lib
-typeconverters should support valueOf type Object
-automated unittest for each specified usecase
-access model from controller to the configs inclusive caching, and proccessing caching like setting messages ad append content, something like an result closure from content method not just one content, configuration manager
-config item with factory, declare staticly Item(key,default,hint), ControllerConfig.get(itemList) factory gets instance, than set globals,params,binding, c.get("service"); in progress
-tomcat reload memleak on threads check shutdown
#jj
-highlight current nalkey for navigation flag
-when cat contains sublevel make lvl1 page else lvl2
-hotlist auslagern specials auslaggern specials navi auslagern
-componentless fw-web deploy with db only, with mvn excclusion?

#
# Phase 3 will cover
#
-site personalisation examples
-Staging Concept/Implementation
-Versioning Concept/Implementation
-Test coverage, output mask validation
?an browser based usecase configurator


Caused by: java.lang.IllegalArgumentException: Cant destinguish the converter type to convert into string representation
.
        at net.xy.jcms.controller.configurations.UIConfiguration.toDTO(UIConfiguration.java:429)
        at net.xy.jcms.controller.usecase.Usecase.toDTO(Usecase.java:246)
        at net.xy.jcms.persistence.PersistenceHelper$DB.saveUsecase(PersistenceHelper.java:149)
        at net.xy.jcms.mvn.ConfigExporter.execute(ConfigExporter.java:271)
        ... 21 more
[ERROR]
[ERROR] Re-run Maven using the -X switch to enable full debug logging.
[ERROR]
[ERROR] For more information about the errors and possible solutions, please read the following articles:
[ERROR] [Help 1] http://cwiki.apache.org/confluence/display/MAVEN/PluginExecutionException
L:\Matrice\fw-web>cls && mvn exporter:export -Dexporter.in.usecase.dir=uc -Dexporter.out.usecase.jpa=fw-web -Dexporter.s
impleXml=false -e