package com.example.pokedex.data

import android.content.Context
import android.content.SharedPreferences
import com.example.pokedex.data.model.User

class SessionManager(context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences("PokedexAppPrefs", Context.MODE_PRIVATE)

    companion object {
        const val AUTH_TOKEN = "auth_token"
        const val USER_ID = "user_id"
        const val USER_NAME = "user_name"
    }

    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(AUTH_TOKEN, token)
        editor.apply()
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(AUTH_TOKEN, null)
    }

    fun saveUser(user: User) {
        val editor = prefs.edit()
        editor.putString(USER_ID, user.id)
        editor.putString(USER_NAME, user.name)
        editor.apply()
    }

    fun logout() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}