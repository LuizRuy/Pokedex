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
import com.example.pokedex.data.model.PokemonPayload
import com.example.pokedex.data.network.RetrofitInstance
import kotlinx.coroutines.launch

class AddPokemonActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_pokemon)

        val toolbar: Toolbar = findViewById(R.id.toolbar_add_pokemon)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Cadastrar Pokémon"

        val etPokemonName = findViewById<EditText>(R.id.etPokemonName)
        val etPokemonType = findViewById<EditText>(R.id.etPokemonType)
        val etAbility1 = findViewById<EditText>(R.id.etPokemonAbility1)
        val etAbility2 = findViewById<EditText>(R.id.etPokemonAbility2)
        val etAbility3 = findViewById<EditText>(R.id.etPokemonAbility3)
        val btnSavePokemon = findViewById<Button>(R.id.btnSavePokemon)

        btnSavePokemon.setOnClickListener {
            val name = etPokemonName.text.toString().trim()
            val type = etPokemonType.text.toString().trim()
            val abilities = listOf(etAbility1, etAbility2, etAbility3)
                .map { it.text.toString().trim() }
                .filter { it.isNotEmpty() }

            if (name.isEmpty() || type.isEmpty() || abilities.isEmpty()) {
                Toast.makeText(this, "Nome, Tipo e pelo menos uma Habilidade são obrigatórios.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            btnSavePokemon.isEnabled = false
            btnSavePokemon.text = "Carregando..."

            val pokemonPayload = PokemonPayload(name = name, type = type, abilities = abilities)

            lifecycleScope.launch {
                try {
                    val apiService = RetrofitInstance.getApiService(this@AddPokemonActivity)
                    val response = apiService.createPokemon(pokemonPayload)

                    if (response.isSuccessful) {
                        AlertDialog.Builder(this@AddPokemonActivity)
                            .setTitle("Sucesso")
                            .setMessage("Pokémon '${response.body()?.name}' cadastrado com sucesso!")
                            .setPositiveButton("OK") { _, _ -> finish() }
                            .show()
                    } else {
                        val errorMessage = if (response.code() == 409) {
                            "Já existe um Pokémon com este nome."
                        } else {
                            "Falha ao cadastrar Pokémon. Código: ${response.code()}"
                        }
                        AlertDialog.Builder(this@AddPokemonActivity)
                            .setTitle("Erro")
                            .setMessage(errorMessage)
                            .setPositiveButton("OK", null)
                            .show()
                    }
                } catch (e: Exception) {
                    Log.e("AddPokemonActivity", "Erro ao cadastrar Pokémon", e)
                    AlertDialog.Builder(this@AddPokemonActivity)
                        .setTitle("Erro de Conexão")
                        .setMessage("Não foi possível conectar à API.")
                        .setPositiveButton("OK", null)
                        .show()
                } finally {
                    if (!isFinishing) {
                        btnSavePokemon.isEnabled = true
                        btnSavePokemon.text = "Salvar"
                    }
                }
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