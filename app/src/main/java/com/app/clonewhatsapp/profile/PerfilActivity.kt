package com.app.clonewhatsapp.profile

import android.content.*
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import com.app.clonewhatsapp.R
import com.app.clonewhatsapp.databinding.ActivityPerfilBinding
import com.app.clonewhatsapp.model.Usuario
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.*
import java.util.*
import com.google.firebase.auth.UserProfileChangeRequest
import java.text.SimpleDateFormat


class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding
    private lateinit var toolbar: Toolbar
    lateinit var imageUri: Uri

    lateinit var storage: FirebaseStorage
    lateinit var auth: FirebaseAuth

    companion object {
        const val IMAGE_REQUEST = 1

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = findViewById(R.id.toolbarPrincipal)
        toolbar.title = ""
        setSupportActionBar(toolbar)


        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
//        var user = auth.currentUser

//        if (user != null) {
//
//            Log.d("PerfilActivity", "Colocar ImageURL em ImagemPerfil")
//
//        }


        binding.backArrowPerfil.setOnClickListener {
            onBackPressed()
        }

        binding.fabFotoPerfil.setOnClickListener {

            showBottomSheetPickPhoto()

        }

        var uid2 = FirebaseAuth.getInstance().uid
        var ref2 = FirebaseDatabase.getInstance().getReference("/usuarios/$uid2")


        ref2.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                var usuario = snapshot.getValue(Usuario::class.java)!!

                binding.editTextNome.setText(usuario?.nome)
                binding.textTelefone.setText(usuario?.numero)

                if (usuario?.profileImageUrl == "default") {

                    binding.ImagemPerfil.setImageResource(R.mipmap.ic_launcher)

                } else {
                    Picasso.get()
                        .load(usuario.profileImageUrl)
                        .into(binding.ImagemPerfil)
                }


            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("PerfilActivity",
                    "Nao foi possivel receber os dados "
                )
            }

        })


    }



    // ----------------------- Metodos de Click  ------------------------

    fun openGaleria() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, IMAGE_REQUEST)
    }

//    private fun openCamera(){
//
//        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
//            takePictureIntent.resolveActivity(packageManager)?.also {
//                startActivityForResult(takePictureIntent, TAKE_IMAGE_CODE)
//            }
//        }
//    }


    // ------------------------  Bottom Sheet ----------------------------------


    // bottomSheet.show(supportFragmentManager,"BottomSheetDialog")
    private fun showBottomSheetPickPhoto() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_pick, null)

        view.findViewById<View>(R.id.Imagem_Galeria).setOnClickListener {
            openGaleria()
            dialog.dismiss()
        }

        view.findViewById<View>(R.id.Imagem_Camera).setOnClickListener {

            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }


    // --------------------   Upload de Imagem------------------------------
    private fun uploadImage() {

        //val filename = UUID.randomUUID().toString() //$filename

        var ref = FirebaseStorage.getInstance()
            .getReference("imagens/${auth.currentUser?.uid}/perfil.jpg")

        //Da upload na imagem para o Storage e Mostra em ImagemPerfil
        ref.putFile(imageUri!!).addOnSuccessListener {

            Picasso.get()
                .load(imageUri)
                .into(binding.ImagemPerfil)

            Log.d("PerfilActivity", "Imagem salva com sucesso: ${it.metadata?.path}")

            //Pega o URL da Imagem e envia para saveUserFirebaseDatabase
            ref.downloadUrl.addOnSuccessListener {

                Log.d("PerfilActivity", "File location: $it")
                saveUserToFirebaseDatabase(it.toString())

            }


        }

    }


    // -------------------------- Salva Usuario no Banco de Dados ------------------------------

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {

        var uid = FirebaseAuth.getInstance().uid
        //val uid = UUID.randomUUID().toString()

        var usuario = Usuario(uid!!, binding.editTextNome.text.toString(),
            profileImageUrl, binding.textTelefone.text.toString())

        var ref = FirebaseDatabase.getInstance().getReference("/usuarios/$uid")


        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                usuario = snapshot.getValue(Usuario::class.java)!!

                binding.editTextNome.setText(usuario?.nome)
                binding.textTelefone.setText(usuario?.numero)


            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(
                    "PerfilActivity",
                    "Não foi possivel salvar usuario no Banco de Dados"
                )
            }

        })


        //Só deus sabe o que isso faz
        ref.setValue(usuario)
            .addOnSuccessListener {

                Log.d("PerfilActivity", "Usuario Salvo")
            }.addOnFailureListener() {

                Log.d("PerfilActivity", "Erro ao salvar usuario  no banco de dados")
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK &&
            data != null && data?.data!! != null
        ) {
            imageUri = data?.data!!
            uploadImage()

        }

    }



}
