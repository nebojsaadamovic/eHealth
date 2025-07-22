package com.example.zdravlje.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zdravlje.data.model.request.chat.AcceptContactRequest
import com.example.zdravlje.data.model.request.chat.RemoveContactRequest
import com.example.zdravlje.data.model.request.chat.SearchContact
import com.example.zdravlje.data.model.request.chat.SendContactListRequest
import com.example.zdravlje.data.model.request.chat.SendContactRequest
import com.example.zdravlje.data.model.response.chat.Participant
import com.example.zdravlje.data.model.response.login.GlobalError
import com.example.zdravlje.data.repository.ChatRepository
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.ui.base.SingleLiveEvent
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

data class ChatViewModel(
    private val chatRepository: ChatRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {


    private val _searchContactResponseLiveData = SingleLiveEvent<Response<List<Participant>>>()
    val searchContactResponseLiveData: LiveData<Response<List<Participant>>> = _searchContactResponseLiveData

    val sendContactResponseLiveData: MutableLiveData<Response<String>> = MutableLiveData()
    val sendContactResponseErrorLiveData: MutableLiveData<GlobalError> = MutableLiveData()

    val removeContactResponseLiveData: MutableLiveData<Response<Any>> = MutableLiveData()
    val removeContactResponseErrorLiveData: MutableLiveData<Any> = MutableLiveData()

    val cancelFriendRequestLiveData: MutableLiveData<Response<String>> = MutableLiveData()
    val cancelFriendRequestErrorLiveData: MutableLiveData<Any> = MutableLiveData()

    val denyContactRequestErrorLiveData: MutableLiveData<Any> = MutableLiveData()


    private val _denyContactRequestLiveData = SingleLiveEvent<Response<String>>()
    val denyContactRequestLiveData: LiveData<Response<String>> = _denyContactRequestLiveData

    val acceptContactRequestLiveData: MutableLiveData<Response<String>> = MutableLiveData()
    val acceptContactRequestErrorLiveData: MutableLiveData<Any> = MutableLiveData()

    fun searchContact(searchContact: SearchContact) {
        viewModelScope.launch {
            val response = try {
                chatRepository.searchContacts(searchContact)
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }

            if (response.isSuccessful) {
                _searchContactResponseLiveData.postValue(response)
            }
        }
    }

    fun sendContactRequest(sendContactRequest: SendContactRequest) {
        viewModelScope.launch {
            val response = try {
                chatRepository.sendContactRequest(sendContactRequest)
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

    fun sendContactListRequest(sendContactListRequest: SendContactListRequest) {
        viewModelScope.launch {
            val response = try {
                chatRepository.sendContactListRequest(sendContactListRequest)
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

    fun removeContact(removeContactRequest: RemoveContactRequest) {
        viewModelScope.launch {
            val response = try {
                chatRepository.removeContact(removeContactRequest)
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }

            if (response.isSuccessful) {
                removeContactResponseLiveData.value = response
            } else {
                removeContactResponseErrorLiveData.value = response
            }
        }
    }

    fun cancelFriendRequest(removeContactRequest: RemoveContactRequest) {
        viewModelScope.launch {
            val response = try {
                chatRepository.cancelFriendRequest(removeContactRequest)
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }
            if (response.isSuccessful) {
                cancelFriendRequestLiveData.postValue(response)
            } else {
                cancelFriendRequestErrorLiveData.value = response
            }
        }
    }

    fun denyContactRequest(removeContactRequest: RemoveContactRequest) {
        viewModelScope.launch {
            val response = try {
                chatRepository.denyContactRequest(removeContactRequest)
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }

            if (response.isSuccessful) {
                _denyContactRequestLiveData.postValue(response)
            } else {
                denyContactRequestErrorLiveData.postValue(response)
            }
        }
    }

    fun acceptContactRequest(acceptContactRequest: AcceptContactRequest) {
        viewModelScope.launch {
            val response = try {
                chatRepository.acceptContactRequest(acceptContactRequest)
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }

            if (response.isSuccessful) {
                acceptContactRequestLiveData.postValue(response)
            } else {
                acceptContactRequestErrorLiveData.postValue(response)
            }
        }
    }
}