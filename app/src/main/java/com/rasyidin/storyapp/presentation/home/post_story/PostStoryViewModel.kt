package com.rasyidin.storyapp.presentation.home.post_story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rasyidin.storyapp.data.repository.StoryRepository
import com.rasyidin.storyapp.data.source.remote.response.ApiResponse
import com.rasyidin.storyapp.data.utils.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PostStoryViewModel @Inject constructor(private val repository: StoryRepository) :
    ViewModel() {

    private var _postStory: Channel<ResultState<ApiResponse>> = Channel()
    val postStory = _postStory.receiveAsFlow()

    fun postStory(description: String, imageFile: File) {
        viewModelScope.launch {
            repository.postStory(description, imageFile).collect { resultState ->
                _postStory.send(resultState)
            }
        }
    }
}