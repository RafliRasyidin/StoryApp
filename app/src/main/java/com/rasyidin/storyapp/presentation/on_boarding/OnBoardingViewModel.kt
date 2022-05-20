package com.rasyidin.storyapp.presentation.on_boarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rasyidin.storyapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(private val repository: AuthRepository) :
    ViewModel() {

    fun setOnBoardingState(state: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        repository.setOnBoardingState(state)
    }

}