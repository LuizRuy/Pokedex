package com.example.pokedex.data.model

data class Dashboard(
    val totalPokemons: Int,
    val topTipos: List<TopType>,
    val topHabilidades: List<TopAbility>
)

data class TopType(
    val tipo: String,
    val count: Int
)

data class TopAbility(
    val habilidade: String,
    val count: Int
)
