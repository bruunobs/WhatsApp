package com.app.clonewhatsapp.principal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.app.clonewhatsapp.ConfiguracoesActivity
import com.app.clonewhatsapp.R
import com.app.clonewhatsapp.adapter.ViewPagerAdapter
import com.app.clonewhatsapp.login.LoginActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth


class PrincipalActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        toolbar = findViewById(R.id.toolbarPrincipal)
        toolbar.setTitle("WhatsApp")
        setSupportActionBar(toolbar)

        auth = FirebaseAuth.getInstance()

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager2 = findViewById<ViewPager2>(R.id.viewPager)

        val adapter = ViewPagerAdapter(supportFragmentManager,lifecycle)
        viewPager2.adapter = adapter

        TabLayoutMediator(tabLayout,viewPager2){tab,position ->
            when(position){
                0->{
                    tab.text="Conversas"
                }
                1->{
                    tab.text="Chamadas"
                }
            }
        }.attach()


    }
    //Configuração Abas


    //Configuração Menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_principal,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menuSair -> {
                deslogarUsuario()

            }
            R.id.menuConfiguracoes -> {
                startActivity(Intent(this,ConfiguracoesActivity::class.java))
            }

        }
        return super.onOptionsItemSelected(item)
    }

    fun deslogarUsuario(){
        try {
            auth.signOut()
            startActivity(Intent(this@PrincipalActivity, LoginActivity::class.java))
            finish()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}