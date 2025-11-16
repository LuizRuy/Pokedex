package com.example.pokedex.data.model

import com.google.gson.annotations.SerializedName

/**
 * Represents the data payload sent to the server when creating or updating a Pokemon.
 * It only contains the fields the server actually needs.
 */
data class PokemonPayload(
    val name: String,
    @SerializedName("tipo")
    val type: String,
    @SerializedName("habilidades")
    val abilities: List<String>
)