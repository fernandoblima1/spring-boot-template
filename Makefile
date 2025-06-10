configure:
	@ echo '📋 Criando arquivo .env a partir do env.example...'
	@ [ ! -f ".env" ] && cp .env.example .env || true
	@ echo '🔧-Dando permissao de execucao ao binario mvnw...'
	@ chmod +x mvnw
	@ echo '🪝-Adicionando hook de pre-commit...'
	@ [ -d ".git" ] && cp scripts/pre-commit .git/hooks/ && chmod +x .git/hooks/pre-commit && echo '✅-Hook de pre-commit copiado' || true

start: lint
	@ echo '🚀-Iniciando aplicação...'
	@ export $$(cat .env | grep -v '^#' | xargs) && ./mvnw spring-boot:run

test: configure
	@ echo '🧪-Executando testes...'
	@ export $$(cat .env | grep -v '^#' | xargs) && ./mvnw test

package: configure
	@ echo '📦-Empacotando aplicação...'
	@ export $$(cat .env | grep -v '^#' | xargs) && ./mvnw clean package

sonar: configure
	@ echo '🔍-Executando análise de qualidade com SonarQube...'
	@ export $$(cat .env | grep -v '^#' | xargs) && ./mvnw verify sonar:sonar -Dsonar.qualitygate.wait=true

lint: configure
	@ echo '✨-Aplicando formatação de código...'
	@ export $$(cat .env | grep -v '^#' | xargs) && ./mvnw spotless:apply

migrate: configure
	@ echo '🗄️-Executando migrações do banco de dados...'
	@ export $$(cat .env | grep -v '^#' | xargs) && ./mvnw flyway:migrate

generate-secret:
	@ echo '🔑-Gerando secret key...'
	@ JWT_SECRET=$$(openssl rand -base64 32) && \
	  echo "JWT_SECRET gerada: $$JWT_SECRET" && \
	  if [ -f ".env" ]; then \
	    if grep -q "^JWT_SECRET=" .env; then \
	      grep -v "^JWT_SECRET=" .env > .env.tmp && mv .env.tmp .env && \
	      echo "JWT_SECRET=\"$$JWT_SECRET\"" >> .env && \
	      echo '✅-JWT_SECRET atualizada no arquivo .env'; \
	    else \
	      echo "JWT_SECRET=\"$$JWT_SECRET\"" >> .env && \
	      echo '✅-JWT_SECRET adicionada ao arquivo .env'; \
	    fi; \
	  else \
	    echo '⚠️-Arquivo .env não encontrado. Execute "make configure" primeiro'; \
	  fi