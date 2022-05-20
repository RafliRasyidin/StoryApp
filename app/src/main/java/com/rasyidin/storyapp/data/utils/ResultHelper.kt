package com.rasyidin.storyapp.data.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import retrofit2.Response

sealed class ResultState<T : Any> {
    class Loading<T : Any> : ResultState<T>()
    class Idle<T : Any> : ResultState<T>()
    data class Success<T : Any>(val data: T?) : ResultState<T>()
    data class Error<T : Any>(val message: String) : ResultState<T>()
}

suspend fun <T : Any> fetch(call: suspend () -> Response<T>): Flow<ResultState<Response<T>>> =
    flow {
        emit(ResultState.Loading())
        val result = call.invoke()
        if (result.isSuccessful) {
            emit(ResultState.Success(result))
        } else {
            emit(ResultState.Error(result.errorBody()?.string().toString()))
            result.errorBody()?.close()
        }
    }.catch { error ->
        emit(ResultState.Error(error.message.toString()))
    }.flowOn(Dispatchers.IO)

inline fun <T : Any, U : Any> mapResult(
    resultState: ResultState<out T>,
    mapper: T?.() -> U?
): ResultState<U> {
    return when (resultState) {
        is ResultState.Error -> ResultState.Error(resultState.message)
        is ResultState.Idle -> ResultState.Idle()
        is ResultState.Loading -> ResultState.Loading()
        is ResultState.Success -> {
            val data = resultState.data
            val mapData = mapper.invoke(data)
            ResultState.Success(mapData)
        }
    }
}

fun <T : Any> idle(): MutableStateFlow<ResultState<T>> = run {
    MutableStateFlow(ResultState.Idle())
}

fun <T : Any> ResultState<T>.onFailure(result: (String) -> Unit) {
    if (this is ResultState.Error) {
        result.invoke(this.message)
    }
}

fun <T : Any> ResultState<T>.onSuccess(result: (T?) -> Unit) {
    if (this is ResultState.Success) {
        result.invoke(this.data)
    }
}

fun <T : Any> ResultState<T>.onLoading(result: () -> Unit) {
    if (this is ResultState.Loading) {
        result.invoke()
    }
}

fun <T : Any> ResultState<T>.onIdle(result: () -> Unit) {
    if (this is ResultState.Idle) {
        result.invoke()
    }
}

fun <T : Any> isLoading(result: ResultState<T>): Boolean {
    return result is ResultState.Loading
}

fun <T : Any> isSuccess(result: ResultState<T>): Boolean {
    return result is ResultState.Success
}

fun <T : Any> isFailure(result: ResultState<T>): Boolean {
    return result is ResultState.Error
}