package br.edu.utfpr.usandosqlite

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.utfpr.usandosqlite.database.DatabaseHandler
import br.edu.utfpr.usandosqlite.databinding.ActivityMainBinding
import br.edu.utfpr.usandosqlite.entity.Cadastro

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var banco: DatabaseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        banco = DatabaseHandler.getInstance(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun btIncluirOnClick(view: View) {
        val cadastro = Cadastro(
            0,
            binding.etNome.text.toString(),
            binding.etTelefone.text.toString()
        )

        banco.inserir(cadastro)

        Toast.makeText(
            this, "Inclusão efetuada com sucesso!", Toast.LENGTH_SHORT
        ).show()
    }

    fun btAlterarOnClick(view: View) {
        val cadastro = Cadastro(
            binding.etCod.text.toString().toInt(),
            binding.etNome.text.toString(),
            binding.etTelefone.text.toString()
        )

        banco.alterar(cadastro)

        Toast.makeText(
            this, "Alteração efetuada com sucesso!", Toast.LENGTH_SHORT
        ).show()
    }

    fun btExcluirOnClick(view: View) {
        banco.excluir(binding.etCod.text.toString().toInt())

        Toast.makeText(
            this, "Exclusão efetuada com sucesso!", Toast.LENGTH_SHORT
        ).show()
    }

    fun btPesquisarOnClick(view: View) {
        val cadastro = banco.pesquisar(binding.etCod.text.toString().toInt())

        if (cadastro != null) {
            binding.etNome.setText(cadastro.nome)
            binding.etTelefone.setText(cadastro.telefone)
        } else {
            binding.etNome.setText("")
            binding.etTelefone.setText("")

            Toast.makeText(
                this, "Registro não encontrado!", Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun btListarOnClick(view: View) {
        val intent = Intent(this, ListarActivity::class.java)
        startActivity(intent)
    }
}