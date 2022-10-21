package com.rasyidin.storyapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import com.rasyidin.storyapp.data.model.Story
import com.rasyidin.storyapp.data.repository.StoryRepository
import com.rasyidin.storyapp.data.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: StoryRepository) : ViewModel() {

    private var _stories: MutableStateFlow<ResultState<List<Story>>> = idle()
    val stories: StateFlow<ResultState<List<Story>>> = _stories

    private var _pagingStories: MutableStateFlow<PagingData<Story>> =
        MutableStateFlow(PagingData.empty())
    val pagingStories: StateFlow<PagingData<Story>> = _pagingStories

    private var _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private var _errorMessage: Channel<String> = Channel()
    val errorMessage = _errorMessage.receiveAsFlow()

    fun getStories(page: Int = 1, size: Int = 15, withLocation: Int = 1) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.getStories(page, size, withLocation).collect { resultState ->
                _stories.value = resultState
            }
        }

    fun getStories() = viewModelScope.launch {
        repository.getStories().collect { resultState ->
            resultState.onSuccess { pager ->
                _loading.value = false
                viewModelScope.launch {
                    pager?.flow?.collect { pagingData ->
                        _pagingStories.value = pagingData.map { storyEntity ->
                            storyEntity.toStory()
                        }
                    }
                }
            }

            resultState.onLoading {
                _loading.value = true
            }

            resultState.onFailure { message ->
                _loading.value = false
                viewModelScope.launch {
                    _errorMessage.send(message)
                }
            }

        }
    }
}