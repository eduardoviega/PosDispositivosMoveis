package br.edu.utfpr.financeflow.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.getDoubleOrNull
import br.edu.utfpr.financeflow.entity.LancamentoEntity
import br.edu.utfpr.financeflow.enum.TipoLancamentoEnum
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class DatabaseHandler private constructor(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "bdfile.sqlite"
        const val TABLE_NAME = "lancamento"

        const val COLUMN_ID = "_id"
        const val COLUMN_TIPO_SELECIONADO = "tipoSelecionado"
        const val COLUMN_DESCRICAO = "descricao"
        const val COLUMN_VALOR = "valor"
        const val COLUMN_DATA = "data"

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
        banco?.execSQL(
            "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "tipoSelecionado TEXT," +
                    "descricao TEXT," +
                    "valor NUMBER," +
                    "data TEXT" +
                    ")"
        )
    }

    override fun onUpgrade(
        banco: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {
        banco?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(banco)
    }

    fun inserir(lancamento: LancamentoEntity) {
        val registro = ContentValues()
        registro.put(COLUMN_TIPO_SELECIONADO, lancamento.tipoSelecionado.toString())
        registro.put(COLUMN_DESCRICAO, lancamento.descricao)
        registro.put(COLUMN_VALOR, lancamento.valor)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        registro.put(COLUMN_DATA, lancamento.data.format(formatter))

        writableDatabase.insert(TABLE_NAME, null, registro)
    }

    fun listar(tipoLancamentoEnum: TipoLancamentoEnum?): List<LancamentoEntity> {
        var selection = if(tipoLancamentoEnum != null) "$COLUMN_TIPO_SELECIONADO = '$tipoLancamentoEnum'" else null

        val registros: Cursor = writableDatabase.query(
            TABLE_NAME,
            null,
            selection,
            null,
            null,
            null,
            null
        )

        val registrosList = mutableListOf<LancamentoEntity>()

        while (registros.moveToNext()) {
            val id = registros.getInt(0)
            val tipoSelecionado = registros.getString(1)
            val descricao = registros.getString(2)
            val valor = registros.getDoubleOrNull(3)
            val data = registros.getString(4)

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val lancamento = LancamentoEntity(
                id,
                TipoLancamentoEnum.valueOf(tipoSelecionado),
                descricao,
                valor,
                LocalDateTime.parse(data, formatter),
            )
            registrosList.add(lancamento)
        }

        return registrosList
    }
}