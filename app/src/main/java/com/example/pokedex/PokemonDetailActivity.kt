package com.example.pokedex

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.example.pokedex.data.model.Pokemon
import com.example.pokedex.data.model.PokemonPayload
import com.example.pokedex.data.network.RetrofitInstance
import kotlinx.coroutines.launch

class PokemonDetailActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etType: EditText
    private lateinit var etAbility1: EditText
    private lateinit var etAbility2: EditText
    private lateinit var etAbility3: EditText
    private lateinit var btnEditSave: Button
    private lateinit var btnDelete: Button

    private var isEditing = false
    private var currentPokemon: Pokemon? = null
    private var pokemonId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokemon_detail)

        val toolbar: Toolbar = findViewById(R.id.toolbar_pokemon_detail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detalhes"

        etName = findViewById(R.id.etPokemonNameDetail)
        etType = findViewById(R.id.etPokemonTypeDetail)
        etAbility1 = findViewById(R.id.etPokemonAbility1Detail)
        etAbility2 = findViewById(R.id.etPokemonAbility2Detail)
        etAbility3 = findViewById(R.id.etPokemonAbility3Detail)
        btnEditSave = findViewById(R.id.btnEditSave)
        btnDelete = findViewById(R.id.btnDelete)

        pokemonId = intent.getStringExtra("POKEMON_ID")

        if (pokemonId == null) {
            finish()
            return
        }

        fetchPokemonDetails(pokemonId!!)

        btnEditSave.setOnClickListener { handleEditSaveClick() }
        btnDelete.setOnClickListener { handleDeleteClick() }
    }

    private fun fetchPokemonDetails(id: String) {
        lifecycleScope.launch {
            try {
                val apiService = RetrofitInstance.getApiService(this@PokemonDetailActivity)
                val response = apiService.getPokemonById(id)

                if (response.isSuccessful && response.body() != null) {
                    currentPokemon = response.body()
                    fetchPokemon()
                } else {
                    Toast.makeText(this@PokemonDetailActivity, "Falha ao buscar detalhes", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("PokemonDetailActivity", "Erro ao buscar detalhes", e)
            }
        }
    }

    private fun fetchPokemon() {
        currentPokemon?.let {
            etName.setText(it.name)
            etType.setText(it.type)
            etAbility1.setText(it.abilities.getOrNull(0) ?: "")
            etAbility2.setText(it.abilities.getOrNull(1) ?: "")
            etAbility3.setText(it.abilities.getOrNull(2) ?: "")
        }
    }

    private fun handleEditSaveClick() {
        if (isEditing) {
            savePokemonChanges()
        } else {
            toggleEditMode(true)
        }
    }

    private fun toggleEditMode(enable: Boolean) {
        isEditing = enable
        etName.isEnabled = enable
        etType.isEnabled = enable
        etAbility1.isEnabled = enable
        etAbility2.isEnabled = enable
        etAbility3.isEnabled = enable
        btnEditSave.text = if (enable) "Salvar" else "Editar"
    }

    private fun savePokemonChanges() {
        val name = etName.text.toString().trim()
        val type = etType.text.toString().trim()
        val abilities = listOf(etAbility1, etAbility2, etAbility3)
            .map { it.text.toString().trim() }
            .filter { it.isNotEmpty() }

        if (name.isEmpty() || type.isEmpty() || abilities.isEmpty()) return

        val pokemonPayload = PokemonPayload(name = name, type = type, abilities = abilities)

        lifecycleScope.launch {
            try {
                val apiService = RetrofitInstance.getApiService(this@PokemonDetailActivity)
                val response = apiService.updatePokemon(pokemonId!!, pokemonPayload)

                if (response.isSuccessful) {
                    currentPokemon = response.body()
                    fetchPokemon()
                    toggleEditMode(false)
                    AlertDialog.Builder(this@PokemonDetailActivity).setTitle("Sucesso").setMessage("Pokémon atualizado!")
                        .setPositiveButton("OK", null).show()
                } else {
                    val errorMessage = if (response.code() == 409) {
                        "Você já possui outro pokémon com este nome."
                    } else {
                        "Falha ao atualizar. Código: ${response.code()}"
                    }
                    AlertDialog.Builder(this@PokemonDetailActivity)
                        .setTitle("Erro")
                        .setMessage(errorMessage)
                        .setPositiveButton("OK", null)
                        .show()
                }
            } catch (e: Exception) {
                Log.e("PokemonDetailActivity", "Erro ao atualizar", e)
            }
        }
    }

    private fun handleDeleteClick() {
        AlertDialog.Builder(this)
            .setTitle("Excluir Pokémon")
            .setMessage("Tem certeza que deseja excluir o ${currentPokemon?.name}?")
            .setPositiveButton("Excluir") { _, _ -> deletePokemon() }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deletePokemon() {
        lifecycleScope.launch {
            try {
                val apiService = RetrofitInstance.getApiService(this@PokemonDetailActivity)
                val response = apiService.deletePokemon(pokemonId!!)

                if (response.isSuccessful) {
                    AlertDialog.Builder(this@PokemonDetailActivity)
                        .setTitle("Sucesso")
                        .setMessage("Pokémon excluído com sucesso!")
                        .setPositiveButton("OK") { _, _ -> finish() }
                        .setCancelable(false)
                        .show()
                } else {
                    AlertDialog.Builder(this@PokemonDetailActivity)
                        .setTitle("Erro")
                        .setMessage("Falha ao excluir o Pokémon. Código: ${response.code()}")
                        .setPositiveButton("OK", null)
                        .show()
                }
            } catch (e: Exception) {
                Log.e("PokemonDetailActivity", "Erro ao excluir", e)
                AlertDialog.Builder(this@PokemonDetailActivity)
                    .setTitle("Erro de Conexão")
                    .setMessage("Não foi possível conectar à API.")
                    .setPositiveButton("OK", null)
                    .show()
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