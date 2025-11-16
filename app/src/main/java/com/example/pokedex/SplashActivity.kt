package com.example.pokedex

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.pokedex.data.SessionManager

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val sessionManager = SessionManager(this)
            
            // Check if user is logged in
            if (sessionManager.fetchAuthToken() != null) {
                // User is logged in, go to MainActivity
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // User is not logged in, go to LoginActivity
                startActivity(Intent(this, LoginActivity::class.java))
            }
            
            // Finish SplashActivity so it's not in the back stack
            finish()
        }, 3000) // 3 seconds delay
    }
}