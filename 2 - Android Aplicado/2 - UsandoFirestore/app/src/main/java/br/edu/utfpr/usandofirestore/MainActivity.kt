package br.edu.utfpr.usandofirestore

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.utfpr.usandofirestore.database.DatabaseHandler
import br.edu.utfpr.usandofirestore.databinding.ActivityMainBinding
import br.edu.utfpr.usandofirestore.entity.Cadastro
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var banco: DatabaseHandler
    val db = Firebase.firestore

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
        val cadastro = Cadastro(
            if (binding.etCod.text.toString().isEmpty()) 0 else binding.etCod.text.toString()
                .toInt(),
            binding.etNome.text.toString(),
            binding.etTelefone.text.toString()
        )

        db.collection("cadastro")
            .document(binding.etCod.text.toString())
            .set(cadastro)
            .addOnSuccessListener {
                val msg = if (binding.etCod.text.toString().isEmpty()) {
                    "Inclusão efetuada com sucesso!"
                } else {
                    "Alteração efetuada com sucesso!"
                }
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao salvar: ${e.message}", Toast.LENGTH_SHORT).show()
            }


//        lateinit var msg: String
//
//        if (binding.etCod.text.toString().isEmpty()) {
//            val cadastro = Cadastro(
//                0,
//                binding.etNome.text.toString(),
//                binding.etTelefone.text.toString()
//            )
//            banco.inserir(cadastro)
//            msg = "Inclusão efetuada com sucesso!"
//        } else {
//            val cadastro = Cadastro(
//                binding.etCod.text.toString().toInt(),
//                binding.etNome.text.toString(),
//                binding.etTelefone.text.toString()
//            )
//            banco.alterar(cadastro)
//            msg = "Alteração efetuada com sucesso!"
//        }
//
//        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
//        finish()
    }

    fun btExcluirOnClick(view: View) {
        banco.excluir(binding.etCod.text.toString().toInt())

        Toast.makeText(
            this, "Exclusão efetuada com sucesso!", Toast.LENGTH_SHORT
        ).show()
        finish()
    }

    fun btPesquisarOnClick(view: View) {
        val msg = StringBuilder()

        db.collection("cadastro")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val registro = document.getString("nome")
                    msg.append("$registro\n")
                }
                Toast.makeText(
                    this, msg.toString(), Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this, "Erro ao buscar registros: ${e.message}", Toast.LENGTH_SHORT
                ).show()
            }

//        val etCodPesquisar = EditText(this)
//        val builder = AlertDialog.Builder(this)
//
//        builder.setTitle("Digite o Código")
//        builder.setView(etCodPesquisar)
//        builder.setCancelable(false)
//        builder.setNegativeButton("Fechar", null)
//
//        builder.setPositiveButton("Pesquisar") { dialog, _ ->
//            if (etCodPesquisar.text != null) {
//                val cadastro = banco.pesquisar(etCodPesquisar.text.toString().toInt())
//
//                if (cadastro != null) {
//                    binding.etCod.setText(cadastro._id.toString())
//                    binding.etNome.setText(cadastro.nome)
//                    binding.etTelefone.setText(cadastro.telefone)
//                } else {
//                    binding.etCod.setText("")
//                    binding.etNome.setText("")
//                    binding.etTelefone.setText("")
//
//                    Toast.makeText(
//                        this, "Registro não encontrado!", Toast.LENGTH_SHORT
//                    ).show()
//                }
//            } else {
//                Toast.makeText(
//                    this, "Código inválido!", Toast.LENGTH_SHORT
//                ).show()
//            }
//            dialog.dismiss()
//        }
//
//        builder.show()
    }
}