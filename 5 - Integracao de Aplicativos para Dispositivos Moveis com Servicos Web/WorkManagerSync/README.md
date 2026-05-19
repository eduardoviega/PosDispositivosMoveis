# WorkManagerSync POC

POC Android em Kotlin para demonstrar sincronização offline-first com:

- WorkManager
- Room
- Firebase Firestore
- Coroutines

## O que o app faz

- Cria anotações localmente no Room;
- Marca novas notas como `isSynced = false`;
- Agenda automaticamente um `OneTimeWorkRequest` com rede conectada;
- Envia as notas pendentes para a coleção `notes` no Firestore;
- Atualiza o banco local para `isSynced = true`;
- Faz retry automático com `Result.retry()`.

## Vídeo de demonstração do app

https://github.com/user-attachments/assets/15e5f2d5-af80-4598-bc62-62240399041a

## Configuração do Firebase

1. Crie um projeto no Firebase Console.
2. Registre o app Android usando o package name `br.edu.utfpr.workmanagersync`.
3. Adicione o arquivo `app/google-services.json` do seu projeto Firebase.
4. Habilite **Firestore Database** com permissões para leitura e escrita.

## Como executar

Abra o projeto no Android Studio e rode o módulo `app`.

## Testes manuais sugeridos

- Criar nota sem internet e validar status pendente.
- Fechar o app e reconectar a internet para confirmar a sincronização.
- Reiniciar o dispositivo com notas pendentes e verificar se o WorkManager retoma a tarefa.
- Simular falha no Firestore para observar o retry automático.
