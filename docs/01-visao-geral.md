# 01 — Visão Geral

**Projeto:** Galax Bank (Sistema Financeiro)  
**Versão do documento:** 1.0  
**Status:** Fase 1 — Produto e Negócio

---

## Descrição do Sistema

O Galax Bank é uma projeto backend inspirado em bancos digitais modernos como Nubank e PicPay. O sistema permite que usuários realizem operações financeiras de forma segura, incluindo autenticação, gerenciamento de contas, transferências, depósitos, saques e consulta de extrato.

O projeto simula um ambiente corporativo real de desenvolvimento backend, aplicando padrões e práticas amplamente adotados no mercado financeiro.

---

## Objetivo Principal

Desenvolver um sistema financeiro backend profissional, aplicando conceitos modernos de arquitetura, segurança robusta e boas práticas de engenharia de software.

---

## Objetivos do Projeto

- Permitir que usuários se cadastrem e autentiquem na plataforma com segurança.
- Oferecer gerenciamento completo de contas bancárias digitais.
- Possibilitar a realização de operações financeiras: depósitos, saques e transferências.
- Garantir rastreabilidade e auditoria de todas as operações realizadas.
- Disponibilizar histórico de transações acessível ao usuário.

---

## Público-Alvo

**Usuário final:** Pessoas que desejam realizar operações financeiras básicas de forma segura e digital, sem a burocracia de bancos tradicionais.

---

## Escopo da V1

A primeira versão tem como foco estabelecer a base funcional da plataforma.

### Funcionalidades incluídas

| Funcionalidade | Descrição |
|---|---|
| Cadastro de usuários | Registro de novos usuários na plataforma |
| Autenticação | Login seguro com geração de token |
| Controle de acesso | Permissões por perfil de usuário |
| Criação de conta bancária | Conta criada automaticamente no cadastro |
| Consulta de saldo | Visualização do saldo atual da conta |
| Depósito | Adição de valores à conta |
| Saque | Retirada de valores da conta |
| Transferência | Movimentação entre contas do sistema |
| Histórico de transações | Extrato com todas as operações realizadas |
| Auditoria básica | Registro de operações para rastreabilidade |
| Documentação da API | Endpoints documentados e acessíveis |

### Fora do escopo da V1

As funcionalidades abaixo estão mapeadas para versões futuras e **não** serão implementadas na V1:

- Cache distribuído
- Mensageria assíncrona
- Notificações
- Cartão virtual
- PIX
- Microsserviços
- Observabilidade avançada
- CI/CD e deploy em nuvem
- Mecanismos antifraude
- Autenticação multifator (MFA)

---

## Roadmap de Versões

| Versão | Foco | Principais entregas |
|---|---|---|
| **v1.0.0** | Base funcional | Auth, contas, transações, auditoria |
| **v1.1.0** | Performance | Cache com Redis, otimizações |
| **v1.2.0** | Comunicação | Mensageria com Kafka, notificações |
| **v2.0.0** | Escala | Microsserviços, observabilidade avançada |

---

## Premissas

- Cada usuário terá exatamente uma conta bancária na V1.
- Todas as operações financeiras serão registradas para fins de auditoria.
- O sistema operará com uma única moeda (BRL).
- A autenticação será obrigatória para todas as operações financeiras.

---

## Restrições

- O sistema não processará pagamentos reais; trata-se de uma simulação.
- Não haverá integração com sistemas bancários externos.
- Alta disponibilidade e SLA não são requisitos da V1.