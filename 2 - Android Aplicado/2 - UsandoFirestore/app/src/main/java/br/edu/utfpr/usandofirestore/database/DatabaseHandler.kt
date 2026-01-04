package br.edu.utfpr.usandofirestore.database

import br.edu.utfpr.usandofirestore.entity.Cadastro
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.tasks.await

class DatabaseHandler() {

    private val firestore = Firebase.firestore

    companion object {
        private const val COLLECTION_NAME = "cadastro"
        const val COLUMN_NOME = "nome"
        const val COLUMN_TELEFONE = "telefone"

        @Volatile
        private var INSTANCE: DatabaseHandler? = null

        fun getInstance(): DatabaseHandler {
            return INSTANCE ?: synchronized(this) {
                val instance = DatabaseHandler()
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun inserir(cadastro: Cadastro) {
        firestore
            .collection(COLLECTION_NAME)
            .add(cadastro)
            .await()
    }

    suspend fun alterar(cadastro: Cadastro) {
        firestore.collection(COLLECTION_NAME)
            .document(cadastro._id)
            .set(cadastro).await()
    }

    suspend fun excluir(id: String) {
        firestore.collection(COLLECTION_NAME)
            .document(id)
            .delete().await()
    }

    suspend fun pesquisar(id: String): Cadastro? {
        val document = firestore.collection(COLLECTION_NAME)
            .document(id)
            .get().await()

        return if (document.exists()) {
            document.toObject(Cadastro::class.java)
        } else {
            null
        }
    }

    suspend fun listar(filtro: String): List<Cadastro> {
        val query = firestore.collection(COLLECTION_NAME)
        val snapshot = query.get().await()
        val cadastros = snapshot.toObjects<Cadastro>()

        return if (filtro.isNotEmpty()) {
            cadastros.filter { it.nome.contains(filtro, ignoreCase = true) }
        } else {
            cadastros
        }
    }
}