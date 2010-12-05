
#
# Feature list
#

-No release cycle, just on core updates the core will deployed
-All usecases and data are loaded at runtime from an db with an adapter
-Its fail save to offline compilation/validation/testrun - Process
-An configuration management checks for an present and valid config
-No patching, usecases get on the fly updated in db
-URL's got independently translated to usecases full featuring UTF-8 human semantic or SEO related requests and I18N
-Usecases are encapsulated in all in one independent descriptors, with at least to none sideeffects when developing
-No usecase component is ever known to the core, abstracted interfaces
-Environment independed actual CLI, Java and Servlet clients
-Markup independent fragment handling and renderkit support on component level
-Running with no config but configurable to its smallest component
-Fully and very easy unit testable, whole sites or single components
-Featuring an strong typesation
-fully opensource and gpled
-easy distributable to partner cooperations
-own lightweight and specalized single class cache implementation (compared to ehcache)
-97KB lines of code, in ram caching of all static and vital parts

#
# Phase 2 will cover (~64 MH +/-50%)
#

-the db schemata and connectors
-an browser based usecase configurator
-partial component tree rendering
-single usecase controller example
-generic controller managed configuration
-componenttree caching support
-CLI tool insert UC from xml
-CLI tool insert ranslation from xml
-Local Test coverage
	1. junit tests for isolated components
	2. junit tests for rendering predefined usecases
	3. dbrun to check if UCs would run with local build/changed components

-more jj usecases and components, inclusive fully functional jj order


#
# Phase 3 will cover
#
-site personalisation examples
-Staging Concept/Implementation
-Versioning Concept/Implementation
-Test coverage, output mask validation



#
# Concept Release cycle
#

#Developer
1. develope
2. run local test coverage > checkin
3. diff got commited in stage db

#Manager
1. create usecase from components
2. engine validates UC
3. checkin into stage db

#Both
4. ci runs local test coverage
5. qa runs diff tests
6. stage db envolves