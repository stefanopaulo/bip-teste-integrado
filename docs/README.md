# 🏗️ Desafio Fullstack Integrado

Este projeto é uma solução completa para gerenciamento e transferência de benefícios, composta por uma API em Spring Boot e uma interface em Angular.

A documentação detalhada e o processso de desenvolvimento de cada parte se encontram dentro das pastas filhas desse repositório (backend-module e frontend).

## Tecnologias Principais
- Backend: Java 17, Spring Boot 3, Spring Data JPA, PostgreSQL.

- Frontend: Angular 21, Nginx (Docker).

- DevOps: Docker, Docker Compose.

---

## Como Rodar com Docker
A forma mais rápida de testar a aplicação é utilizando o Docker Compose. Ele subirá o Banco de Dados com o schema já populado pelos arquivos da pasta /db, a API e o Frontend.

Na raiz do projeto, execute:

```bash
docker compose up -d
```

Acessos:

- Frontend: http://localhost:4222

- Backend (Swagger): http://localhost:8085/swagger-ui.html

*Nota sobre portas: Por padrão, a API está exposta na porta 8085 para evitar conflitos com outros serviços locais (como o Tomcat padrão na 8080). Caso precise alterar, edite o arquivo .env na raiz.*

---

Se quiser apagar os containers depois:

```bash
docker compose down -v
```

Isso apagará os containers e volumes criados por esse projeto.

## Como Rodar Manualmente

Caso prefira rodar o projeto manualmente ou via IDE, certifique-se desses passos:

1. Banco de Dados (PostgreSQL)
  - Crie um banco chamado beneficios_db.

2. Backend
  - Ajuste as credenciais do banco no arquivo application.properties.

3. Frontend
  - Certifique-se de que a apiUrl em src/environments/environment.ts aponta para a porta correta da API.

## Nota final

Qualquer outra dúvida sobre o projeto, me encontro a disposição.

---

## 👨‍💻 Autor

**Stefano Souza**
