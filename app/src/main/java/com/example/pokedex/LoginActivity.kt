package com.example.pokedex

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.pokedex.data.SessionManager
import com.example.pokedex.data.model.LoginRequest
import com.example.pokedex.data.network.RetrofitInstance
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sessionManager = SessionManager(this)

        val emailEditText = findViewById<EditText>(R.id.editTextEmail)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val loginButton = findViewById<Button>(R.id.buttonLogin)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                lifecycleScope.launch {
                    try {
                        val apiService = RetrofitInstance.getApiService(this@LoginActivity)
                        val response = apiService.login(LoginRequest(email, password))

                        if (response.isSuccessful && response.body() != null) {
                            val loginResponse = response.body()!!

                            sessionManager.saveAuthToken(loginResponse.token)
                            sessionManager.saveUser(loginResponse.user)
                            Log.d("LoginActivity", "Token and user info saved.")

                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()

                        } else {
                            AlertDialog.Builder(this@LoginActivity)
                                .setTitle("Falha no Login")
                                .setMessage("Login ou Senha incorretos")
                                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                                .show()
                        }
                    } catch (e: Exception) {
                        Log.e("LoginActivity", "Error during login", e)
                        Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}