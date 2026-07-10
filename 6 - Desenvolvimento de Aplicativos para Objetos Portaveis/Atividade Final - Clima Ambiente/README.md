# Atividade Final - Clima Ambiente

Aplicativo Wear OS desenvolvido como atividade final da disciplina de Desenvolvimento de Aplicativos para Objetos Portaveis, utilizando sensores de temperatura ambiente e umidade relativa para monitoramento do conforto ambiental em tempo real.

---

## 📝 Descrição da Atividade

O objetivo deste trabalho é demonstrar os conhecimentos adquiridos sobre **Wear OS**, sensores, arquitetura de aplicações e desenvolvimento para dispositivos portáteis.

A aplicação deverá atender aos seguintes requisitos:

* **Utilizar Kotlin**;
* **Utilizar Jetpack Compose**;
* **Utilizar o padrão MVVM**;
* **Ler informações de pelo menos um sensor** disponível no dispositivo ou emulador;
* **Exibir os dados coletados em tempo real** na interface;
* **Possuir alguma interação ou tomada de decisão** baseada nas informações obtidas pelos sensores;
* **Implementar corretamente as permissões necessárias** para acesso aos sensores, quando aplicável.

---

## ✅ Funcionalidades Implementadas

* **Monitoramento de Sensores em Tempo Real:** Coleta contínua de dados do ambiente, através dos sensores de temperatura ambiente (`TYPE_AMBIENT_TEMPERATURE`) e de umidade relativa (`TYPE_RELATIVE_HUMIDITY`).
* **Classificação de Conforto Ambiental:** Algoritmo que cruza os dados de temperatura e umidade para determinar o status do ambiente (ex: Confortável, Quente e Seco, etc.).
* **Interface Dinâmica e Reativa:** A interface muda de cor e ícones automaticamente conforme o status de conforto detectado.
* **Gestão de Ciclo de Vida:** O monitoramento é pausado automaticamente quando o aplicativo vai para o segundo plano para economizar bateria.

---

## 🛠 Tecnologias Utilizadas

* **Kotlin:** Linguagem principal do projeto.
* **Wear OS:** Sistema operacional alvo.
* **Jetpack Compose (Material 3):** Toolkit moderno para construção da interface de usuário.
* **MVVM (Model-View-ViewModel):** Padrão de arquitetura para separação de responsabilidades.
* **StateFlow:** Para gerenciamento de estado reativo e imutável.
* **SensorManager:** API do Android para acesso direto ao hardware de sensores.
* **Coroutines:** Para operações assíncronas e fluxo de dados.
* **Lifecycle Compose:** Integração segura entre o ciclo de vida do Android e o Compose.

---

## 🏗 Arquitetura

O projeto segue rigorosamente o padrão **MVVM**, garantindo que a lógica de negócio esteja isolada da interface.

**Responsabilidades:**
* **Model:** Define as estruturas dos dados (`AmbienteStatus` e `AmbienteUiState`).
* **Sensor Layer:** O `AmbienteSensorManager` encapsula a complexidade do `SensorEventListener`.
* **ViewModel:** Centraliza a lógica de classificação e mantém o estado único da tela.
* **UI:** O `ConfortoAmbientalScreen` apenas desenha os componentes com base no estado recebido.

---

## ⚙ Funcionamento

1. **Inicialização:** Ao abrir o aplicativo, o `ConfortoAmbientalScreen` utiliza um `LaunchedEffect` para solicitar ao `ViewModel` o início do monitoramento.
2. **Escuta de Dados:** O `AmbienteSensorManager` registra os listeners no sistema Android.
3. **Processamento:** Cada mudança nos sensores dispara um callback para o `ViewModel`.
4. **Cálculo de Status:** O `ViewModel` recebe os valores brutos, aplica as regras de conforto e gera um novo `AmbienteUiState`.
5. **Atualização da UI:** O Compose detecta a mudança no `StateFlow` e redesenha a tela com as novas cores, textos e ícones.

---

## 📋 Regras de Negócio

O aplicativo classifica o ambiente em seis estados possíveis:

| Status | Condição | Recomendação |
| :--- | :--- | :--- |
| **CONFORTÁVEL** | Temp: 20°C a 26°C **e** Umidade: 40% a 60% | O ambiente está agradável. |
| **QUENTE_SECO** | Temp > 30°C **e** Umidade < 35% | Beba água e mantenha-se hidratado. |
| **QUENTE_ABAFADO** | Temp > 30°C **e** Umidade > 70% | Evite exposição prolongada ao calor. |
| **FRIO_SECO** | Temp < 18°C **e** Umidade < 35% | O ar pode causar ressecamento. |
| **FRIO_UMIDO** | Temp < 18°C **e** Umidade > 70% | Ambiente com sensação elevada de umidade. |
| **ATENÇÃO** | Qualquer outra combinação intermediária | Condições intermediárias. |

---

## 📸 Imagens do Projeto

<img width="250" alt="Screenshot_20260709_211752" src="https://github.com/user-attachments/assets/17303281-364b-43e9-9f2f-08d73383b7b9" />
<img width="250" alt="Screenshot_20260709_211801" src="https://github.com/user-attachments/assets/e9f6f9bf-6146-4ae8-b5f0-5e290e661516" />
<div/>
<img width="250" alt="Screenshot_20260709_211901" src="https://github.com/user-attachments/assets/e2569cd1-0db9-4bc5-9a61-f0659d8fa640" />
<img width="250" alt="Screenshot_20260709_211903" src="https://github.com/user-attachments/assets/c397d22e-9fe7-40e6-82ee-e70a4888a7f4" />
<div/>
<img width="250" alt="Screenshot_20260709_211925" src="https://github.com/user-attachments/assets/64686a5c-bd8c-43a1-8188-5c5bc6164ac4" />
<img width="250" alt="Screenshot_20260709_211928" src="https://github.com/user-attachments/assets/6ff26a40-96b1-4aaf-925e-dff62a4726c1" />
<div/>
<img width="250" alt="Screenshot_20260709_211951" src="https://github.com/user-attachments/assets/9989e58c-dfc8-409a-98fa-68487f8f9d0d" />
<img width="250" alt="Screenshot_20260709_211955" src="https://github.com/user-attachments/assets/d24dc723-b194-4a85-8f1f-3167abd96e5d" />
<div/>
<img width="250" alt="Screenshot_20260709_212012" src="https://github.com/user-attachments/assets/f103b0ea-e47c-40e0-91f6-238f984e0c47" />
<img width="250" alt="Screenshot_20260709_212015" src="https://github.com/user-attachments/assets/89449bb3-c66c-4b29-9167-9ae8b3cda82f" />
<div/>
<img width="250" alt="Screenshot_20260709_212058" src="https://github.com/user-attachments/assets/2c0d2219-079a-42e2-abab-78e8669030e2" />
<img width="250" alt="Screenshot_20260709_212101" src="https://github.com/user-attachments/assets/72a86b9a-1f36-45de-a21c-92fe3bb02db8" />

