package com.rasyidin.storyapp.presentation.authentication

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.rasyidin.storyapp.R
import com.rasyidin.storyapp.databinding.ActivityAuthBinding
import com.rasyidin.storyapp.presentation.component.ActivityBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : ActivityBinding<ActivityAuthBinding>() {

    private lateinit var navHost: NavHostFragment
    private lateinit var navController: NavController

    override fun inflateViewBinding(): ActivityAuthBinding {
        return ActivityAuthBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navHost =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_auth) as NavHostFragment
        navController = navHost.navController
        supportActionBar?.hide()
    }
}