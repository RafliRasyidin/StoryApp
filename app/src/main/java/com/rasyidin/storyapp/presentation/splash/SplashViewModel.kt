package com.rasyidin.storyapp.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rasyidin.storyapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {

    private var _loginState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loginState: StateFlow<Boolean> = _loginState

    private var _onBoardingState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val onBoardingState: StateFlow<Boolean> = _onBoardingState

    private var _token: MutableStateFlow<String> = MutableStateFlow("")
    val token: StateFlow<String> = _token
    init {
        getOnBoardingState()
        getLoginState()
        getToken()
    }

    private fun getToken() = viewModelScope.launch {
        repository.getToken().collect {
            _token.value = it
        }
    }

    private fun getOnBoardingState() = viewModelScope.launch(Dispatchers.IO) {
        repository.getOnBoardingState().collect { state ->
            _onBoardingState.value = state
        }
    }

    private fun getLoginState() = viewModelScope.launch(Dispatchers.IO) {
        repository.getLoginState().collect { state ->
            _loginState.value = state
        }
    }

}