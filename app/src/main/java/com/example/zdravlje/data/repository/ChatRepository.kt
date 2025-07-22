package com.example.zdravlje.data.repository

import com.example.zdravlje.data.api.RetrofitInstance
import com.example.zdravlje.data.model.request.chat.AcceptContactRequest
import com.example.zdravlje.data.model.request.chat.RemoveContactRequest
import com.example.zdravlje.data.model.request.chat.SearchContact
import com.example.zdravlje.data.model.request.chat.SendContactListRequest
import com.example.zdravlje.data.model.request.chat.SendContactRequest
import com.example.zdravlje.data.model.response.chat.Participant
import retrofit2.Response

class ChatRepository {

    suspend fun searchContacts( searchModel: SearchContact): Response<List<Participant>> {
        return RetrofitInstance.appApi.searchContacts(searchModel)
    }

    suspend fun sendContactRequest( sendContactRequest: SendContactRequest): Response<String> {
        return RetrofitInstance.appApi.sendContactRequest(sendContactRequest)
    }

    suspend fun sendContactListRequest( sendContactListRequest: SendContactListRequest): Response<String> {
        return RetrofitInstance.appApi.sendContactListRequest(sendContactListRequest)
    }

    suspend fun removeContact(removeContactRequest: RemoveContactRequest): Response<Any> {
        return RetrofitInstance.appApi.removeContact(removeContactRequest)
    }

    suspend fun cancelFriendRequest(removeContactRequest: RemoveContactRequest): Response<String> {
        return RetrofitInstance.appApi.cancelFriendRequest(removeContactRequest)
    }

    suspend fun denyContactRequest(removeContactRequest: RemoveContactRequest): Response<String> {
        return RetrofitInstance.appApi.denyContactRequest(removeContactRequest)
    }

    suspend fun acceptContactRequest(acceptContactRequest: AcceptContactRequest): Response<String> {
        return RetrofitInstance.appApi.acceptContactRequest(acceptContactRequest)
    }
}