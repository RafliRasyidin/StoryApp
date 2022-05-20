package com.rasyidin.storyapp.data.repository

import com.rasyidin.storyapp.data.model.Login
import com.rasyidin.storyapp.data.source.local.LocalDataSource
import com.rasyidin.storyapp.data.source.remote.network.AuthService
import com.rasyidin.storyapp.data.source.remote.response.ApiResponse
import com.rasyidin.storyapp.data.utils.ResultState
import com.rasyidin.storyapp.data.utils.fetch
import com.rasyidin.storyapp.data.utils.mapResult
import com.rasyidin.storyapp.data.utils.toLogin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImp @Inject constructor(
    private val service: AuthService,
    private val local: LocalDataSource
) : AuthRepository {

    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): Flow<ResultState<ApiResponse>> {
        return fetch {
            service.register(name, email, password)
        }.map { resultState ->
            mapResult(resultState) {
                this?.body()
            }
        }
    }

    override suspend fun login(email: String, password: String): Flow<ResultState<Login>> {
        return fetch {
            service.login(email, password)
        }.map { resultState ->
            mapResult(resultState) {
                this?.body()?.loginResult?.toLogin()
            }
        }
    }

    override suspend fun saveToken(token: String) {
        local.saveToken(token)
    }

    override suspend fun setLoginState(state: Boolean) {
        local.setLoginState(state)
    }

    override suspend fun setOnBoardingState(state: Boolean) {
        local.setOnBoardingState(state)
    }

    override suspend fun removeToken() {
        local.removeToken()
    }

    override fun getToken(): Flow<String> {
        return local.getToken()
    }

    override fun getLoginState(): Flow<Boolean> {
        return local.getLoginState()
    }

    override fun getOnBoardingState(): Flow<Boolean> {
        return local.getOnBoardingState()
    }
}