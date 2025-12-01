# Bean-Organized 🌱

Repository created to store our final project in the subject of Back-end

**Tema:** Hábitos e Organização Pessoal

**Desafio:** Auditoria - Todas as transações (inserções, atualizações e exclusões) de uma entidade devem ser auditadas e registradas em um log de auditoria, que deve estar acessível na rota GET /log;

## 👥 Integrantes
- Lucas De Bitencourt Frasson
- Vitor Muneretto Tinelli
- Filipe De Lima Marques

---

## 📋 Sobre o Projeto

Bean-Organized é uma aplicação Spring Boot organizada em arquitetura multi-módulo para gerenciamento de hábitos e organização pessoal. O projeto implementa um sistema completo de auditoria com stack ELK (ElasticSearch, Logstash, Kibana) para rastreamento e visualização de logs.

## 🏗️ Arquitetura

O projeto está estruturado em módulos para melhor organização e separação de responsabilidades:

### Módulos

#### 📦 **Root (Bean-Organized)**
- Módulo pai que gerencia configurações comuns
- Coordena os submódulos
- Define versões e plugins compartilhados

#### 🌐 **API Module**
- Módulo de infraestrutura e apresentação
- Contém controllers REST, configurações e infraestrutura
- **Dependências principais:**
  - Spring Boot Web
  - Spring Boot Validation
  - SpringDoc OpenAPI (Swagger)
  - Logstash Logback Encoder
  - DevTools
  - Lombok

#### 💼 **Domain Module**
- Módulo de lógica de negócio
- Contém entidades, regras de negócio e interfaces de repositório
- **Características:**
  - Independente de frameworks de infraestrutura
  - Foco em regras de domínio
  - Entidades JDBC e Value Objects

#### 🔗 **int-api Module**
- Módulo de integração/contratos (integrações internas ou adaptadores)
- Contém contratos e adaptadores usados para comunicação entre módulos

#### 🧰 **Logstash**
- Contém a configuração do pipeline do Logstash responsável por enviar logs para o ElasticSearch
- Arquivo principal: `logstash/pipeline/logstash.conf`

## 🛠️ Tecnologias

- **Java 21**
- **Spring Boot 3.5.7**
- **Gradle** (Build Tool)
- **PostgreSQL** (Database)
- **Apache Kafka** (Message Streaming)
- **ElasticSearch 9.2.0** (Search and Analytics)
- **Logstash 9.2.0** (Log Pipeline)
- **Kibana 9.2.0** (Data Visualization)
- **SpringDoc OpenAPI** (API Documentation)
- **Lombok** (Code Generation)
- **Docker & Docker Compose** (Containerization)

---

## 🚀 Como Executar

### Pré-requisitos
- **Java 21** instalado
- **Docker** e **Docker Compose** instalados
- **Gradle** (incluído via wrapper)

### Passo a Passo

#### 1. Clone o repositório
```bash
git clone <repository-url>
cd Bean-Organized
```

#### 2. Inicie os serviços do Docker (ELK Stack)
```bash
docker-compose up -d
```

Este comando irá iniciar:
- **ElasticSearch** na porta `9200`
- **Logstash** na porta `5044`
- **Kibana** na porta `5601`

#### 3. Compile o projeto
```bash
./gradlew clean build
```

#### 4. Execute a aplicação
```bash
./gradlew :api:bootRun
```

A aplicação estará disponível em: `http://localhost:8080`

---

## 📡 Acessando os Serviços

### 🔍 Swagger UI (Documentação da API)

Acesse a documentação interativa da API através do Swagger:

```
http://localhost:8080/swagger-ui.html
```

ou

```
http://localhost:8080/swagger-ui/index.html
```

**Recursos disponíveis:**
- Visualização de todos os endpoints da API
- Teste interativo de endpoints
- Schemas de request/response
- Exemplos de uso

### 📊 Kibana (Visualização de Logs)

Acesse o Kibana para visualizar e analisar os logs da aplicação:

```
http://localhost:5601
```

**Primeiro acesso - Configuração do Data View:**

1. Acesse `http://localhost:5601`
2. No menu lateral, vá em **Management** → **Stack Management**
3. Em **Kibana**, clique em **Data Views**
4. Clique em **Create data view**
5. Configure:
   - **Name:** `bean-organized-logs`
   - **Index pattern:** `logs-*`
   - **Timestamp field:** `@timestamp`
6. Clique em **Save data view to Kibana**
7. Agora vá em **Analytics** → **Discover** para visualizar os logs

**Funcionalidades do Kibana:**
- **Discover:** Pesquisa e exploração de logs em tempo real
- **Dashboard:** Criação de dashboards personalizados
- **Visualize:** Criação de visualizações (gráficos, tabelas, etc.)
- **Dev Tools:** Console para queries diretas no ElasticSearch

### 🔎 ElasticSearch (API Direta)

Acesse diretamente o ElasticSearch para queries avançadas:

```
http://localhost:9200
```

**Verificar status:**
```bash
curl http://localhost:9200/_cluster/health
```

**Listar índices:**
```bash
curl http://localhost:9200/_cat/indices?v
```

**Buscar logs:**
```bash
curl http://localhost:9200/logs-*/_search?pretty
```

---

## 🧪 Testando o Sistema de Logs

A aplicação possui endpoints específicos para testar o sistema de auditoria:

### Endpoint de Teste de Logs

**GET** `/v1/logs-test`
- Gera logs de INFO, WARN e ERROR
- Útil para verificar se os logs estão sendo enviados corretamente

**POST** `/v1/logs-test`
- Gera logs de erro com stack trace
- Testa o tratamento de exceções

### Testando via Swagger:
1. Acesse `http://localhost:8080/swagger-ui.html`
2. Localize o controller **Logs Test Controller**
3. Execute os endpoints GET e POST
4. Verifique os logs no Kibana em `http://localhost:5601`

### Testando via cURL:
```bash
# Teste de logs normais
curl http://localhost:8080/v1/logs-test

# Teste de logs de erro
curl -X POST http://localhost:8080/v1/logs-test
```

---

## 📁 Estrutura do Projeto

```
Bean-Organized/
├── api/                        # Módulo de API (Controllers, Config)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── cutepush/beanorganized/api/
│   │   │   │       └── controller/
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── logback-spring.xml
│   │   └── test/
│   └── build.gradle
├── domain/                     # Módulo de Domínio (Entities, Business Logic)
│   ├── src/
│   └── build.gradle
├── logstash/                   # Configurações do Logstash
│   └── pipeline/
│       └── logstash.conf
├── docker-compose.yaml         # Orquestração dos containers
├── build.gradle                # Configuração raiz do Gradle
├── settings.gradle
└── README.md
```

---

## 🔧 Configuração

### Portas Utilizadas

| Serviço        | Porta | URL                              |
|----------------|-------|----------------------------------|
| API Spring     | 8080  | http://localhost:8080            |
| Swagger UI     | 8080  | http://localhost:8080/swagger-ui |
| ElasticSearch  | 9200  | http://localhost:9200            |
| Logstash       | 5044  | tcp://localhost:5044             |
| Kibana         | 5601  | http://localhost:5601            |
| PostgreSQL     | 5432  | tcp://localhost:5432             |
| Kafka          | 9092  | tcp://localhost:9092             |
| Kafdrop        | 9000  | http://localhost:9000            |
| Zookeeper      | 2181  | tcp://localhost:2181             |

### Variáveis de Ambiente (Docker)

O arquivo `docker-compose.yaml` configura:
- **ElasticSearch:** Modo single-node, sem autenticação (desenvolvimento)
- **Logstash:** Pipeline configurado em `./logstash/pipeline/logstash.conf`
- **Kibana:** Conectado ao ElasticSearch automaticamente

---

## 📝 Endpoints Principais

### Logs Test Controller
- **GET** `/v1/logs-test` - Testa logs normais (INFO, WARN, ERROR)
- **POST** `/v1/logs-test` - Testa logs de exceção

### Auditoria
- As informações de auditoria (inserções/atualizações/exclusões) são enviadas para a stack ELK e armazenadas no índice `logs-*` do ElasticSearch. Não há um endpoint HTTP global `GET /log` implementado neste repositório para recuperar toda a auditoria; para análise e exploração dos logs utilize o Kibana (`http://localhost:5601`) ou consulte o ElasticSearch diretamente via API (`logs-*` index). Para testes locais, use os endpoints de teste acima (`/v1/logs-test`).

*(Outros endpoints serão documentados conforme desenvolvimento)*

---

## 🐛 Troubleshooting

### ElasticSearch não inicia
```bash
# Verifique os logs do container
docker logs elasticsearch

# Reinicie o container
docker-compose restart elasticsearch
```

### Logs não aparecem no Kibana
1. Verifique se o Logstash está rodando: `docker ps`
2. Verifique os logs do Logstash: `docker logs logstash`
3. Confirme que o índice existe: `curl http://localhost:9200/_cat/indices?v`
4. Recrie o Data View no Kibana

### Aplicação não conecta ao ElasticSearch
1. Verifique se todos os containers estão rodando: `docker-compose ps`
2. Verifique a rede Docker: `docker network ls`
3. Reinicie os serviços: `docker-compose down && docker-compose up -d`

---

## 📚 Documentação Adicional

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **ElasticSearch Docs:** https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html
- **Kibana Docs:** https://www.elastic.co/guide/en/kibana/current/index.html

---

## 📄 Licença

Este projeto foi desenvolvido para fins educacionais como trabalho final da disciplina de Back-end.

---

## 🤝 Contribuições

Desenvolvido por Lucas De Bitencourt Frasson e Vitor Muneretto Tinelli como projeto final de Back-end.
