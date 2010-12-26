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
# Phase 2 will cover (~40 MH +/-50%)
#
-usecase templating esp. inheritance for mainlayout
-partial component tree rendering & caching - think about rendering only subcomponents pathes, or init without an use case an litle component tree an render an asingle content list
?single usecase controller example
-Local Test coverage
	1. junit tests for isolated components // whats about no markup checks ?
	3. dbrun to check if UCs would run with local build/changed components - implement usecase validation as maven plugin target
?fully functional jj order

#TODO
-xml cdata für fragmente ausserdem attribute sortierung (http://jaxb.java.net/faq/JaxbCDATASample.java), inline fragments via resource laden, obmitted config  bug for enumset db

#
# Phase 3 will cover
#
-site personalisation examples
-Staging Concept/Implementation
-Versioning Concept/Implementation
-Test coverage, output mask validation
?an browser based usecase configurator


