package com.example.zdravlje.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zdravlje.data.model.request.LoginResponse
import com.example.zdravlje.data.model.request.PinRequest
import com.example.zdravlje.data.model.request.UpdateMyProfileRequest
import com.example.zdravlje.data.model.request.chat.SearchContact
import com.example.zdravlje.data.model.request.chat.SearchPermisionRequest
import com.example.zdravlje.data.model.request.chat.SendContactRequest
import com.example.zdravlje.data.model.request.profile.SetPermission
import com.example.zdravlje.data.model.request.profile.SharedProfile
import com.example.zdravlje.data.model.response.chat.Participant
import com.example.zdravlje.data.model.response.chat.SearchPermisionResponse
import com.example.zdravlje.data.model.response.chat.UserContact
import com.example.zdravlje.data.model.response.login.GlobalError
import com.example.zdravlje.data.model.response.profile.AccountSynchronizeResponse
import com.example.zdravlje.data.model.response.profile.UserInfo
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.ProfileRepository
import com.example.zdravlje.services.avc.SampleApplication.Companion.context
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import com.nites.ezdravljeapp.R
import java.io.IOException

data class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    var eMailValue: String? = ""
    var adressValue: String? = ""
    var phoneNumberValue: String? = ""
    var languageValue: String? = ""
    var bloodGroupValue: String? = ""
    var languageTag: Int? = null
    var staticValidUsers: MutableLiveData<List<UserContact>> = MutableLiveData()

    var userInfoLiveData: MutableLiveData<Response<UserInfo>> = MutableLiveData()
    var userInfoLiveDataErrorLiveData: MutableLiveData<Any> = MutableLiveData()
    var updateUserLiveData: MutableLiveData<Response<Any>> = MutableLiveData()
    var updateUserLiveDataErrorLiveData: MutableLiveData<Any> = MutableLiveData()

    var updateAvatarLiveData: MutableLiveData<Response<Any>> = MutableLiveData()
    var updateAvatarErrorLiveData: MutableLiveData<String> = MutableLiveData()

    var setPermissionLiveData: MutableLiveData<Response<Any>> = MutableLiveData()
    var setPermissionErrorLiveData: MutableLiveData<String> = MutableLiveData()

    var fullDeleteLiveData: MutableLiveData<Response<Any>> = MutableLiveData()
    var fullDeleteErrorLiveData: MutableLiveData<Any> = MutableLiveData()

    var loginAsContactLiveData: MutableLiveData<Response<LoginResponse>> = MutableLiveData()
    var loginAsContactErrorLiveData: MutableLiveData<String> = MutableLiveData()

    val searchPermissionErrorLiveData: MutableLiveData<GlobalError> = MutableLiveData()
    val usersLiveData: MutableLiveData<Response<SearchPermisionResponse>> = MutableLiveData()
    val subinsuredLiveData: MutableLiveData<Response<SearchPermisionResponse>> = MutableLiveData()
    val doctorsLiveData: MutableLiveData<Response<SearchPermisionResponse>> = MutableLiveData()
    val pharmacistLiveData: MutableLiveData<Response<SearchPermisionResponse>> = MutableLiveData()

    val searchContactResponseLiveData: MutableLiveData<Response<List<Participant>>> =
        MutableLiveData()

    val sendContactResponseLiveData: MutableLiveData<Response<String>> = MutableLiveData()
    val sendContactResponseErrorLiveData: MutableLiveData<GlobalError> = MutableLiveData()

    val getSynchronizeStatusLiveData: MutableLiveData<List<AccountSynchronizeResponse>> =
        MutableLiveData()

    val isSynchronizeStatusLiveData: MutableLiveData<Any> = MutableLiveData()

    fun getSynchronizeStatus(pinRequest: PinRequest) {
        viewModelScope.launch {
            val response = try {
                profileRepository.getSynchronizeStatus(pinRequest)
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }

            if (response.isSuccessful) {
                getSynchronizeStatusLiveData.value = response.body()
            }
        }
    }

    fun isSynchronizeStatus(pinRequest: PinRequest) {
        viewModelScope.launch {
            val response = try {
                profileRepository.startSynchronizeStatus(pinRequest)
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }

            if (response.isSuccessful) {
                isSynchronizeStatusLiveData.value = response.body()
            }
        }
    }

    //User info
    fun getUserInformation() {
        viewModelScope.launch {
            val response = try {
                profileRepository.getUserInformation()
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }

            if (response.isSuccessful) {
                userInfoLiveData.value = response
            } else {
                userInfoLiveDataErrorLiveData.value = response
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            // Unsubscribe from topic
            dataStoreRepository.logout()
        }
    }

    fun updateMyProfileRequest(updateMyProfileRequest: UpdateMyProfileRequest) {
        viewModelScope.launch {
            val response = try {
                profileRepository.updateMyProfile(updateMyProfileRequest)
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }

            if (response.isSuccessful) {
                updateUserLiveData.value = response
            } else {
                updateUserLiveDataErrorLiveData.value = response
            }
        }
    }

    fun fullDeletePatient() {
        viewModelScope.launch {
            val response = try {
                profileRepository.fullDeletePatient()
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }
            if (response.isSuccessful) {
                fullDeleteLiveData.value = response
            } else {
                fullDeleteErrorLiveData.value = response
            }
        }
    }

    fun updateAvatarProfile(pin: String, avatar: MultipartBody.Part) {
        viewModelScope.launch {
            try {
                val response = profileRepository.updateAvatarProfile(avatar, pin)
                if (response.isSuccessful) {
                    updateAvatarLiveData.postValue(response)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = if (!errorBody.isNullOrEmpty()) {
                        try {
                            val jsonObject = JSONObject(errorBody)
                            jsonObject.getString("message")
                        } catch (e: Exception) {
                            "${context?.getString(R.string.unknown_error)}"
                        }
                    } else {
                        "${context?.getString(R.string.unknown_error)}"
                    }
                    val errorMessage = "${context?.getString(R.string.error)}: ${errorMsg}"
                    updateAvatarErrorLiveData.postValue(errorMessage)
                }
            } catch (e: Exception) {
                updateAvatarErrorLiveData.postValue("Exception: ${e.message}")
            }
        }
    }


    fun searchPermission(vararg searchPermissionRequests: SearchPermisionRequest) {
        viewModelScope.launch {
            searchPermissionRequests.forEach { searchPermissionRequest ->
                val response = try {
                    profileRepository.searchPermission(searchPermissionRequest)
                } catch (e: IOException) {
                    Log.e("ChatViewModel", "IOException: ${e.message}")
                    return@forEach
                } catch (e: HttpException) {
                    Log.e("ChatViewModel", "HttpException: ${e.message}")
                    return@forEach
                }

                if (response.isSuccessful) {
                    when (searchPermissionRequest.type) {
                        SearchPermisionRequest.TYPE_SUBINSURED -> subinsuredLiveData.postValue(
                            response
                        )

                        SearchPermisionRequest.TYPE_USER -> usersLiveData.postValue(response)
                        SearchPermisionRequest.TYPE_DOCTOR -> doctorsLiveData.postValue(response)
                        SearchPermisionRequest.TYPE_PHARMACIST -> pharmacistLiveData.postValue(
                            response
                        )

                        else -> Log.e(
                            "ProfileViewModel",
                            "Unknown type: ${searchPermissionRequest.type}"
                        )
                    }
                } else {
                    val errorMessage = response?.errorBody()?.string() ?: "Unknown error"
                    Log.e("ProfileViewModel", "Error: $errorMessage")
                    val globalError = GlobalError(message = errorMessage)
                    searchPermissionErrorLiveData.postValue(globalError)
                }
            }
        }
    }

    fun setPermission(setPermision: SetPermission) {
        viewModelScope.launch {
            try {
                val response = profileRepository.setPermission(setPermision)

                if (response.isSuccessful) {
                    setPermissionLiveData.postValue(response)
                }
                if (!response.isSuccessful) {
                    val errorJson = response.errorBody()?.string()
                    val errorMessage = try {
                        val jsonObject = JSONObject(errorJson)
                        jsonObject.getString("message")
                    } catch (e: Exception) {
                        "Greška: ${response.code()} - ${response.message()}"
                    }
                    setPermissionErrorLiveData.postValue(errorMessage)
                }
            } catch (e: Exception) {
                setPermissionErrorLiveData.postValue("Exception: ${e.localizedMessage}")
            }
        }
    }


    private val _closeBottomSheetEvent = MutableLiveData<Boolean>()
    val closeBottomSheetEvent: LiveData<Boolean> = _closeBottomSheetEvent

    fun loginAsContact(sharedProfile: SharedProfile) {
        viewModelScope.launch {
            try {
                val response = profileRepository.loginAsContact(sharedProfile)

                if (response.isSuccessful) {
                    loginAsContactLiveData.postValue(response)
                    _closeBottomSheetEvent.postValue(true)
                } else {
                    loginAsContactErrorLiveData.postValue("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                loginAsContactErrorLiveData.postValue("Exception: ${e.message}")
            }
        }
    }


    fun searchContact(searchContact: SearchContact) {
        viewModelScope.launch {
            val response = try {
                profileRepository.searchContacts(searchContact)
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }
            if (response.isSuccessful) {
                searchContactResponseLiveData.value = response
            }
        }
    }

    fun sendContactRequest(sendContactRequest: SendContactRequest) {
        viewModelScope.launch {
            val response = try {
                profileRepository.sendContactRequest(sendContactRequest)
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }

            if (response.isSuccessful) {
                sendContactResponseLiveData.value = response
            } else {
                response.errorBody()?.string()?.let {
                    val gson = Gson()
                    val globalErrorResponse: GlobalError =
                        gson.fromJson(it, GlobalError::class.java)
                    sendContactResponseErrorLiveData.value = globalErrorResponse
                }
            }

        }
    }
}

