package br.edu.utfpr.usandofirestore.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import br.edu.utfpr.usandofirestore.MainActivity
import br.edu.utfpr.usandofirestore.R
import br.edu.utfpr.usandofirestore.database.DatabaseHandler
import br.edu.utfpr.usandofirestore.entity.Cadastro

class MeuAdapter(val context: Context, val registros: List<Cadastro>) : BaseAdapter(){
    override fun getCount(): Int {
        return registros.size
    }

    override fun getItem(pos: Int): Any? {
        val cadastro = Cadastro(
            registros[pos]._id,
            registros[pos].nome,
            registros[pos].telefone
        )
        return cadastro
    }

    override fun getItemId(pos: Int): Long {
        return registros[pos]._id.toInt().toLong()
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

        tvNomeElementoLista.text = registros[pos].nome
        tvTelefoneElementoLista.text = registros[pos].telefone

        btEditarElementoLista.setOnClickListener {
            val intent = android.content.Intent(context, MainActivity::class.java)

            intent.putExtra("cod", registros[pos]._id)
            intent.putExtra(DatabaseHandler.COLUMN_NOME, registros[pos].nome)
            intent.putExtra(DatabaseHandler.COLUMN_TELEFONE, registros[pos].telefone)

            context.startActivity(intent)
        }

        return view
    }

}