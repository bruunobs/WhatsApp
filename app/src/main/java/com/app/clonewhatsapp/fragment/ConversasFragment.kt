package com.app.clonewhatsapp.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.clonewhatsapp.R
import com.app.clonewhatsapp.adapter.ChatAdapter
import com.app.clonewhatsapp.adapter.ContatosAdapter
import com.app.clonewhatsapp.adapter.ConversasAdapter
import com.app.clonewhatsapp.databinding.FragmentConversasBinding
import com.app.clonewhatsapp.model.Chat
import com.app.clonewhatsapp.model.Conversas
import com.app.clonewhatsapp.model.Usuario
import com.app.clonewhatsapp.ui.contatos.ContatosActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.NonCancellable.children


class ConversasFragment : Fragment() {

    lateinit var binding: FragmentConversasBinding

    private var conversasContatos: ArrayList<Conversas>? = null
    private var recyclerView: RecyclerView? = null
    private var conversasAdapter: ConversasAdapter? = null
    private var valueEventListenerConversas: ChildEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentConversasBinding.inflate(inflater, container, false)

        // Configurando RecyclerView
        recyclerView = binding.RecyclerViewConversas
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(activity)
        conversasContatos = ArrayList()



        binding.fabContatos.setOnClickListener {
            val intent = Intent(context, ContatosActivity::class.java)
            startActivity(intent)


        }

        return binding.root


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.menu_principal,menu)


        val item = menu!!.findItem(R.id.menuPesquisaPrincipal)
        val searchView = item.actionView as SearchView
        searchView.queryHint = "Pesquisar..."

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                //contatosAdapter!!.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                conversasAdapter!!.filter.filter(newText)

                return true
            }

        })

        return super.onCreateOptionsMenu(menu,inflater)

    }



    override fun onStart() {
        super.onStart()
        RecuperarConversas()
    }

    override fun onStop() {
        super.onStop()
        val remetenteId = FirebaseAuth.getInstance().uid
        var conversasRef = FirebaseDatabase.getInstance().getReference("/conversas-usuarios/$remetenteId")

        conversasRef.removeEventListener(valueEventListenerConversas!!)

    }

    val ultimaMensagemMap = HashMap<String, Conversas>()

    private fun refreshRecyclerViewConversas(){
        conversasContatos!!.clear()
        ultimaMensagemMap.values.forEach {
            conversasContatos!!.add(it)
        }
    }

    private fun RecuperarConversas(){
//        var intent = Intent()
//        val destinatarioId = intent.getStringExtra("contatoID")
        val remetenteId = FirebaseAuth.getInstance().uid

        val ref = FirebaseDatabase.getInstance().getReference("/conversas-usuarios/$remetenteId")

        valueEventListenerConversas = ref!!.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                var conversas = snapshot.getValue(Conversas::class.java)

                ultimaMensagemMap[snapshot.key!!] = conversas!!
                refreshRecyclerViewConversas()

                conversasAdapter = ConversasAdapter(context!!,conversasContatos!!,)
                recyclerView!!.adapter = conversasAdapter


            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                var conversas = snapshot.getValue(Conversas::class.java)

                ultimaMensagemMap[snapshot.key!!] = conversas!!
                refreshRecyclerViewConversas()


            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })



    }


}