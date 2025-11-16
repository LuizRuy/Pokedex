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
        supportActionBar?.title = "Pokédex" // Set a simple title

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
                val response = apiService.getPokemons()

                if (response.isSuccessful && response.body() != null) {
                    val pokemons = response.body()!!

                    tvPokemonCount.text = "Pokémons cadastrados: ${pokemons.size}"

                    val topTypes = pokemons.groupingBy { it.type }.eachCount()
                        .toList().sortedByDescending { it.second }.take(3)
                        .joinToString("\n") { "- ${it.first} (${it.second})" }
                    tvTopTypes.text = "Top 3 Tipos:\n$topTypes"

                    val topAbilities = pokemons.flatMap { it.abilities }.groupingBy { it }.eachCount()
                        .toList().sortedByDescending { it.second }.take(3)
                        .joinToString("\n") { "- ${it.first} (${it.second})" }
                    tvTopAbilities.text = "Top 3 Habilidades:\n$topAbilities"

                } else {
                    // Handle error or empty state
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Erro ao buscar dados do dashboard", e)
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