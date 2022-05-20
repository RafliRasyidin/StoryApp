package com.rasyidin.storyapp.presentation.authentication

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rasyidin.storyapp.R
import com.rasyidin.storyapp.data.utils.isLoading
import com.rasyidin.storyapp.data.utils.onFailure
import com.rasyidin.storyapp.data.utils.onSuccess
import com.rasyidin.storyapp.databinding.FragmentLoginBinding
import com.rasyidin.storyapp.presentation.component.FragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : FragmentBinding<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private val viewModel: AuthViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onView()

        observeLogin()
    }

    private fun onView() = binding.run {
        tvRegister.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }

        btnLogin.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        if (email.isNotBlank() && email.isNotBlank()) {
            viewModel.login(email, password)
        }
    }

    private fun observeLogin() {
        lifecycleScope.launchWhenCreated {
            viewModel.eventLogin.collect { result ->

                binding.progressBar.isVisible = isLoading(result)

                result.onSuccess { login ->
                    viewModel.saveToken(login?.token.toString())
                    viewModel.setLoginState(true)
                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToMainActivity())
                    requireActivity().finish()
                }

                result.onFailure { message ->
                    when {
                        message.contains(requireActivity().getString(R.string.user_not_found)) ||
                                message.contains(requireActivity().getString(R.string.invalid_password)) -> {
                            Toast.makeText(
                                requireActivity(),
                                requireActivity().getString(R.string.error_login),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                    Log.e("LoginFragment", "Error: $message")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}