# Backend (Benefícios API)
Esta é a API de gerenciamento de benefícios, desenvolvida como parte do desafio técnico para correção e evolução do sistema legado de transferências.

## Tecnologias Utilizadas
- Java 17 (Spring Boot 3.2.5)

- PostgreSQL

- Spring Data JPA

- Spring Retry

- Bean Validation

- OpenAPI 3 / Swagger

- JUnit 5 / Mockito

---

## Correção do Bug Crítico (EJB)
O código original no BeneficioEjbService apresentava falhas graves de consistência. A nova implementação no BeneficioService resolveu:

- Lost Update: Implementado Optimistic Locking com a anotação @Version na entidade. Caso dois processos tentem atualizar o mesmo saldo simultaneamente, o Spring Retry realiza até 3 tentativas automáticas (@Retryable).

- Inconsistência de Saldo: Adicionada validação de negócio para impedir transferências de valores maiores que o saldo disponível (SaldoInsuficienteException).

- Atomicidade: Uso de @Transactional para garantir que a operação de débito e crédito ocorra como uma única unidade. Se um falhar, o outro sofre rollback.

- Validações de Entrada: Verificação de IDs nulos, IDs idênticos e valores negativos ou zerados.

---

## Endpoints Principais
- GET /api/v1/beneficios: Lista paginada de benefícios ativos.

- POST /api/v1/beneficios: Cadastro de novo benefício.

- POST /api/v1/beneficios/transferir: Realiza transferência entre contas (Locking Otimista).

- PUT /api/v1/beneficios/{id}: Atualização de dados.

- DELETE /api/v1/beneficios/{id}: Soft delete (desativação).

---

## Testes Implementados
- Unitários (Service): Cobertura completa da lógica de transferência e CRUD seguindo o padrão Given-When-Then.

- Integração (Controller): Validação do fluxo completo do endpoint de transferência, garantindo a integração correta com o banco de dados.

---

## Como rodar o Backend
Siga os passos descritos no README.md do repositório pai desse projeto para subir todo o contexto da aplicação.

Acesse o Swagger em: http://localhost:8085/swagger-ui.html
