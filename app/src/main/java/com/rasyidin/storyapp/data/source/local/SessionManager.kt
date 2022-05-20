package com.rasyidin.storyapp.data.source.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rasyidin.storyapp.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SessionManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = context.getString(
            R.string.SESSION_MANAGER_PREF_NAME
        )
    )

    private val tokenPrefKey = stringPreferencesKey(context.getString(R.string.TOKEN_PREF_KEY))
    private val loginStatePrefKey =
        booleanPreferencesKey(context.getString(R.string.LOGIN_STATE_KEY))
    private val onBoardingStatePrefKey =
        booleanPreferencesKey(context.getString(R.string.ON_BOARDING_STATE_KEY))

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preference ->
            preference[tokenPrefKey] = token
        }
    }

    suspend fun removeToken() {
        context.dataStore.edit { preference ->
            preference[tokenPrefKey] = ""
        }
    }

    suspend fun setLoginState(state: Boolean) {
        context.dataStore.edit { preference ->
            preference[loginStatePrefKey] = state
        }
    }

    suspend fun setOnBoardingState(state: Boolean) {
        context.dataStore.edit { preference ->
            preference[onBoardingStatePrefKey] = state
        }
    }

    fun getToken() = context.dataStore.data.map { preferences ->
        preferences[tokenPrefKey] ?: ""
    }

    fun getLoginState() = context.dataStore.data.map { preferences ->
        preferences[loginStatePrefKey] ?: false
    }

    fun getOnBoardingState() = context.dataStore.data.map { preferences ->
        preferences[onBoardingStatePrefKey] ?: false
    }
}