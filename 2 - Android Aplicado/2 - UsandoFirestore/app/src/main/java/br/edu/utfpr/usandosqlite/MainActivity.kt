package br.edu.utfpr.usandosqlite

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
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

        initView()
    }

    private fun initView() {
        val isEditing = intent.getIntExtra("cod", 0) != 0

        if (isEditing) {
            binding.etCod.setText(intent.getIntExtra("cod", 0).toString())
            binding.etNome.setText(intent.getStringExtra("nome"))
            binding.etTelefone.setText(intent.getStringExtra("telefone"))
        } else {
            binding.tvCod.visibility = View.GONE
            binding.etCod.visibility = View.GONE
            binding.btExcluir.visibility = View.GONE
            binding.btPesquisar.visibility = View.GONE
        }
    }

    fun btSalvarOnClick(view: View) {
        lateinit var msg: String

        if (binding.etCod.text.toString().isEmpty()) {
            val cadastro = Cadastro(
                0,
                binding.etNome.text.toString(),
                binding.etTelefone.text.toString()
            )
            banco.inserir(cadastro)
            msg = "Inclusão efetuada com sucesso!"
        } else {
            val cadastro = Cadastro(
                binding.etCod.text.toString().toInt(),
                binding.etNome.text.toString(),
                binding.etTelefone.text.toString()
            )
            banco.alterar(cadastro)
            msg = "Alteração efetuada com sucesso!"
        }

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        finish()
    }

    fun btExcluirOnClick(view: View) {
        banco.excluir(binding.etCod.text.toString().toInt())

        Toast.makeText(
            this, "Exclusão efetuada com sucesso!", Toast.LENGTH_SHORT
        ).show()
        finish()
    }

    fun btPesquisarOnClick(view: View) {
        val etCodPesquisar = EditText(this)
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Digite o Código")
        builder.setView(etCodPesquisar)
        builder.setCancelable(false)
        builder.setNegativeButton("Fechar", null)

        builder.setPositiveButton("Pesquisar") { dialog, _ ->
            if (etCodPesquisar.text != null) {
                val cadastro = banco.pesquisar(etCodPesquisar.text.toString().toInt())

                if (cadastro != null) {
                    binding.etCod.setText(cadastro._id.toString())
                    binding.etNome.setText(cadastro.nome)
                    binding.etTelefone.setText(cadastro.telefone)
                } else {
                    binding.etCod.setText("")
                    binding.etNome.setText("")
                    binding.etTelefone.setText("")

                    Toast.makeText(
                        this, "Registro não encontrado!", Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this, "Código inválido!", Toast.LENGTH_SHORT
                ).show()
            }
            dialog.dismiss()
        }

        builder.show()
    }
}