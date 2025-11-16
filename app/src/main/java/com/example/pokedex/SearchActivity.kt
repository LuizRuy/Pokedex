package com.example.pokedex

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pokedex.data.model.Pokemon
import com.example.pokedex.data.network.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private lateinit var searchResultsAdapter: PokemonAdapter
    private var searchType: String? = null

    companion object {
        const val SEARCH_MODE = "SEARCH_MODE"
        const val TYPE = "TYPE"
        const val ABILITY = "ABILITY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchType = intent.getStringExtra(SEARCH_MODE)

        val toolbar: Toolbar = findViewById(R.id.toolbar_search)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val etQuery = findViewById<EditText>(R.id.etSearchQuery)
        val btnSearch = findViewById<Button>(R.id.btnSearch)

        if (searchType == TYPE) {
            supportActionBar?.title = "Pesquisar por Tipo"
            etQuery.hint = "Digite o tipo do Pokémon"
        } else {
            supportActionBar?.title = "Pesquisar por Habilidade"
            etQuery.hint = "Digite a habilidade do Pokémon"
        }

        setupRecyclerView()

        btnSearch.setOnClickListener {
            val query = etQuery.text.toString().trim()
            if (query.isNotEmpty()) {
                performSearch(query)
            }
        }
    }

    private fun setupRecyclerView() {
        val rvResults = findViewById<RecyclerView>(R.id.rvSearchResults)
        rvResults.layoutManager = LinearLayoutManager(this)
        searchResultsAdapter = PokemonAdapter { pokemon ->
            val intent = Intent(this, PokemonDetailActivity::class.java)
            intent.putExtra("POKEMON_ID", pokemon.id)
            startActivity(intent)
        }
        rvResults.adapter = searchResultsAdapter
    }

    private fun performSearch(query: String) {
        lifecycleScope.launch {
            val filters = mutableMapOf<String, String>()
            if (searchType == TYPE) {
                filters["tipo"] = query
            } else {
                filters["habilidade"] = query
            }

            try {
                val apiService = RetrofitInstance.getApiService(this@SearchActivity)
                val response = apiService.searchPokemons(filters)

                if (response.isSuccessful && response.body() != null) {
                    val results = response.body()!!
                    searchResultsAdapter.updateData(results)
                    if (results.isEmpty()) {
                        Toast.makeText(this@SearchActivity, "Nenhum Pokémon encontrado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@SearchActivity, "Falha na pesquisa", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Log.e("SearchActivity", "Erro na pesquisa", e)
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