# 06 — Modelagem do Banco de Dados

**Projeto:** GalaxBank  
**Versão do documento:** 1.0  
**Status:** Fase 2 — Arquitetura e Modelagem

---

> Este documento define as entidades, atributos, relacionamentos, constraints e índices do banco de dados da V1. Toda alteração futura no schema deve ser feita via migration Flyway.

---

## Decisões Técnicas

| Decisão | Escolha | Justificativa |
|---|---|---|
| Chave primária | `UUID v4` | Não sequencial, não expõe volume de dados |
| Valores monetários | `NUMERIC(19, 2)` | Precisão exata, sem erro de ponto flutuante |
| Timestamps | `TIMESTAMP WITH TIME ZONE` em UTC | Consistência em ambientes distribuídos |
| Soft delete | Não aplicado na V1 | Simplifica a V1; registros financeiros são imutáveis por regra |
| Enums | `VARCHAR` com constraint `CHECK` | Mais legível e portável que enum nativo do PostgreSQL |

---

## Entidades

---

### users

Armazena os dados cadastrais do usuário e informações de segurança de acesso.

| Coluna | Tipo | Constraints | Descrição |
|---|---|---|---|
| `id` | `UUID` | PK, NOT NULL | Identificador único |
| `name` | `VARCHAR(100)` | NOT NULL | Nome completo |
| `cpf` | `VARCHAR(11)` | NOT NULL, UNIQUE | CPF sem formatação |
| `email` | `VARCHAR(150)` | NOT NULL, UNIQUE | E-mail do usuário |
| `password` | `VARCHAR(255)` | NOT NULL | Senha de acesso com hash BCrypt |
| `transaction_password` | `VARCHAR(255)` | NOT NULL | Senha de transação com hash BCrypt |
| `login_attempts` | `INTEGER` | NOT NULL, DEFAULT 0 | Contador de tentativas de login incorretas |
| `locked_until` | `TIMESTAMP WITH TIME ZONE` | NULLABLE | Data/hora até quando a conta está bloqueada |
| `transaction_attempts` | `INTEGER` | NOT NULL, DEFAULT 0 | Contador de tentativas incorretas da senha de transação |
| `transaction_locked_until` | `TIMESTAMP WITH TIME ZONE` | NULLABLE | Data/hora até quando operações financeiras estão bloqueadas |
| `active` | `BOOLEAN` | NOT NULL, DEFAULT TRUE | Indica se o usuário está ativo |
| `created_at` | `TIMESTAMP WITH TIME ZONE` | NOT NULL | Data de criação do registro |
| `updated_at` | `TIMESTAMP WITH TIME ZONE` | NOT NULL | Data da última atualização |

---

### accounts

Armazena a conta bancária do usuário. Cada usuário possui exatamente uma conta.

| Coluna | Tipo | Constraints | Descrição |
|---|---|---|---|
| `id` | `UUID` | PK, NOT NULL | Identificador único |
| `user_id` | `UUID` | FK → users(id), NOT NULL, UNIQUE | Referência ao titular da conta |
| `number` | `VARCHAR(10)` | NOT NULL, UNIQUE | Número da conta gerado automaticamente |
| `balance` | `NUMERIC(19, 2)` | NOT NULL, DEFAULT 0.00, CHECK >= 0 | Saldo atual da conta |
| `active` | `BOOLEAN` | NOT NULL, DEFAULT TRUE | Indica se a conta está ativa |
| `created_at` | `TIMESTAMP WITH TIME ZONE` | NOT NULL | Data de criação do registro |
| `updated_at` | `TIMESTAMP WITH TIME ZONE` | NOT NULL | Data da última atualização |

---

### transactions

Armazena todas as movimentações financeiras: depósitos, saques e transferências.

| Coluna | Tipo | Constraints | Descrição |
|---|---|---|---|
| `id` | `UUID` | PK, NOT NULL | Identificador único da transação |
| `account_id` | `UUID` | FK → accounts(id), NOT NULL | Conta que originou a operação |
| `target_account_id` | `UUID` | FK → accounts(id), NULLABLE | Conta destino (apenas em transferências) |
| `type` | `VARCHAR(20)` | NOT NULL, CHECK IN ('DEPOSIT', 'WITHDRAW', 'TRANSFER') | Tipo da operação |
| `amount` | `NUMERIC(19, 2)` | NOT NULL, CHECK > 0 | Valor da operação |
| `balance_after` | `NUMERIC(19, 2)` | NOT NULL | Saldo da conta origem após a operação |
| `created_at` | `TIMESTAMP WITH TIME ZONE` | NOT NULL | Data e hora da operação |

---

### token_blacklist

Armazena tokens JWT invalidados após logout. Garante que tokens descartados não sejam reutilizados.

| Coluna | Tipo | Constraints | Descrição |
|---|---|---|---|
| `id` | `UUID` | PK, NOT NULL | Identificador único |
| `token` | `TEXT` | NOT NULL, UNIQUE | Token JWT invalidado |
| `expires_at` | `TIMESTAMP WITH TIME ZONE` | NOT NULL | Data de expiração do token |
| `created_at` | `TIMESTAMP WITH TIME ZONE` | NOT NULL | Data em que o token foi invalidado |

---

### audit_logs

Registra todas as operações realizadas no sistema para fins de rastreabilidade. Imutável — nenhum registro pode ser alterado ou excluído.

| Coluna | Tipo | Constraints | Descrição |
|---|---|---|---|
| `id` | `UUID` | PK, NOT NULL | Identificador único |
| `user_id` | `UUID` | FK → users(id), NULLABLE | Usuário que realizou a operação |
| `action` | `VARCHAR(50)` | NOT NULL | Tipo de ação realizada |
| `description` | `TEXT` | NULLABLE | Detalhes adicionais da operação |
| `ip_address` | `VARCHAR(45)` | NULLABLE | IP de origem da requisição |
| `created_at` | `TIMESTAMP WITH TIME ZONE` | NOT NULL | Data e hora do registro |

**Valores previstos para `action`:**
- `USER_REGISTER`
- `USER_LOGIN`
- `USER_LOGIN_FAILED`
- `USER_LOGOUT`
- `USER_LOCKED`
- `DEPOSIT`
- `WITHDRAW`
- `TRANSFER_SENT`
- `TRANSFER_RECEIVED`
- `TRANSACTION_PASSWORD_FAILED`
- `TRANSACTION_LOCKED`

---

## Relacionamentos

```
users 1:1 accounts
  └── Um usuário possui exatamente uma conta

accounts 1:N transactions (account_id)
  └── Uma conta pode ter muitas transações como origem

accounts 1:N transactions (target_account_id)
  └── Uma conta pode receber muitas transferências

users 1:N audit_logs
  └── Um usuário pode ter muitos registros de auditoria
```

---

## Índices

| Tabela | Coluna(s) | Tipo | Justificativa |
|---|---|---|---|
| `users` | `cpf` | UNIQUE | Login e validação de CPF único |
| `users` | `email` | UNIQUE | Validação de e-mail único |
| `accounts` | `user_id` | UNIQUE | Garante uma conta por usuário |
| `accounts` | `number` | UNIQUE | Busca por número de conta na transferência |
| `transactions` | `account_id` | INDEX | Consulta de histórico por conta |
| `transactions` | `created_at` | INDEX | Ordenação e filtro por data |
| `transactions` | `account_id, created_at` | INDEX | Cálculo do limite diário de transferência |
| `token_blacklist` | `token` | UNIQUE | Validação rápida de token invalidado |
| `token_blacklist` | `expires_at` | INDEX | Limpeza periódica de tokens expirados |
| `audit_logs` | `user_id` | INDEX | Consulta de auditoria por usuário |
| `audit_logs` | `created_at` | INDEX | Consulta de auditoria por período |

---

## ERD

```
┌──────────────────────────────┐
│            users             │
├──────────────────────────────┤
│ PK  id UUID                  │
│ UQ  cpf VARCHAR(11)          │
│ UQ  email VARCHAR(150)       │
│     name VARCHAR(100)        │
│     password VARCHAR(255)    │
│     transaction_password     │
│     login_attempts INT       │
│     locked_until TIMESTAMPZ  │
│     transaction_attempts INT │
│     transaction_locked_until │
│     active BOOLEAN           │
│     created_at TIMESTAMPZ    │
│     updated_at TIMESTAMPZ    │
└──────────────┬───────────────┘
               │ 1:1
┌──────────────▼───────────────┐
│           accounts           │
├──────────────────────────────┤
│ PK  id UUID                  │
│ FK  user_id UUID             │
│ UQ  number VARCHAR(10)       │
│     balance NUMERIC(19,2)    │
│     active BOOLEAN           │
│     created_at TIMESTAMPZ    │
│     updated_at TIMESTAMPZ    │
└──────────────┬───────────────┘
               │ 1:N
┌──────────────▼───────────────┐
│         transactions         │
├──────────────────────────────┤
│ PK  id UUID                  │
│ FK  account_id UUID          │
│ FK  target_account_id UUID   │
│     type VARCHAR(20)         │
│     amount NUMERIC(19,2)     │
│     balance_after NUMERIC    │
│     created_at TIMESTAMPZ    │
└──────────────────────────────┘

┌──────────────────────────────┐
│         audit_logs           │
├──────────────────────────────┤
│ PK  id UUID                  │
│ FK  user_id UUID             │
│     action VARCHAR(50)       │
│     description TEXT         │
│     ip_address VARCHAR(45)   │
│     created_at TIMESTAMPZ    │
└──────────────────────────────┘

┌──────────────────────────────┐
│       token_blacklist        │
├──────────────────────────────┤
│ PK  id UUID                  │
│ UQ  token TEXT               │
│     expires_at TIMESTAMPZ    │
│     created_at TIMESTAMPZ    │
└──────────────────────────────┘
```

---

## Migrations Flyway

Ordem das migrations na V1:

| Arquivo | Descrição |
|---|---|
| `V1__create_users_table.sql` | Criação da tabela users |
| `V2__create_accounts_table.sql` | Criação da tabela accounts |
| `V3__create_transactions_table.sql` | Criação da tabela transactions |
| `V4__create_token_blacklist_table.sql` | Criação da tabela token_blacklist |
| `V5__create_audit_logs_table.sql` | Criação da tabela audit_logs |

---