package com.rasyidin.storyapp.presentation.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rasyidin.storyapp.data.model.Login
import com.rasyidin.storyapp.data.repository.AuthRepository
import com.rasyidin.storyapp.data.source.remote.response.ApiResponse
import com.rasyidin.storyapp.data.utils.ResultState
import com.rasyidin.storyapp.data.utils.idle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {

    private var _eventRegister: MutableSharedFlow<ResultState<ApiResponse>> = idle()
    val eventRegister: SharedFlow<ResultState<ApiResponse>> = _eventRegister

    private var _eventLogin: MutableSharedFlow<ResultState<Login>> = idle()
    val eventLogin: SharedFlow<ResultState<Login>> = _eventLogin

    fun register(name: String, email: String, password: String) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.register(name, email, password).collect { resultState ->
                _eventRegister.emit(resultState)
            }
        }

    fun login(email: String, password: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.login(email, password).collect { resultState ->
            _eventLogin.emit(resultState)
        }
    }

    fun saveToken(token: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.saveToken(token)
    }

    fun setLoginState(state: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        repository.setLoginState(state)
    }


}