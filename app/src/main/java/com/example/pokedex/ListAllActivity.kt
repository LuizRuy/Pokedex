package com.example.pokedex

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pokedex.data.network.RetrofitInstance
import kotlinx.coroutines.launch

class ListAllActivity : AppCompatActivity() {

    private lateinit var pokemonAdapter: PokemonAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_all)

        val toolbar: Toolbar = findViewById(R.id.toolbar_list_all)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Todos os Pokémons"

        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        fetchPokemons() // Fetch data every time the screen is resumed
    }

    private fun setupRecyclerView() {
        val rvPokemons = findViewById<RecyclerView>(R.id.rvPokemons)
        rvPokemons.layoutManager = LinearLayoutManager(this)
        pokemonAdapter = PokemonAdapter { pokemon ->
            val intent = Intent(this, PokemonDetailActivity::class.java)
            intent.putExtra("POKEMON_ID", pokemon.id)
            startActivity(intent)
        }
        rvPokemons.adapter = pokemonAdapter
    }

    private fun fetchPokemons() {
        lifecycleScope.launch {
            try {
                val apiService = RetrofitInstance.getApiService(this@ListAllActivity)
                val response = apiService.getPokemons()

                if (response.isSuccessful && response.body() != null) {
                    pokemonAdapter.updateData(response.body()!!)
                } else {
                    Toast.makeText(this@ListAllActivity, "Falha ao buscar Pokémons", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("ListAllActivity", "Erro ao buscar Pokémons", e)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}