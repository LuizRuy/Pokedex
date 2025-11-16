package com.example.pokedex.data.network

import com.example.pokedex.data.model.LoginRequest
import com.example.pokedex.data.model.LoginResponse
import com.example.pokedex.data.model.Pokemon
import com.example.pokedex.data.model.PokemonPayload
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // Authentication
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // Pokemon CRUD
    @GET("pokemons")
    suspend fun getPokemons(): Response<List<Pokemon>>

    @POST("pokemons")
    suspend fun createPokemon(@Body payload: PokemonPayload): Response<Pokemon>

    @GET("pokemons/{id}")
    suspend fun getPokemonById(@Path("id") id: String): Response<Pokemon>

    @PUT("pokemons/{id}")
    suspend fun updatePokemon(@Path("id") id: String, @Body payload: PokemonPayload): Response<Pokemon>

    @DELETE("pokemons/{id}")
    suspend fun deletePokemon(@Path("id") id: String): Response<Unit>

    // Unified Search
    @GET("pokemons/search")
    suspend fun searchPokemons(@QueryMap filters: Map<String, String>): Response<List<Pokemon>>
}