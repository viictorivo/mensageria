# Mensageria API

API REST para criação e gerenciamento de mensagens com persistência em MySQL.

## Stack

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 17 |
| Framework | Spring Boot 3.2 |
| Banco | MySQL 8.0 |
| ORM | Spring Data JPA / Hibernate |
| Testes | JUnit 5 + Mockito |
| Container | Docker + Docker Compose |
| Infra | AWS ECS Fargate + RDS MySQL (Terraform) |

---

## Como rodar localmente

### Pré-requisitos
- Docker e Docker Compose instalados
- Java 17+ (opcional, apenas se quiser rodar sem Docker)
- Maven 3.9+ (opcional)

### Opção 1 — Docker Compose (recomendado)

```bash
# Sobe o banco MySQL + a aplicação
docker compose up --build

# A API estará disponível em:
# http://localhost:8080
```

Para parar:
```bash
docker compose down
```

Para parar e remover os volumes (apaga dados do banco):
```bash
docker compose down -v
```

---

### Opção 2 — Rodar só o banco e a app local

```bash
# 1. Sobe só o MySQL
docker compose up db -d

# 2. Rode a aplicação via Maven
./mvnw spring-boot:run
```

---

### Opção 3 — Apenas Java/Maven (com MySQL local instalado)

Crie o banco e usuário no MySQL:
```sql
CREATE DATABASE mensageria;
CREATE USER 'mensageria'@'localhost' IDENTIFIED BY 'mensageria';
GRANT ALL PRIVILEGES ON mensageria.* TO 'mensageria'@'localhost';
FLUSH PRIVILEGES;
```

Rode a aplicação:
```bash
./mvnw spring-boot:run
```

---

## Rodando os testes

```bash
# Todos os testes (usa H2 em memória, não precisa de MySQL)
./mvnw test

# Com relatório
./mvnw test surefire-report:report
```

---

## Endpoints da API

Base URL: `http://localhost:8080/api/mensagens`

### Criar mensagem
```
POST /api/mensagens
Content-Type: application/json

{
  "titulo": "Minha mensagem",
  "conteudo": "Conteúdo da mensagem",
  "remetente": "victor@email.com"
}
```
Retorna `201 Created` com o objeto criado e o `id` gerado.

---

### Buscar por ID
```
GET /api/mensagens/{id}
```
Retorna `200 OK` com a mensagem ou `404 Not Found`.

---

### Listar todas
```
GET /api/mensagens
```
Retorna `200 OK` com array de mensagens.

---

### Buscar por remetente
```
GET /api/mensagens/remetente/{remetente}
```

---

### Atualizar
```
PUT /api/mensagens/{id}
Content-Type: application/json

{
  "titulo": "Título atualizado",
  "conteudo": "Novo conteúdo",
  "remetente": "novo@email.com"
}
```

---

### Deletar
```
DELETE /api/mensagens/{id}
```
Retorna `204 No Content`.

---

### Health check
```
GET /actuator/health
```

---

## Exemplo cURL completo

```bash
# Criar
curl -X POST http://localhost:8080/api/mensagens \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Olá","conteudo":"Primeira mensagem","remetente":"victor@email.com"}'

# Buscar por ID
curl http://localhost:8080/api/mensagens/1

# Listar todas
curl http://localhost:8080/api/mensagens

# Atualizar
curl -X PUT http://localhost:8080/api/mensagens/1 \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Atualizado","conteudo":"Conteúdo novo","remetente":"victor@email.com"}'

# Deletar
curl -X DELETE http://localhost:8080/api/mensagens/1
```

---

## Deploy na AWS (ECS Fargate)

### Pré-requisitos
- AWS CLI configurado (`aws configure`)
- Terraform >= 1.5

### 1. Build e push da imagem para o ECR

```bash
cd infra/terraform
terraform init
terraform apply -target=aws_ecr_repository.app -var="ecr_image_uri=placeholder" -var="db_password=SUA_SENHA"

ECR_URL=$(terraform output -raw ecr_repository_url)
AWS_REGION=us-east-1

aws ecr get-login-password --region $AWS_REGION | \
  docker login --username AWS --password-stdin $ECR_URL

docker build -t $ECR_URL:latest .
docker push $ECR_URL:latest
```

### 2. Deploy completo da infra

```bash
cd infra/terraform

cat > terraform.tfvars <<EOF
db_password   = "SuaSenhaSegura123!"
ecr_image_uri = "SEU_ECR_URL:latest"
EOF

terraform apply
```

### 3. Acessar a aplicação

```bash
terraform output alb_dns_name
# http://<alb_dns_name>/api/mensagens
```

### Destruir a infra
```bash
terraform destroy
```

---

## Variáveis de ambiente da aplicação

| Variável | Padrão | Descrição |
|---|---|---|
| `DB_HOST` | `localhost` | Host do MySQL |
| `DB_PORT` | `3306` | Porta do MySQL |
| `DB_NAME` | `mensageria` | Nome do banco |
| `DB_USER` | `mensageria` | Usuário do banco |
| `DB_PASSWORD` | `mensageria` | Senha do banco |
| `SERVER_PORT` | `8080` | Porta da aplicação |
