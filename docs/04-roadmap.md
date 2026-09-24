# 04 — Roadmap

**Projeto:** Galax Bank  
**Versão do documento:** 1.0  
**Status:** Fase 1 — Produto e Negócio

---

> O roadmap define a ordem de desenvolvimento das funcionalidades e a evolução do sistema ao longo das versões. Serve como guia de priorização e referência de progresso.

---

## V1.0.0 — Base Funcional

**Objetivo:** Entregar a plataforma funcional com autenticação, operações financeiras e auditoria.

---

### Módulo 1 — Auth

Primeiro módulo a ser desenvolvido pois todos os outros dependem de autenticação.

| # | Funcionalidade | Requisito | Regra |
|---|---|---|---|
| 1.1 | Cadastro de usuário | RF01 | RN01 |
| 1.2 | Autenticação com JWT | RF02 | RN02 |
| 1.3 | Bloqueio por tentativas de login | RF03 | RN02 |
| 1.4 | Logout com invalidação de token | RF02 | RN02 |
| 1.5 | Validação de senha de transação | RF06, RF07 | RN03 |
| 1.6 | Bloqueio por tentativas de senha de transação | — | RN03 |

---

### Módulo 2 — Account

Depende do módulo auth — conta só existe depois que o usuário existe.

| # | Funcionalidade | Requisito | Regra |
|---|---|---|---|
| 2.1 | Criação automática de conta no cadastro | RF01 | RN04 |
| 2.2 | Consulta de saldo | RF04 | RN04 |
| 2.3 | Dados da conta (número, titular) | — | RN04 |

---

### Módulo 3 — Transaction

Depende do módulo account — transações só existem sobre contas.

| # | Funcionalidade | Requisito | Regra |
|---|---|---|---|
| 3.1 | Depósito | RF05 | RN05 |
| 3.2 | Saque | RF06 | RN06 |
| 3.3 | Transferência entre contas | RF07 | RN07 |
| 3.4 | Limite diário de transferência | RF08 | RN08 |
| 3.5 | Histórico de transações paginado | RF09, RF10 | RN09 |

---

### Módulo 4 — Audit

Desenvolvido por último mas registra operações de todos os módulos anteriores.

| # | Funcionalidade | Requisito | Regra |
|---|---|---|---|
| 4.1 | Registro automático de operações | RF11 | RN10 |
| 4.2 | Registro de tentativas de login | — | RN10 |
| 4.3 | Registro de IP de origem | — | RN10 |

---

### Módulo 5 — Infrastructure

Responsabilidades técnicas transversais que suportam todos os módulos de negócio.

| # | Funcionalidade | Requisito | Regra |
|---|---|---|---|
| 5.1 | Documentação da API com Swagger | RF12 | — |
| 5.2 | Containerização com Docker e Docker Compose | — | RNF09 |
| 5.3 | Migrations de banco com Flyway | — | RNF10 |

---

### Entregáveis da V1.0.0

- [ ] API REST funcional com todos os endpoints documentados
- [ ] Autenticação JWT com RSA
- [ ] Operações financeiras (depósito, saque, transferência)
- [ ] Limite diário de transferência
- [ ] Bloqueio por tentativas de login e senha de transação
- [ ] Histórico de transações paginado
- [ ] Auditoria de operações
- [ ] Documentação da API (Swagger)
- [ ] Ambiente containerizado com Docker
- [ ] Migrations de banco com Flyway

**Tag de release:** `v1.0.0`

---

## V1.1.0 — Performance

**Objetivo:** Melhorar a performance da plataforma com cache distribuído.

| # | Funcionalidade |
|---|---|
| 1 | Cache de saldo com Redis |
| 2 | Cache de sessão JWT com Redis |
| 3 | Otimização de queries no histórico |

**Tag de release:** `v1.1.0`

---

## V1.2.0 — Comunicação

**Objetivo:** Introduzir mensageria assíncrona e notificações.

| # | Funcionalidade |
|---|---|
| 1 | Mensageria com Kafka |
| 2 | Notificação de transações realizadas |
| 3 | Notificação de tentativas de login suspeitas |

**Tag de release:** `v1.2.0`

---

## V1.3.0 — Segurança Avançada

**Objetivo:** Reforçar segurança com rate limiting e MFA.

| # | Funcionalidade |
|---|---|
| 1 | Rate limiting por IP e por usuário |
| 2 | Blacklist de tokens JWT |
| 3 | Autenticação multifator (MFA) |
| 4 | Exportação de extrato em PDF |

**Tag de release:** `v1.3.0`

---

## V2.0.0 — Escala

**Objetivo:** Evoluir o monólito modular para arquitetura de microsserviços.

| # | Funcionalidade |
|---|---|
| 1 | Separação em microsserviços (auth, account, transaction, audit) |
| 2 | Observabilidade com Prometheus e Grafana |
| 3 | CI/CD com GitHub Actions |
| 4 | Deploy em nuvem |
| 5 | Mecanismos antifraude |

**Tag de release:** `v2.0.0`

---

## Visão Geral das Versões

| Versão | Foco | Dependência |
|---|---|---|
| v1.0.0 | Base funcional | — |
| v1.1.0 | Performance | v1.0.0 |
| v1.2.0 | Comunicação | v1.1.0 (Redis para Kafka) |
| v1.3.0 | Segurança avançada | v1.0.0 |
| v2.0.0 | Escala e microsserviços | v1.2.0 |

---

## Fluxo de Branches

```
main
 └── v1.0.0
 └── v1.1.0
 └── v2.0.0

develop
 ├── feature/auth
 ├── feature/account
 ├── feature/transaction
 ├── feature/audit
 └── feature/infrastructure
```