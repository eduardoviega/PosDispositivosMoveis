package br.edu.utfpr.usandofirestore

import android.os.Bundle
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.utfpr.usandofirestore.adapter.MeuAdapter
import br.edu.utfpr.usandofirestore.database.DatabaseHandler
import br.edu.utfpr.usandofirestore.databinding.ActivityListarBinding

class ListarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListarBinding
    private lateinit var banco: DatabaseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityListarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        banco = DatabaseHandler.getInstance(this)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.fabIncluir.setOnClickListener {
            fabIncluirOnClick()
        }

        binding.svFiltro.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Não precisamos de ação no submit, o filtro é em tempo real
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                // Chama a inicialização da lista com o novo texto do filtro
                initList(newText ?: "")
                return true
            }
        })
    }

    override fun onStart() {
        super.onStart()
        initList()
    }

    private fun initList(filtro: String = "") {
        val registros = banco.listar(filtro)

        val adapter = MeuAdapter(this, registros)

        binding.lvRegistros.adapter = adapter
    }

    fun fabIncluirOnClick() {
        val intent = android.content.Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}