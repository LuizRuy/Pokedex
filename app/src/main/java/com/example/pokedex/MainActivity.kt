package com.example.pokedex

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.example.pokedex.data.SessionManager
import com.example.pokedex.data.network.RetrofitInstance
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var tvPokemonCount: TextView
    private lateinit var tvTopTypes: TextView
    private lateinit var tvTopAbilities: TextView
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sessionManager = SessionManager(this)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Pokédex"

        tvPokemonCount = findViewById(R.id.tvPokemonCount)
        tvTopTypes = findViewById(R.id.tvTopTypes)
        tvTopAbilities = findViewById(R.id.tvTopAbilities)
    }

    override fun onResume() {
        super.onResume()
        fetchDashboardData()
    }

    private fun fetchDashboardData() {
        lifecycleScope.launch {
            try {
                val apiService = RetrofitInstance.getApiService(this@MainActivity)
                val response = apiService.getDashboardData()

                if (response.isSuccessful && response.body() != null) {
                    val dashboardData = response.body()!!

                    tvPokemonCount.text = "Pokémons cadastrados: ${dashboardData.totalPokemons}"

                    val topTypes = dashboardData.topTipos
                        .joinToString("\n") { "- ${it.tipo} (${it.count})" }
                    tvTopTypes.text = "Top 3 Tipos:\n$topTypes"

                    val topAbilities = dashboardData.topHabilidades
                        .joinToString("\n") { "- ${it.habilidade} (${it.count})" }
                    tvTopAbilities.text = "Top 3 Habilidades:\n$topAbilities"

                } else {
                    Toast.makeText(this@MainActivity, "Falha ao carregar dados do dashboard", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Erro ao buscar dados do dashboard", e)
                Toast.makeText(this@MainActivity, "Erro de conexão: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_pokemon -> {
                startActivity(Intent(this, AddPokemonActivity::class.java))
                true
            }
            R.id.action_list_all -> {
                startActivity(Intent(this, ListAllActivity::class.java))
                true
            }
            R.id.action_search_by_type -> {
                val intent = Intent(this, SearchActivity::class.java)
                intent.putExtra(SearchActivity.SEARCH_MODE, SearchActivity.TYPE)
                startActivity(intent)
                true
            }
            R.id.action_search_by_ability -> {
                val intent = Intent(this, SearchActivity::class.java)
                intent.putExtra(SearchActivity.SEARCH_MODE, SearchActivity.ABILITY)
                startActivity(intent)
                true
            }
            R.id.action_exit -> {
                sessionManager.logout()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
