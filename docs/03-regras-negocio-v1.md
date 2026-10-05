# 03 — Regras de Negócio da V1

**Projeto:** Galax Bank  
**Versão do documento:** 1.0  
**Status:** Fase 1 — Produto e Negócio

---

> Regras de negócio definem as condições, restrições e comportamentos que o sistema deve respeitar para que as operações sejam válidas. São independentes de tecnologia — valem para qualquer implementação.

---

## RN01 — Cadastro de Usuário

- O CPF deve ser válido segundo o algoritmo de validação oficial (dígitos verificadores)
- O CPF não pode estar cadastrado em outra conta
- O e-mail não pode estar cadastrado em outra conta
- A senha de acesso deve ter no mínimo 8 caracteres
- A senha de transação deve conter exatamente 4 dígitos numéricos
- A senha de transação não pode ser igual à senha de acesso
- Ao concluir o cadastro, uma conta bancária é criada automaticamente com saldo inicial de R$ 0,00
- Um número de conta único é gerado automaticamente pelo sistema

---

## RN02 — Autenticação

- O login deve ser feito com CPF e senha de acesso
- CPF ou senha incorretos devem retornar a mesma mensagem de erro genérica, sem indicar qual dos dois está errado
- Após 5 tentativas consecutivas de login com senha incorreta, a conta é bloqueada por 30 minutos
- O contador de tentativas é zerado após um login bem-sucedido
- O token JWT gerado tem validade de 24 horas
- Após o logout, o token é invalidado e não pode ser reutilizado

---

## RN03 — Senha de Transação

- A senha de transação é obrigatória para saque e transferência
- Após 5 tentativas consecutivas incorretas da senha de transação, as operações financeiras da conta são bloqueadas por 30 minutos
- O contador de tentativas da senha de transação é zerado após uma confirmação bem-sucedida
- O bloqueio da senha de transação não impede o acesso à conta — o usuário ainda pode consultar saldo e extrato
- A senha de transação pode ser alterada pelo usuário autenticado mediante confirmação da senha de acesso atual

---

## RN04 — Conta Bancária

- Cada usuário possui exatamente uma conta bancária
- O número da conta é gerado automaticamente pelo sistema e é único
- O saldo inicial da conta é R$ 0,00
- O saldo não pode ser negativo em nenhuma circunstância
- Uma conta só pode ser operada pelo seu titular

---

## RN05 — Depósito

- O valor do depósito deve ser maior que R$ 0,00
- Não há valor máximo para depósito na V1
- O saldo é atualizado imediatamente após o depósito
- Todo depósito gera um registro no histórico de transações

---

## RN06 — Saque

- O valor do saque deve ser maior que R$ 0,00
- O usuário deve ter saldo suficiente para cobrir o valor do saque
- A confirmação da senha de transação é obrigatória antes de efetivar o saque
- O saldo é atualizado imediatamente após o saque
- Todo saque gera um registro no histórico de transações

---

## RN07 — Transferência

- O valor da transferência deve ser maior que R$ 0,00
- O usuário deve ter saldo suficiente para cobrir o valor da transferência
- A transferência é identificada pelo número da conta do destinatário
- Não é permitido transferir para a própria conta
- A conta destinatária deve existir e estar ativa no sistema
- A confirmação da senha de transação é obrigatória antes de efetivar a transferência
- O saldo da conta origem é debitado e o saldo da conta destino é creditado de forma atômica — ou os dois acontecem, ou nenhum acontece
- A transferência gera registro no histórico de ambas as contas (débito na origem, crédito no destino)

---

## RN08 — Limite Diário de Transferência

- O limite padrão de transferência é de R$ 5.000,00 por dia
- O limite é calculado com base na soma de todas as transferências realizadas no dia corrente (00:00 às 23:59)
- Depósitos e saques não entram no cálculo do limite diário
- Ao atingir o limite, novas transferências são bloqueadas até o início do dia seguinte
- O sistema deve informar o valor disponível do limite ao usuário quando ele tentar realizar uma transferência

---

## RN09 — Histórico de Transações

- O histórico exibe todas as movimentações da conta: depósitos, saques e transferências
- Cada registro exibe: tipo da operação, valor, data e hora, e saldo após a operação
- Transferências recebidas aparecem como crédito; transferências enviadas aparecem como débito
- O histórico é ordenado do mais recente para o mais antigo
- O histórico é paginado com 20 itens por página por padrão e máximo de 100 por página
- Registros do histórico não podem ser editados ou excluídos

---

## RN10 — Auditoria

- Toda operação realizada no sistema gera um registro de auditoria automático
- O registro de auditoria contém: usuário, tipo de operação, data, hora e IP de origem
- Registros de auditoria são imutáveis — não podem ser editados ou excluídos
- A auditoria é independente do histórico de transações — são registros distintos com propósitos distintos

---

## RN11 — Segurança de Dados

- Senhas de acesso e senhas de transação são sempre armazenadas com hash — nunca em texto puro
- O sistema nunca retorna senhas em nenhuma resposta da API
- O CPF é armazenado e pode ser usado para login, mas não é exibido integralmente em respostas da API
- Tokens JWT expirados ou invalidados são rejeitados em qualquer endpoint protegido

---

## Tabela Resumo das Regras

| ID | Área | Resumo |
|---|---|---|
| RN01 | Cadastro | CPF válido e único, e-mail único, senhas distintas, conta criada automaticamente |
| RN02 | Autenticação | Login por CPF, bloqueio após 5 tentativas por 30 min, token JWT de 24h |
| RN03 | Senha de transação | Obrigatória para saque e transferência, bloqueio após 5 tentativas por 30 min |
| RN04 | Conta bancária | Uma conta por usuário, saldo nunca negativo |
| RN05 | Depósito | Valor maior que zero, sem limite máximo |
| RN06 | Saque | Saldo suficiente, senha de transação obrigatória |
| RN07 | Transferência | Por número de conta, atômica, senha de transação obrigatória |
| RN08 | Limite diário | R$ 5.000,00 por dia em transferências |
| RN09 | Histórico | Imutável, paginado, ordenado do mais recente |
| RN10 | Auditoria | Registro automático e imutável de todas as operações |
| RN11 | Segurança | Senhas sempre com hash, dados sensíveis protegidos nas respostas |
