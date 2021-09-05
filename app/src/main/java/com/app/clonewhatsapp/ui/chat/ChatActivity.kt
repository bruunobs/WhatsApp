package com.app.clonewhatsapp.ui.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.app.clonewhatsapp.R
import androidx.appcompat.widget.Toolbar
import com.app.clonewhatsapp.databinding.ActivityChatBinding
import com.app.clonewhatsapp.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var toolbar: Toolbar
    var firebaseUser: FirebaseUser? = null
    var reference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = findViewById(R.id.toolbarPrincipal)
        toolbar.title = ""
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var intent = intent
        var contatoId = intent.getStringExtra("contatoID")
        firebaseUser = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().getReference("/usuarios/").child(contatoId!!)

        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var contato = snapshot.getValue(Usuario::class.java)
                binding.nomeChat.text = contato!!.nome

                if (contato!!.profileImageUrl == "") {

                    binding.ImagemChat.setImageResource(R.mipmap.ic_launcher)

                } else {
                    Picasso.get()
                        .load(contato.profileImageUrl)
                        .into(binding.ImagemChat)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


        binding.fabMic.setOnClickListener {
            var mensagem: String = binding.EditTextChat.text.toString()

            if(mensagem.isEmpty()){
                Toast.makeText(applicationContext,"Mensagem esta vazia",Toast.LENGTH_SHORT).show()
            }else{
                sendMessage(firebaseUser!!.uid,contatoId,mensagem)
            }
        }


    }

    private fun sendMessage(remetenteId: String, destinatarioId: String, mensagem: String){
        var reference: DatabaseReference?  = FirebaseDatabase.getInstance().getReference()

        var hashMap: HashMap<String,String> = HashMap()
        hashMap.put("remetenteId", remetenteId)
        hashMap.put("destinaratioId", destinatarioId)
        hashMap.put("mensagem", mensagem)
        reference!!.child("Chat").push().setValue(hashMap)
    }



    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        finish()
        return true
    }
}