package com.example.pokedex.data.model

import com.google.gson.annotations.SerializedName

data class Pokemon(
    val id: String,
    val name: String,
    @SerializedName("tipo")
    val type: String,
    @SerializedName("habilidades")
    val abilities: List<String>,
    // userId is nullable because the server does not always return it.
    val userId: String?
)