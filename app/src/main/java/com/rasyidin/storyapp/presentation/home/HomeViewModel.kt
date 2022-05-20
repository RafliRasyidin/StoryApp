package com.rasyidin.storyapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rasyidin.storyapp.data.model.Story
import com.rasyidin.storyapp.data.repository.StoryRepository
import com.rasyidin.storyapp.data.utils.ResultState
import com.rasyidin.storyapp.data.utils.idle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: StoryRepository) : ViewModel() {

    private var _stories: MutableStateFlow<ResultState<List<Story>>> = idle()
    val stories: StateFlow<ResultState<List<Story>>> = _stories

    fun getStories(page: Int = 1, size: Int = 15, withLocation: Boolean = false) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.getStories(page, size, withLocation).collect { resultState ->
                _stories.value = resultState
            }
        }
}