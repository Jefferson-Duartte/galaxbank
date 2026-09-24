# 02 — Requisitos da V1

**Projeto:** Galax Bank  
**Versão do documento:** 1.0  
**Status:** Fase 1 — Produto e Negócio

---

## Requisitos Funcionais

Requisitos funcionais descrevem **o que o sistema deve fazer**.

---

### RF01 — Cadastro de usuário

O sistema deve permitir que novos usuários se cadastrem informando nome completo, CPF, e-mail, senha e senha de transação.

- O CPF deve ser único no sistema e válido
- O e-mail deve ser único no sistema
- A senha deve ter no mínimo 8 caracteres
- A senha de transação deve conter exatamente 4 dígitos numéricos
- A senha de transação deve ser armazenada separadamente da senha de acesso
- Uma conta bancária deve ser criada automaticamente após o cadastro

---

### RF02 — Autenticação

O sistema deve permitir que usuários acessem a plataforma informando CPF e senha.

- Em caso de sucesso, o sistema deve retornar um token JWT
- O token deve ter prazo de expiração
- Após o logout, o token deve ser invalidado

---

### RF03 — Bloqueio por tentativas de login

O sistema deve bloquear temporariamente o acesso de um usuário após tentativas consecutivas de login com senha incorreta.

- Após 5 tentativas incorretas, a conta deve ser bloqueada
- O bloqueio deve durar 30 minutos
- O usuário deve ser informado do bloqueio e do tempo restante

---

### RF04 — Consulta de saldo

O sistema deve permitir que o usuário autenticado consulte o saldo atual da sua conta bancária.

---

### RF05 — Depósito

O sistema deve permitir que o usuário autenticado deposite um valor em sua própria conta.

- O valor deve ser maior que zero
- O saldo deve ser atualizado imediatamente após o depósito
- A operação deve ser registrada no histórico

---

### RF06 — Saque

O sistema deve permitir que o usuário autenticado saque um valor da sua conta.

- O valor deve ser maior que zero
- O usuário deve ter saldo suficiente para realizar o saque
- A confirmação da senha de transação é obrigatória
- A operação deve ser registrada no histórico

---

### RF07 — Transferência entre contas

O sistema deve permitir que o usuário autenticado transfira um valor para outra conta cadastrada no sistema.

- O valor deve ser maior que zero
- O usuário deve ter saldo suficiente para realizar a transferência
- Não deve ser permitido transferir para a própria conta
- A confirmação da senha de transação é obrigatória
- A operação deve ser registrada no histórico de ambas as contas

---

### RF08 — Limite diário de transferência

O sistema deve limitar o valor total transferido por um usuário dentro de um mesmo dia.

- O limite padrão é de R$ 5.000,00 por dia
- Ao atingir o limite, novas transferências devem ser bloqueadas até o dia seguinte
- O usuário deve ser informado do limite disponível ao tentar transferir

---

### RF09 — Histórico de transações

O sistema deve permitir que o usuário autenticado consulte o histórico de transações da sua conta.

- O histórico deve exibir: tipo da operação, valor, data e hora, e saldo após a operação
- As transações devem ser ordenadas da mais recente para a mais antiga
- O histórico deve ser paginado

---

### RF10 — Paginação e ordenação

O sistema deve suportar paginação e ordenação em todas as listagens da API.

- O cliente deve poder informar o número da página e a quantidade de itens por página
- O tamanho padrão de página é 20 itens
- O tamanho máximo de página é 100 itens

---

### RF11 — Auditoria de operações

O sistema deve registrar automaticamente todas as operações realizadas para fins de rastreabilidade.

- Deve ser registrado: usuário, tipo de operação, data, hora e IP de origem
- Os registros de auditoria não podem ser editados ou excluídos
- O administrador poderá consultar os logs de auditoria em versões futuras

---

### RF12 — Documentação da API

O sistema deve disponibilizar documentação interativa de todos os endpoints da API.

---

## Requisitos Não Funcionais

Requisitos não funcionais descrevem **como o sistema deve se comportar**.

---

### RNF01 — Segurança de senhas

As senhas de acesso devem ser armazenadas com hash BCrypt. As senhas de transação também devem ser armazenadas com hash. Nenhuma senha pode ser armazenada em texto puro.

---

### RNF02 — Autenticação stateless

A autenticação deve ser implementada com JWT assinado com chave RSA. O servidor não deve armazenar estado de sessão.

---

### RNF03 — Proteção de endpoints

Todos os endpoints, exceto cadastro e login, devem exigir token JWT válido para serem acessados.

---

### RNF04 — Tempo de resposta

A API deve responder em até 500ms para operações de consulta e até 1000ms para operações de escrita em condições normais de uso.

---

### RNF05 — Versionamento da API

A API deve ser versionada. Todos os endpoints devem estar sob o prefixo `/api/v1`.

---

### RNF06 — Integridade das transações

Operações financeiras devem ser executadas dentro de transações de banco de dados, garantindo que em caso de falha nenhuma operação parcial seja persistida.

---

### RNF07 — Rastreabilidade

Toda operação financeira deve gerar um identificador único (UUID) para fins de rastreabilidade e suporte.

---

### RNF08 — Logs da aplicação

A aplicação deve registrar logs estruturados para todas as operações relevantes, erros e exceções.

---

### RNF09 — Containerização

A aplicação e suas dependências devem rodar em containers Docker, garantindo padronização do ambiente.

---

### RNF10 — Versionamento de banco de dados

Todas as alterações no schema do banco de dados devem ser controladas via migrations com Flyway.

---

## Resumo dos Requisitos

| ID | Descrição | Tipo |
|---|---|---|
| RF01 | Cadastro de usuário | Funcional |
| RF02 | Autenticação | Funcional |
| RF03 | Bloqueio por tentativas de login | Funcional |
| RF04 | Consulta de saldo | Funcional |
| RF05 | Depósito | Funcional |
| RF06 | Saque | Funcional |
| RF07 | Transferência entre contas | Funcional |
| RF08 | Limite diário de transferência | Funcional |
| RF09 | Histórico de transações | Funcional |
| RF10 | Paginação e ordenação | Funcional |
| RF11 | Auditoria de operações | Funcional |
| RF12 | Documentação da API | Funcional |
| RNF01 | Segurança de senhas | Não Funcional |
| RNF02 | Autenticação stateless | Não Funcional |
| RNF03 | Proteção de endpoints | Não Funcional |
| RNF04 | Tempo de resposta | Não Funcional |
| RNF05 | Versionamento da API | Não Funcional |
| RNF06 | Integridade das transações | Não Funcional |
| RNF07 | Rastreabilidade | Não Funcional |
| RNF08 | Logs da aplicação | Não Funcional |
| RNF09 | Containerização | Não Funcional |
| RNF10 | Versionamento de banco de dados | Não Funcional |