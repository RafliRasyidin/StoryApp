package com.rasyidin.storyapp.presentation.authentication

import android.os.Bundle
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
import com.rasyidin.storyapp.databinding.FragmentRegisterBinding
import com.rasyidin.storyapp.presentation.component.FragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment :
    FragmentBinding<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) {

    private val viewModel: AuthViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onView()

        observeRegister()

    }

    private fun onView() = binding.apply {
        tvRegister.setOnClickListener {
            findNavController().popBackStack()
        }

        btnRegister.setOnClickListener {
            register()
        }
    }

    private fun register() {
        val name = binding.etName.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
            viewModel.register(name, email, password)
        }
    }

    private fun observeRegister() {
        lifecycleScope.launchWhenCreated {
            viewModel.eventRegister.collect { result ->
                binding.progressBar.isVisible = isLoading(result)

                result.onSuccess {
                    findNavController().popBackStack()
                    Toast.makeText(
                        requireActivity(),
                        requireActivity().getString(R.string.success_register),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                result.onFailure { message ->
                    when {
                        message.contains(requireActivity().getString(R.string.email_already_take)) -> {
                            Toast.makeText(
                                requireActivity(),
                                requireActivity().getString(R.string.error_email_already_taken),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        message.contains(requireActivity().getString(R.string.invalid_email)) -> {
                            Toast.makeText(
                                requireActivity(),
                                requireActivity().getString(R.string.error_email),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        message.contains(requireActivity().getString(R.string.invalid_password_register)) -> {
                            Toast.makeText(
                                requireActivity(),
                                requireActivity().getString(R.string.error_password),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}