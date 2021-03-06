package com.app.clonewhatsapp.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.app.clonewhatsapp.R
import com.app.clonewhatsapp.databinding.FragmentStatusBinding
import com.app.clonewhatsapp.model.Usuario
import com.app.clonewhatsapp.ui.configuracao.ConfiguracoesActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class StatusFragment : Fragment() {

    private lateinit var binding: FragmentStatusBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStatusBinding.inflate(inflater,container,false)


        getData()
        return binding.root


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
        when(item.itemId){

            R.id.menuConfiguracoes -> {
                startActivity(Intent(activity, ConfiguracoesActivity::class.java))
            }
        }
    }

    private fun getData() {
        var uid = FirebaseAuth.getInstance().uid
        var ref = FirebaseDatabase.getInstance().getReference("/usuarios/$uid")



        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var usuario = snapshot.getValue(Usuario::class.java)!!

                if (usuario?.profileImageUrl == "") {

                    binding.imagemPerfilStatus.setImageResource(R.mipmap.ic_launcher)

                } else {
                    Picasso.get()
                        .load(usuario.profileImageUrl)
                        .into(binding.imagemPerfilStatus)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

}