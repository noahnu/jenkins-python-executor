.PHONY: build
build:
	mvn install

.PHONY: package
package:
	mvn package

.PHONY: debug
debug:
	MAVEN_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8000,suspend=n" mvn hpi:run

.PHONY: start-jenkins
start-jenkins:
	docker-compose up local_jenkins

.PHONY: stop-jenkins
stop-jenkins:
	docker-compose down

.PHONY: show-password
show-password:
	@docker-compose exec local_jenkins cat /var/jenkins_home/secrets/initialAdminPassword

.PHONY: open-jenkins
open-jenkins:
	open http://localhost:8080/jenkins

.PHONY: tools
tools:
	mkdir -p tools
