package br.edu.utfpr.usandosqlite.adapter

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import br.edu.utfpr.usandosqlite.MainActivity
import br.edu.utfpr.usandosqlite.R
import br.edu.utfpr.usandosqlite.database.DatabaseHandler
import br.edu.utfpr.usandosqlite.entity.Cadastro

class MeuAdapter(val context: Context, val cursor: Cursor) : BaseAdapter(){
    override fun getCount(): Int {
        return cursor.count
    }

    override fun getItem(pos: Int): Any? {
        cursor.moveToPosition(pos)

        val cadastro = Cadastro(
            cursor.getInt(cursor.getColumnIndexOrThrow("_id")),
            cursor.getString(cursor.getColumnIndexOrThrow("nome")),
            cursor.getString(cursor.getColumnIndexOrThrow("telefone"))
        )
        return cadastro
    }

    override fun getItemId(pos: Int): Long {
        cursor.moveToPosition(pos)
        return cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHandler.COLUMN_ID)).toLong()
    }

    override fun getView(
        pos: Int,
        p1: View?,
        p2: ViewGroup?
    ): View? {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.elemento_lista, null)

        val tvNomeElementoLista = view.findViewById<TextView>(R.id.tvNomeElementoLista)
        val tvTelefoneElementoLista = view.findViewById<TextView>(R.id.tvTelefoneElementoLista)
        val btEditarElementoLista = view.findViewById<ImageButton>(R.id.btEditarElementoLista)

        cursor.moveToPosition(pos)

        tvNomeElementoLista.text = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.COLUMN_NOME))
        tvTelefoneElementoLista.text = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.COLUMN_TELEFONE))

        btEditarElementoLista.setOnClickListener {
            val intent = android.content.Intent(context, MainActivity::class.java)
            cursor.moveToPosition(pos)

            intent.putExtra("cod", cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHandler.COLUMN_ID)))
            intent.putExtra(DatabaseHandler.COLUMN_NOME, cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.COLUMN_NOME)))
            intent.putExtra(DatabaseHandler.COLUMN_TELEFONE, cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.COLUMN_TELEFONE)))

            context.startActivity(intent)
        }


        return view
    }

}