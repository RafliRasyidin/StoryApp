package com.rasyidin.storyapp.data.source.local

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(private val sessionManager: SessionManager) {

    suspend fun saveToken(token: String) {
        sessionManager.saveToken(token)
    }

    suspend fun setLoginState(state: Boolean) {
        sessionManager.setLoginState(state)
    }

    suspend fun setOnBoardingState(state: Boolean) {
        sessionManager.setOnBoardingState(state)
    }

    suspend fun removeToken() {
        sessionManager.removeToken()
    }

    fun getToken(): Flow<String> {
        return sessionManager.getToken()
    }

    fun getLoginState(): Flow<Boolean> {
        return sessionManager.getLoginState()
    }

    fun getOnBoardingState(): Flow<Boolean> {
        return sessionManager.getOnBoardingState()
    }


}