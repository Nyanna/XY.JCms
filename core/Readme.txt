
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
?an browser based usecase configurator
?partial component tree rendering & caching
?single usecase controller example
?generic controller managed configuration
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

#
# Performance on fmd-xyan with one thread for 100 reqs, for rendering the startpage, just the service calls are cached, no frontent code is cached
# req: http://localhost:8080/fw-web/
123863 [http-8080-exec-2] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 13 Μ
123881 [http-8080-exec-3] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 11 Μ
123900 [http-8080-exec-1] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 12 Μ
123915 [http-8080-exec-8] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 10 Μ
123934 [http-8080-exec-4] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 12 Μ
123952 [http-8080-exec-9] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 11 Μ
123970 [http-8080-exec-5] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 11 Μ
123989 [http-8080-exec-6] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 13 Μ
124007 [http-8080-exec-7] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 12 Μ
124027 [http-8080-exec-10] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 13 Μ
124081 [http-8080-exec-2] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 47 Μ
124104 [http-8080-exec-3] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 16 Μ
124123 [http-8080-exec-1] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 12 Μ
124143 [http-8080-exec-8] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 14 Μ
124172 [http-8080-exec-4] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 12 Μ
124190 [http-8080-exec-9] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 13 Μ
124208 [http-8080-exec-5] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 12 Μ
124226 [http-8080-exec-6] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 12 Μ
124244 [http-8080-exec-7] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 11 Μ
124265 [http-8080-exec-10] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 12 Μ
124283 [http-8080-exec-2] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 11 Μ
124302 [http-8080-exec-3] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 13 Μ
#with enabled config dependent render output cache
# req: http://localhost:8080/fw-web/Willkommen?cache=600
975992 [http-8080-exec-8] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 4 Μ
976003 [http-8080-exec-4] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 3 Μ
976011 [http-8080-exec-9] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 4 Μ
976021 [http-8080-exec-5] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 3 Μ
976030 [http-8080-exec-6] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 4 Μ
976046 [http-8080-exec-7] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 12 Μ
976053 [http-8080-exec-10] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 3 Μ
976061 [http-8080-exec-2] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 4 Μ
976069 [http-8080-exec-3] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 4 Μ
976077 [http-8080-exec-1] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 5 Μ
976085 [http-8080-exec-8] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 4 Μ
976105 [http-8080-exec-4] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 17 Μ
976130 [http-8080-exec-9] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 4 Μ
976138 [http-8080-exec-5] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 4 Μ
976145 [http-8080-exec-6] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 4 Μ
976152 [http-8080-exec-7] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 3 Μ
976160 [http-8080-exec-10] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 3 Μ
976170 [http-8080-exec-2] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 4 Μ
976177 [http-8080-exec-3] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 3 Μ
976185 [http-8080-exec-1] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 4 Μ
976192 [http-8080-exec-8] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 3 Μ
976200 [http-8080-exec-4] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 3 Μ
976215 [http-8080-exec-9] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 8 Μ
976229 [http-8080-exec-5] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 3 Μ
976237 [http-8080-exec-6] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 4 Μ
976246 [http-8080-exec-7] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 5 Μ
976258 [http-8080-exec-10] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 8 Μ
976283 [http-8080-exec-2] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 19 Μ
976292 [http-8080-exec-3] INFO  de.jamba.MainServlet  - Execution succeeded in milliseconds 4 Μ
#maggie fw under same condition with req: http://localhost.www.jamba.de:8001/fw/
DEBUG 2010-12-15 13:12:57,320 [http-0.0.0.0-8001-7][09ac5878-4351-42c4-baa1-f3c5ecec3d23][] de.jamba.frontend.library.protocoladapter.impl.ServletFilter 'Execution succeded in 130 milliseconds.'
DEBUG 2010-12-15 13:12:57,423 [http-0.0.0.0-8001-7][c6defc98-5167-436d-b697-02a14c218adc][] de.jamba.frontend.library.protocoladapter.impl.ServletFilter 'Execution succeded in 98 milliseconds.'
DEBUG 2010-12-15 13:12:57,535 [http-0.0.0.0-8001-7][c2cfe366-fa3c-4997-b0d9-bf7a769dd2b5][] de.jamba.frontend.library.protocoladapter.impl.ServletFilter 'Execution succeded in 108 milliseconds.'
DEBUG 2010-12-15 13:12:57,650 [http-0.0.0.0-8001-7][8b24095d-64e9-4036-892a-567e59189c18][] de.jamba.frontend.library.protocoladapter.impl.ServletFilter 'Execution succeded in 112 milliseconds.'
DEBUG 2010-12-15 13:12:57,774 [http-0.0.0.0-8001-7][5b1e5755-1f72-490f-82bd-c85dfc1b663b][] de.jamba.frontend.library.protocoladapter.impl.ServletFilter 'Execution succeded in 119 milliseconds.'
DEBUG 2010-12-15 13:12:57,912 [http-0.0.0.0-8001-7][d416255e-9f2f-4e8d-ac97-2b6a74c693d9][] de.jamba.frontend.library.protocoladapter.impl.ServletFilter 'Execution succeded in 133 milliseconds.'
DEBUG 2010-12-15 13:12:58,024 [http-0.0.0.0-8001-7][ecf1c9a5-0bc1-4afa-984f-29535002f039][] de.jamba.frontend.library.protocoladapter.impl.ServletFilter 'Execution succeded in 108 milliseconds.'
DEBUG 2010-12-15 13:12:58,138 [http-0.0.0.0-8001-7][ee017dfc-27bf-40f3-b93a-473c2a2a6972][] de.jamba.frontend.library.protocoladapter.impl.ServletFilter 'Execution succeded in 110 milliseconds.'
DEBUG 2010-12-15 13:12:58,259 [http-0.0.0.0-8001-7][5b445f03-4909-43aa-928b-44215375c452][] de.jamba.frontend.library.protocoladapter.impl.ServletFilter 'Execution succeded in 115 milliseconds.'
DEBUG 2010-12-15 13:12:58,385 [http-0.0.0.0-8001-7][4e422156-e167-46cc-b9cd-3d5dde0ca5d1][] de.jamba.frontend.library.protocoladapter.impl.ServletFilter 'Execution succeded in 121 milliseconds.'
DEBUG 2010-12-15 13:12:58,509 [http-0.0.0.0-8001-7][26c16c1a-316f-4aa1-932b-11f6bc91065f][] de.jamba.frontend.library.protocoladapter.impl.ServletFilter 'Execution succeded in 120 milliseconds.'
#Björn Clemens prototype actual contains no comparable usecase like startpage

