# 05 — Arquitetura da V1

**Projeto:** GalaxBank  
**Versão do documento:** 1.0  
**Status:** Fase 2 — Arquitetura e Modelagem

---

> Este documento define como o sistema será construído: padrão arquitetural, organização dos módulos, estrutura de pacotes e decisões técnicas que guiarão o desenvolvimento.

---

## Estilo Arquitetural

### Monólito Modular

O GalaxBank será construído como um monólito modular — uma única aplicação deployada, internamente dividida em módulos com responsabilidades bem definidas e baixo acoplamento entre si.

**Por que monólito modular e não microsserviços:**
- Microsserviços trazem complexidade operacional que não se justifica no início do projeto
- Um monólito bem modularizado migra para microsserviços com muito menos esforço
- É exatamente como sistemas financeiros reais começam na prática

---

## Padrão Arquitetural

### Arquitetura em Camadas

Cada módulo segue o padrão clássico de três camadas, amplamente adotado no mercado com Spring Boot.

```
[ Request HTTP ]
       │
  [ Controller ]     ← Recebe a requisição, valida entrada, chama o Service
       │
   [ Service ]       ← Contém as regras de negócio e orquestra as operações
       │
  [ Repository ]     ← Acessa o banco de dados via Spring Data JPA
       │
  [ PostgreSQL ]
```

**Responsabilidade de cada camada:**

| Camada | Responsabilidade |
|---|---|
| Controller | Receber requisição, validar DTOs, retornar resposta HTTP |
| Service | Aplicar regras de negócio, orquestrar operações, lançar exceções |
| Repository | Persistir e consultar dados no banco |

**Regra fundamental:** Regra de negócio fica no Service, nunca no Controller ou no Repository.

---

## Módulos

| Módulo | Responsabilidade |
|---|---|
| `auth` | Cadastro, autenticação, JWT, bloqueio de conta, senha de transação |
| `account` | Criação e consulta de conta bancária, saldo |
| `transaction` | Depósito, saque, transferência, limite diário, histórico |
| `audit` | Registro imutável de todas as operações do sistema |

**Dependência entre módulos:**
```
auth ← account ← transaction
                      ↓
                    audit
```

- `account` depende de `auth` — conta pertence a um usuário
- `transaction` depende de `account` — transação ocorre sobre uma conta
- `audit` é transversal — registra operações de todos os módulos

---

## Estrutura de Pacotes

```
src/main/java/com/galaxbank/
│
├── auth/
│   ├── controller/         ← AuthController (register, login, logout)
│   ├── service/            ← AuthService (regras de autenticação e bloqueio)
│   ├── repository/         ← UserRepository, TokenBlacklistRepository
│   ├── entity/             ← User, TokenBlacklist (entidades JPA)
│   └── dto/
│       ├── request/        ← RegisterRequest, LoginRequest
│       └── response/       ← LoginResponse, UserResponse
│
├── account/
│   ├── controller/         ← AccountController (saldo, dados da conta)
│   ├── service/            ← AccountService (regras de conta)
│   ├── repository/         ← AccountRepository
│   ├── entity/             ← Account (entidade JPA)
│   └── dto/
│       └── response/       ← AccountResponse, BalanceResponse
│
├── transaction/
│   ├── controller/         ← TransactionController (deposit, withdraw, transfer, history)
│   ├── service/            ← TransactionService (regras de transação e limite diário)
│   ├── repository/         ← TransactionRepository
│   ├── entity/             ← Transaction (entidade JPA)
│   └── dto/
│       ├── request/        ← DepositRequest, WithdrawRequest, TransferRequest
│       └── response/       ← TransactionResponse, HistoryResponse
│
├── audit/
│   ├── service/            ← AuditService (registro de operações)
│   ├── repository/         ← AuditRepository
│   ├── entity/             ← AuditLog (entidade JPA)
│   └── enums/              ← AuditAction (USER_LOGIN, DEPOSIT, etc.)
│
├── security/               ← Configuração Spring Security e filtro JWT
│   ├── JwtFilter.java
│   ├── JwtService.java
│   └── SecurityConfig.java
│
└── shared/                 ← Código compartilhado entre módulos
    ├── exception/          ← Exceções customizadas e @ControllerAdvice
    └── validation/         ← Validações reutilizáveis (CPF)
```

---

## Padrão de Endpoints

Todos os endpoints seguem o prefixo `/api/v1` conforme RNF05.

| Módulo | Base path |
|---|---|
| auth | `/api/v1/auth` |
| account | `/api/v1/accounts` |
| transaction | `/api/v1/transactions` |

### Endpoints previstos

**Auth**
```
POST   /api/v1/auth/register        ← Cadastro de usuário
POST   /api/v1/auth/login           ← Autenticação
POST   /api/v1/auth/logout          ← Logout
```

**Account**
```
GET    /api/v1/accounts/me          ← Dados da conta do usuário autenticado
GET    /api/v1/accounts/me/balance  ← Consulta de saldo
```

**Transaction**
```
POST   /api/v1/transactions/deposit     ← Depósito
POST   /api/v1/transactions/withdraw    ← Saque
POST   /api/v1/transactions/transfer    ← Transferência
GET    /api/v1/transactions/history     ← Histórico paginado
GET    /api/v1/transactions/limit       ← Limite diário disponível
```

---

## Padrão de Resposta de Erro

O sistema adota o padrão **Problem Details (RFC 7807)**, suportado nativamente pelo Spring Boot 4 via `ProblemDetail`.

```json
{
  "type": "https://galaxbank.com/errors/insufficient-balance",
  "title": "Saldo insuficiente",
  "status": 402,
  "detail": "Saldo disponível: R$ 100,00. Valor solicitado: R$ 500,00.",
  "instance": "/api/v1/transactions/withdraw"
}
```

O tratamento de exceções será centralizado via `@ControllerAdvice` no pacote `shared/exception`.

---

## Segurança

### Fluxo de autenticação

```
1. Cliente envia CPF + senha → POST /api/v1/auth/login
2. Sistema valida credenciais
3. Sistema gera token JWT assinado com chave privada RSA
4. Cliente recebe o token
5. Cliente envia token no header: Authorization: Bearer <token>
6. JwtFilter valida o token com a chave pública RSA
7. Requisição é autorizada e processada
```

### Filtro JWT

Um `OncePerRequestFilter` intercepta todas as requisições, extrai o token do header `Authorization`, valida a assinatura RSA e carrega o usuário no contexto de segurança do Spring.

### Endpoints públicos

Os únicos endpoints que não exigem autenticação são:
- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`

---

## Princípios de Código

### Clean Code
- Nomes de classes, métodos e variáveis claros e descritivos
- Métodos pequenos com responsabilidade única
- Sem comentários desnecessários — o código deve se explicar
- Sem duplicação de lógica

### SOLID
| Princípio | Aplicação no projeto |
|---|---|
| **S** — Single Responsibility | Cada classe tem uma única responsabilidade |
| **O** — Open/Closed | Comportamentos extensíveis sem modificar código existente |
| **L** — Liskov Substitution | Subtipos respeitam contratos das interfaces |
| **I** — Interface Segregation | Interfaces pequenas e específicas |
| **D** — Dependency Inversion | Services dependem de abstrações (interfaces de Repository) |

---

## Decisões Técnicas

| Decisão | Escolha | Justificativa |
|---|---|---|
| Chave primária | UUID v4 | Não sequencial, não expõe volume de dados |
| Valores monetários | `BigDecimal` no Java, `NUMERIC(19,2)` no banco | Precisão exata para valores financeiros |
| Fuso horário | UTC no banco, conversão na resposta | Evita inconsistências em ambientes distribuídos |
| Hash de senha | BCrypt (strength 12) | Equilíbrio entre segurança e performance |
| Validação de CPF | Caelum Stella | Algoritmo oficial, sem dependência externa |
| Padrão de erro | Problem Details RFC 7807 | Padrão HTTP oficial, suporte nativo no Spring Boot 4 |

---

## Infraestrutura Local

```
docker-compose.yml
├── galaxbank-app     ← Aplicação Spring Boot
└── galaxbank-db      ← PostgreSQL
```

A aplicação e o banco sobem juntos com:
```bash
docker-compose up -d
```
