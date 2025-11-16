package com.example.pokedex.data.model

data class LoginResponse(
    val user: User,
    val token: String
)

data class User(
    val id: String,
    val name: String,
    val email: String,
    val createdAt: String
)