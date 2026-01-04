package br.edu.utfpr.usandofirestore.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import br.edu.utfpr.usandofirestore.entity.Cadastro

class DatabaseHandler private constructor(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "bdfile.sqlite"
        const val TABLE_NAME = "cadastro"

        const val COLUMN_ID = "_id"
        const val COLUMN_NOME = "nome"
        const val COLUMN_TELEFONE = "telefone"

        @Volatile
        private var instance: DatabaseHandler? = null

        fun getInstance(context: Context): DatabaseHandler {
            if (instance == null) {
                instance = DatabaseHandler(context.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(banco: SQLiteDatabase?) {
        banco?.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_NAME (_id INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT, telefone TEXT)")
    }

    override fun onUpgrade(
        banco: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {
        banco?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(banco)
    }

    fun inserir(cadastro: Cadastro) {
        val registro = ContentValues()
        registro.put(COLUMN_NOME, cadastro.nome)
        registro.put(COLUMN_TELEFONE, cadastro.telefone)

        writableDatabase.insert(TABLE_NAME, null, registro)
    }

    fun alterar(cadastro: Cadastro) {
        val registro = ContentValues()
        registro.put(COLUMN_NOME, cadastro.nome)
        registro.put(COLUMN_TELEFONE, cadastro.telefone)

        writableDatabase.update(TABLE_NAME, registro, "$COLUMN_ID = ${cadastro._id}", null)
    }

    fun excluir(id: Int) {
        writableDatabase.delete(TABLE_NAME, "$COLUMN_ID = $id", null)
    }

    fun pesquisar(id: Int): Cadastro? {
        val registro: Cursor = writableDatabase.query(
            TABLE_NAME,
            null,
            "$COLUMN_ID = $id",
            null, null, null, null
        )

        var retorno: Cadastro? = null

        if (registro.moveToNext()) {
            val nome = registro.getString(1)
            val telefone = registro.getString(2)
            retorno = Cadastro(id, nome, telefone)
        }

        return retorno
    }

    fun listar(filtro: String): Cursor {
        val selection = if (filtro.isNotEmpty()) "nome LIKE ?" else null
        val selectionArgs = if (filtro.isNotEmpty()) arrayOf("%$filtro%") else null

        val registros: Cursor = writableDatabase.query(
            TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        return registros
    }
}