configure:
	@ echo 'Criando arquivo .env a partir do env.exemplo...'
	@ [ ! -f ".env" ] && cp .env.exemplo .env || true
	@ echo 'Dando permissao de execucao ao binario mvnw...'
	@ chmod +x mvnw
	# @ echo 'Adicionando hook de pre-push para sonar - apenas em ambientes linux...'
	# @ [ -d ".git" ] && cp scripts/pre-push .git/hooks/ && chmod +x .git/hooks/pre-push && echo 'Hook de sonar copiado' || true

start: configure
	@ export $$(cat .env | grep -v '^#' | xargs) && ./mvnw spring-boot:run

test: configure
	@ export $$(cat .env | grep -v '^#' | xargs) && ./mvnw test

package: configure
	@ export $$(cat .env | grep -v '^#' | xargs) && ./mvnw clean package

sonar: configure
	@ export $$(cat .env | grep -v '^#' | xargs) && ./mvnw verify sonar:sonar -Dsonar.qualitygate.wait=true
