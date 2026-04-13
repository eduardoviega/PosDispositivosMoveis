# Atividade Final - Contas

Aplicativo Android desenvolvido como atividade final da disciplina de **Interfaces Gráficas em Aplicações para Dispositivos Móveis** — projeto para controle de receitas e despesas com foco em usabilidade, organização por camadas e fluxo completo de cadastro/listagem.

---

## 📝 Descrição da Atividade

O objetivo do projeto é permitir o gerenciamento de lançamentos financeiros em um app nativo Android, com operações de criação, edição, remoção e visualização dos registros, além de totalizadores para acompanhamento do saldo.

Com base na especificação da disciplina, o foco principal da entrega está nas melhorias de interface e experiência do usuário em duas áreas: **listagem de lançamentos** e **formulário de lançamento**.

O aplicativo foi estruturado com separação de responsabilidades entre UI, ViewModel e camada de dados, utilizando estado reativo para atualização das telas.

---

## ✅ Critérios da Especificação Atendidos

### 1. Listagem de Lançamentos

| Critério | Peso | Implementação |
| --- | --- | --- |
| Ícone por item | 1,5 | Ícone à esquerda indicando status do lançamento com `Icons.Filled.ThumbUp` (pago) e `Icons.Filled.ThumbDownOffAlt` (não pago), com cor por tipo: despesa `#CF5355` e receita `#00984E`. |
| Layout do item | 0,5 | Estrutura em duas linhas: linha 1 com descrição; linha 2 com data alinhada à esquerda e valor alinhado à direita. |
| Formato do valor | 1,0 | Valores formatados com a função `formatar()`, incluindo sinal negativo para despesas e cor conforme tipo. |
| BottomBar | 1,5 | Saldo e previsão exibidos com as mesmas regras visuais de formatação e sinal dos valores. |

### 2. Formulário de Lançamento

| Critério | Peso | Implementação |
| --- | --- | --- |
| Ícones nos campos | 1,0 | Ícones à esquerda nos campos: `Icons.AutoMirrored.Filled.Notes` (descrição) e `Icons.Filled.AttachMoney` (valor). |
| DatePicker | 1,5 | Campo de data implementado com DatePicker em vez de entrada textual simples. |
| Confirmação de exclusão | 1,5 | Janela de confirmação exibida antes da remoção de um lançamento. |
| Validações | 1,5 | Validação de formulário com descrição obrigatória, valor obrigatório e tratamento de entrada inválida no campo valor. |

**Cobertura documentada dos critérios da atividade: 10,5/10,5 pontos.**

---

## 📱 Funcionalidades Implementadas

- [x] Cadastro de lançamento com **descrição**, **valor**, **data**, **tipo** (Receita/Despesa) e **status de pagamento**
- [x] Edição de lançamento existente
- [x] Exclusão de lançamento com diálogo de confirmação
- [x] Validações de formulário (descrição obrigatória, valor obrigatório e valor válido)
- [x] Listagem de lançamentos ordenada por data
- [x] Indicador visual de status em cada item da lista (ícone à esquerda)
- [x] Diferenciação visual entre receitas e despesas por cor e sinal do valor
- [x] Cálculo de **Saldo** (considera apenas lançamentos pagos)
- [x] Cálculo de **Previsão** (considera todos os lançamentos)
- [x] Navegação entre tela de listagem e tela de formulário

---

## 🛠️ Tecnologias e Dependências

- **Linguagem:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Navegação:** Navigation Compose
- **Arquitetura:** ViewModel + State Holder + Observer Pattern
- **Armazenamento atual:** Lista em memória (sem banco persistente)
- **SDK Android:** minSdk 26 / targetSdk 36 / compileSdk 36
- **JVM:** Java 11

---

## 🏗️ Organização do Projeto

- **UI Layer**: telas e componentes Compose (lista e formulário)
- **ViewModel Layer**: regras de estado, validação e fluxo de ações
- **Data Layer**: entidade de lançamento, enum de tipo e datasource singleton
- **Utils**: formatação de valores/datas e regras de cálculo de saldo/previsão

---

## 🧭 Especificações das Telas

### Tela de Listagem (Extrato)
- Exibe todos os lançamentos cadastrados
- Permite atualização manual da lista
- Mostra status pago/pendente por ícone à esquerda (ThumbUp/ThumbDownOffAlt)
- Item estruturado em duas linhas: descrição na primeira linha; data à esquerda e valor à direita na segunda
- Exibe valores formatados, com sinal negativo para despesas e cor por tipo
- Exibe totalizadores de **Saldo** e **Previsão** no rodapé com as mesmas regras visuais
- Botão flutuante para adicionar novo lançamento

### Tela de Formulário
- Campos para descrição, valor, data, tipo e status de pagamento
- Ícones à esquerda nos campos de descrição e valor
- DatePicker para seleção de data
- Ações de salvar e, em modo de edição, excluir lançamento
- Exibição de feedback para validação e erros de preenchimento

---

## 📸 Imagens do Projeto

<div>
	<img width="200" alt="01_inicio" src="https://github.com/user-attachments/assets/e8707938-9126-4f16-a848-50c023372989" />
	<img width="200" alt="02_lancamento" src="https://github.com/user-attachments/assets/e7d323d3-def0-43d0-a873-7ea0e8c40fe9" />
	<img width="200" alt="03_listagem_preenchida" src="https://github.com/user-attachments/assets/bd42e534-f76e-46bb-937c-103c51c48648" />
	<img width="200" alt="04_edicao_registro_excluindo" src="https://github.com/user-attachments/assets/abe2d422-8889-486d-b2f3-d2b4af34999c" />
</div>

---
