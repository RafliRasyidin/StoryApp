package com.rasyidin.storyapp.presentation.on_boarding

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import com.rasyidin.storyapp.databinding.ActivityOnBoardingBinding
import com.rasyidin.storyapp.presentation.authentication.AuthActivity
import com.rasyidin.storyapp.presentation.component.ActivityBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class OnBoardingActivity : ActivityBinding<ActivityOnBoardingBinding>() {

    private val viewModel: OnBoardingViewModel by viewModels()

    override fun inflateViewBinding(): ActivityOnBoardingBinding {
        return ActivityOnBoardingBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullScreen()
        onButtonNextClick()
    }

    private fun onButtonNextClick() {
        binding.btnNext.setOnClickListener {
            viewModel.setOnBoardingState(true)
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }
    }

    @Suppress("DEPRECATION")
    private fun setFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
            )
        }
        supportActionBar?.hide()
    }
}