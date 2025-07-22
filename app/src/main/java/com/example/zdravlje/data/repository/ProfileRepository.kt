package com.example.zdravlje.data.repository

import com.example.zdravlje.data.api.RetrofitInstance
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
import com.example.zdravlje.data.model.response.profile.AccountSynchronizeResponse
import com.example.zdravlje.data.model.response.profile.UserInfo
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class ProfileRepository {

    suspend fun getUserInformation(): Response<UserInfo> {
        return RetrofitInstance.appApi.getUserInformation()
    }

    suspend fun getSynchronizeStatus(pinRequest: PinRequest): Response<List<AccountSynchronizeResponse>> {
        return RetrofitInstance.appApi.getSynchronizeStatus(pinRequest)
    }

    suspend fun startSynchronizeStatus(pinRequest: PinRequest): Response<Any> {
        return RetrofitInstance.appApi.startSynchronizeStatus(pinRequest)
    }

    suspend fun updateMyProfile(updateMyProfileRequest: UpdateMyProfileRequest): Response<Any> {
        return RetrofitInstance.appApi.updateMyProfile(updateMyProfileRequest)
    }

    suspend fun fullDeletePatient(): Response<Any> {
        return RetrofitInstance.appApi.fullDeletePatient()
    }

    suspend fun updateAvatarProfile(avatar: MultipartBody.Part, pin: String): Response<Any> {
        val pinRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), pin)
        return RetrofitInstance.appApi.updateAvatarProfile(pinRequestBody, avatar)
    }

    suspend fun searchPermission(searchPermisionRequest: SearchPermisionRequest): Response<SearchPermisionResponse> {
        return RetrofitInstance.appApi.searchPermission(searchPermisionRequest)
    }

    suspend fun setPermission(setPermision: SetPermission): Response<Any> {
        return RetrofitInstance.appApi.setPermission(setPermision)
    }

    suspend fun loginAsContact(sharedProfile: SharedProfile): Response<LoginResponse> {
        return RetrofitInstance.appApi.loginAsContact(sharedProfile)
    }

    suspend fun searchContacts(searchModel: SearchContact): Response<List<Participant>> {
        return RetrofitInstance.appApi.searchContacts(searchModel)
    }

    suspend fun sendContactRequest(sendContactRequest: SendContactRequest): Response<String> {
        return RetrofitInstance.appApi.sendContactRequest(sendContactRequest)
    }
}






