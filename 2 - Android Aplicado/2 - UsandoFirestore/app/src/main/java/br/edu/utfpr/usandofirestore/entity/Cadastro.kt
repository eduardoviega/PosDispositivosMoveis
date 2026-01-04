package br.edu.utfpr.usandofirestore.entity

import com.google.firebase.firestore.DocumentId

data class Cadastro(
    @DocumentId
    val _id: String = "",
    val nome: String = "",
    val telefone: String = ""
)
