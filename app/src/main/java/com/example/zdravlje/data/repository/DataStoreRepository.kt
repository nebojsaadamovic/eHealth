package com.example.zdravlje.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.zdravlje.data.model.request.LoginResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private const val DATASTORE_NAME = "user_prefs"
private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

class DataStoreRepository(context: Context) {

    private val dataStore = context.dataStore


    private object PreferencesKeys {

        val authToken = stringPreferencesKey("auth_token")
        val authType = intPreferencesKey("auth_type")
        val authMessage = stringPreferencesKey("auth_message")
        val data = stringPreferencesKey("data")
        val pin = stringPreferencesKey("pin")
        val avatar = stringPreferencesKey("avatar")


        val expire = stringPreferencesKey("expire")
        val refreshToken = stringPreferencesKey("refresh_token")
        val refreshTokenExpireAt = stringPreferencesKey("refresh_token_expire_at")
        val userId = intPreferencesKey("user_id")
        val username = stringPreferencesKey("username")

        val currencyName = stringPreferencesKey("currency_name")
        val currencyCode = stringPreferencesKey("currency_code")
        val currencyApiCode = stringPreferencesKey("currency_api_code")

        val usernameEmail = stringPreferencesKey("username_email")
        val password = stringPreferencesKey("password")
        val biometricsEnabled = booleanPreferencesKey("biometrics_enabled")

        val isOnBoardingPass = booleanPreferencesKey("is_on_boarding_pass")


    }

//    private val dataStore: DataStore<Preferences> =
//
//        context.datastore(name = Constants.PREFERENCES_NAME)
//



    suspend fun saveCredentials(username: String, password: String) {
        dataStore.edit { preference ->
            preference[PreferencesKeys.usernameEmail] = username
            preference[PreferencesKeys.password] = password
        }
    }

    suspend fun saveUserLoginResponse(loginResponse: LoginResponse) {
        dataStore.edit { preference ->
            preference[PreferencesKeys.authToken] = loginResponse.token.toString()
            preference[PreferencesKeys.authType] = loginResponse.type!!.toInt()
            preference[PreferencesKeys.data] = loginResponse.data.toString()
            preference[PreferencesKeys.authMessage] = loginResponse.message.toString()
        }
    }

    suspend fun saveOnBoardingPass(isOnBoardingPass: Boolean) {
        dataStore.edit {
            it[PreferencesKeys.isOnBoardingPass] = isOnBoardingPass
        }
    }

    suspend fun savePin(pin: String) {
        dataStore.edit {
            it[PreferencesKeys.pin] = pin
        }
    }

    suspend fun deletePin() {
        dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.pin)
        }
    }

    suspend fun saveAvatar(avatar: String) {
        dataStore.edit {
            it[PreferencesKeys.avatar] = avatar
        }
    }

    suspend fun logout() {
        dataStore.edit {
            it.remove(PreferencesKeys.authToken)
            it.remove(PreferencesKeys.data)
            it.remove(PreferencesKeys.pin)
        }
    }

    var pin : Flow<String> =
        dataStore.data
            .map { preference ->
                preference[PreferencesKeys.pin] ?: ""
            }

    var avatar : Flow<String> =
        dataStore.data
            .map { preference ->
                preference[PreferencesKeys.avatar] ?: ""
            }

    val userLoginResponse: Flow<LoginResponse> =
        dataStore.data
            .map { preference ->
                LoginResponse(
                    preference[PreferencesKeys.authMessage] ?: "none",
                    preference[PreferencesKeys.authToken] ?: "none",
                    preference[PreferencesKeys.data] ?: "none",
                    preference[PreferencesKeys.authType] ?: 0,
                )
            }

    val isOnBoardingPass: Flow<Boolean> =
        dataStore.data
            .map { preference ->
                preference[PreferencesKeys.isOnBoardingPass] ?: false
            }
}