# Atividade Final - FuelCalc

Aplicativo Android nativo desenvolvido como atividade final da disciplina de **Android Básico** - projeto para comparar o custo por quilômetro entre dois combustíveis a partir do preço por litro e da autonomia (km/l).

## 📝 Descrição da Atividade

Desenvolver um aplicativo móvel que permita ao usuário comparar dois combustíveis informando o preço por litro e a autonomia (km/l) de cada um. O aplicativo deve apresentar a opção de buscar/selecionar um combustível em uma lista (segunda tela) e, ao retornar, preencher os campos correspondentes na tela principal para cálculo do custo por quilômetro.

---

## 📱 Funcionalidades Implementadas

- [x] Tela principal com campos para preço por litro e autonomia (km/l) para dois combustíveis
- [x] Botão "Buscar" que abre a lista de combustíveis (segunda tela)
- [x] Tela de seleção com lista de combustíveis (ex.: Etanol, Gasolina) e retorno da seleção
- [x] Cálculo do custo por km usando a fórmula `preco_por_km = preco_litro / autonomia_km_l` e indicação do combustível mais econômico

---

## 🛠️ Tecnologias e Dependências

- **Linguagem:** Kotlin
- **UI:** Android Views (layouts XML)
- **Bibliotecas:** AndroidX AppCompat, Material Components

---

## 🧭 Especificações das Telas

### Tela Principal
- Campos para informar Preço por litro e Autonomia (km/l) para dois combustíveis
- Botões: Buscar (abre a lista de combustíveis) e Calcular (exibe o resultado)

### Tela de Seleção de Combustível
- Lista simples de combustíveis
- Ao selecionar um item, a tela fecha e retorna a seleção para a tela principal

---

## 📸 Imagens do Projeto

<div>
  <img src="https://github.com/user-attachments/assets/99a44c69-4a5a-4f52-802d-b5a8e268dcbf" width="200px" alt="Tela principal" />
  <img src="https://github.com/user-attachments/assets/b270bd7e-d619-40ba-836e-f482a02c3873" width="200px" alt="Lista de combustíveis" />
  <img src="https://github.com/user-attachments/assets/45fc9940-a026-4864-a6bf-020c032b9bbf" width="200px" alt="Resultado do cálculo" />
</div>

---