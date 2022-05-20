package com.rasyidin.storyapp.presentation.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.rasyidin.storyapp.presentation.authentication.AuthActivity
import com.rasyidin.storyapp.presentation.home.MainActivity
import com.rasyidin.storyapp.presentation.on_boarding.OnBoardingActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModels()

    private var isOnBoarded: Boolean = false

    private var isAuthenticated: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeOnBoardingState()
        observeLoginState()
        lifecycleScope.launchWhenCreated {
            checkSession()
        }
    }

    private suspend fun checkSession() {
        delay(200)
        val intent: Intent
        if (isOnBoarded) {
            if (isAuthenticated) {
                intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
            }
        } else {
            intent = Intent(this, OnBoardingActivity::class.java)
            startActivity(intent)
        }

    }

    private fun observeLoginState() {
        lifecycleScope.launchWhenCreated {
            viewModel.token.collect {
                Log.d("SplashActivity", it)
            }
        }
        lifecycleScope.launchWhenCreated {
            viewModel.loginState.collect { state ->
                isAuthenticated = state
            }
        }
    }

    private fun observeOnBoardingState() {
        lifecycleScope.launchWhenCreated {
            viewModel.onBoardingState.collect { state ->
                isOnBoarded = state
            }
        }
    }
}