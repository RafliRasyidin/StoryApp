package com.rasyidin.storyapp.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rasyidin.storyapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(private val repository: AuthRepository): ViewModel() {

    fun logout() {
        viewModelScope.launch {
            val resultLoginState = async {
                repository.setLoginState(false)
            }

            val resultToken = async {
                repository.removeToken()
            }

            resultLoginState.await()
            resultToken.await()
        }
    }
}