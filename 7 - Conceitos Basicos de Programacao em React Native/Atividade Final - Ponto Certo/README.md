# Ponto Certo

Calculadora de preço de venda para peças de crochê. A artesã cadastra a peça
(nome, foto, horas de trabalho) e os materiais consumidos; o app devolve o preço
com o custo detalhado, a margem real e o retorno por hora — e escreve os textos
de venda.

Atividade final da disciplina de React Native da pós-graduação em Programação
para Dispositivos Móveis.

## Como rodar

Requer um dev build (o login por Google não roda no Expo Go) e o Firebase
configurado — veja "Configuração" abaixo.

```bash
npm install
npm start        # conecta no dev build já instalado
npm test         # testes do núcleo de cálculo
npx tsc --noEmit # verificação de tipos
npx eslint .     # lint
```

## Configuração

Copie `.env.example` para `.env` e preencha as duas chaves; coloque o
`google-services.json` do Firebase na raiz do projeto. Nenhum dos dois vai
para o Git.

```bash
npx eas-cli login
npx eas-cli build:configure
npx eas-cli build --platform android --profile development
```

## Funcionalidades

**Cálculo de preço.** `src/service/pricing` é módulo puro, sem React e sem
rede. A cascata é: custo unitário do material → custo de material → mão de
obra → indiretos → custo total → lucro → repasse de taxa → arredondamento
comercial. 26 testes, incluindo o exemplo fechado do escopo (coelho amigurumi
de 6 h → R$ 325,00 de preço sugerido e R$ 44,45 de retorno por hora).

O detalhe que o módulo existe para não errar: a taxa da maquininha incide sobre
o preço de venda, então entra por **divisão** (`preço_base / (1 − taxa)`), nunca
somando o percentual no fim.

**Catálogo e projetos.** Sete telas, catálogo de materiais com custo unitário
derivado, tela de preço com detalhamento em cascata e barra de composição.
Projetos e materiais têm busca (por nome, categoria e, no material, cor) sem
diferenciar acento ou maiúscula. Dentro de uma peça, Detalhes, Preço e
Anúncio são sub-abas — Preço e Anúncio ficam sem link na barra até a peça
ser salva pela primeira vez.

**Conta e sincronização.** Login só com conta Google (`@react-native-google-signin`
+ Firebase Auth), dados em Firestore sob `usuarios/{uid}`. O documento do
usuário nasce no primeiro login com os dados do Google e os percentuais
padrão; login seguinte só atualiza o perfil do Google, nunca os percentuais já
ajustados. A foto do projeto vai em base64: miniatura no próprio documento,
foto cheia num documento à parte (`projetos/{id}/midia/foto`), buscada só
quando a tela de detalhe abre.

Exclusão de conta em Ajustes: reautentica com o Google, apaga materiais,
projetos e o perfil no Firestore — nessa ordem, enquanto a sessão ainda é
válida para as regras de segurança aceitarem a limpeza — e só então apaga o
usuário no Firebase Auth.

**IA.** `service/ai/cliente.ts` chama o Gemini pelo SDK `openai`. As três funções (defesa do preço, anúncio, ideias)
validam a resposta e caem no fallback local se a chave faltar, a rede cair ou o
JSON vier malformado. Nenhuma delas produz número — recebem valores já
calculados e devolvem só texto.

**Identidade visual.** Ícone e splash próprios em `assets/images/`: um gancho
de crochê com um laço de fio, nas cores da paleta do app. Ícone adaptativo do
Android com camada de primeiro plano transparente, fundo sólido e versão
monocromática para o modo temático do Android 13+.

## Estrutura

```
src/
├── app/                     rotas (expo-router)
│   ├── login.tsx            redireciona para "/" se já logado
│   └── (protected)/         redireciona para "/login" se não logado
│       ├── _layout.tsx      só aqui existe o DadosProvider
│       ├── (tabs)/          projetos · materiais · ideias · ajustes
│       ├── material/[id]    cadastro de material ("novo" cria)
│       └── projeto/[id]/    sub-abas da peça (_layout.tsx próprio)
│           ├── index        Detalhes
│           ├── preco        Preço
│           └── anuncio      Anúncio — some da barra enquanto id="novo"
├── domain/                  tipos, unidades de medida, regras entre entidades
├── lib/                     dinheiro em centavos, entrada pt-BR, foto base64
├── service/
│   ├── pricing/             núcleo de cálculo + testes
│   ├── storage/             contrato de persistência + implementação Firestore
│   ├── auth/                login Google + sessão Firebase
│   └── ai/                  defesa do preço · anúncio · ideias
├── state/
│   ├── auth.tsx             sessão (leve, sem dados do Firestore)
│   └── dados.tsx            perfil, materiais e projetos — só dentro de (protected)
├── components/              kit de interface e barra de composição
└── theme/                   paleta, espaçamento, tipografia
```


## Capturas de tela

**Login** — a única porta de entrada é a conta Google; sem cadastro, sem senha.

<img src="docs/screenshots/Login%201.jpg" alt="Login" width="200">

<br/>

**Projetos** — lista de peças com foto, preço e status.

<img src="docs/screenshots/Projetos%201.jpg" alt="Lista de projetos" width="200">

<br/>

**Materiais** — catálogo com o custo unitário derivado, nunca digitado.

<img src="docs/screenshots/Materiais%201.jpg" alt="Catálogo de materiais" width="200">
<img src="docs/screenshots/Materiais%202.jpg" alt="Cadastro de material" width="200">

<br/>

**Detalhes** — nome, horas, categoria, dificuldade e os materiais lançados na
peça, com o custo de material somado em tempo real.

<img src="docs/screenshots/Projetos%20Detalhes%201.jpg" alt="Detalhes da peça" width="200">
<img src="docs/screenshots/Projetos%20Detalhes%202.jpg" alt="Materiais usados na peça" width="200">

<br/>

**Preço** — o coração do app: detalhamento em cascata, a barra de composição
mostrando onde vai cada parte do preço e, no fim, a IA escrevendo a resposta
para o cliente que achou caro — nunca sugerindo um preço diferente.

<img src="docs/screenshots/Projetos%20Pre%C3%A7o%201.jpg" alt="Preço sugerido e detalhamento" width="200">
<img src="docs/screenshots/Projetos%20Pre%C3%A7o%202.jpg" alt="Barra de composição e preço final" width="200">
<img src="docs/screenshots/Projetos%20Pre%C3%A7o%203.jpg" alt="Defesa do preço gerada pela IA" width="200">

<br/>

**Anúncio** — título, descrição, bullets e hashtags para venda, gerados a
partir dos dados do projeto, no tom e canal escolhidos.

<img src="docs/screenshots/Projetos%20An%C3%BAncio%201.jpg" alt="Escolha de tom e canal do anúncio" width="200">
<img src="docs/screenshots/Projetos%20An%C3%BAncio%202.jpg" alt="Anúncio de venda gerado pela IA" width="200">

<br/>

**Ideias** — sugestões de peça a partir de um material e uma quantidade, com
tempo e rendimento estimados.

<img src="docs/screenshots/Ideias%201.jpg" alt="Formulário de sobra de material" width="200">
<img src="docs/screenshots/Ideias%202.jpg" alt="Ideias sugeridas pela IA" width="200">

<br/>

**Ajustes** — perfil de precificação com prévia ao vivo, conta Google
conectada e exclusão de conta.

<img src="docs/screenshots/Ajustes%201.jpg" alt="Perfil de precificação" width="200">
<img src="docs/screenshots/Ajustes%202.jpg" alt="Conta Google e zona de risco" width="200">
