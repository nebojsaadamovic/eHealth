package com.example.zdravlje.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zdravlje.data.api.RetrofitInstance
import com.example.zdravlje.data.model.request.AnonimusLoginRequest
import com.example.zdravlje.data.model.request.EidTokenRequest
import com.example.zdravlje.data.model.request.FirebaseModel
import com.example.zdravlje.data.model.request.LoginRequest
import com.example.zdravlje.data.model.request.LoginResponse
import com.example.zdravlje.data.model.request.PinRequest
import com.example.zdravlje.data.model.request.RfzoAuthRequest
import com.example.zdravlje.data.model.request.UserDeviceInfo
import com.example.zdravlje.data.model.request.calendar.ScreeningProgramsRequest
import com.example.zdravlje.data.model.request.login.TicketRequest
import com.example.zdravlje.data.model.response.login.EidTokenResponse
import com.example.zdravlje.data.model.response.login.GlobalError
import com.example.zdravlje.data.model.response.login.LoginErrorResponse
import com.example.zdravlje.data.model.response.login.TicketResponse
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.LoginRepository
import com.example.zdravlje.ui.base.SingleLiveEvent
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class LoginViewModel(
    private val loginRepository: LoginRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val TAG = LoginViewModel::class.java.simpleName

    val notificationLiveData: MutableLiveData<Any> = MutableLiveData()

    //Login
    private val _loginResponseLiveData = SingleLiveEvent<Response<LoginResponse>>()
    val loginResponseLiveData: LiveData<Response<LoginResponse>> = _loginResponseLiveData

    private val _loginRfzoResponseLiveData = SingleLiveEvent<Response<EidTokenResponse>>()
    val loginRfzoResponseLiveData: LiveData<Response<EidTokenResponse>> = _loginRfzoResponseLiveData

    val loginResponseErrorLiveData: MutableLiveData<LoginErrorResponse> = MutableLiveData()
    val loginResponseUnauthorisedLiveData: MutableLiveData<GlobalError> = MutableLiveData()

    //Generate6MonthToken
    val generate6MonthTokenResponseLiveData: MutableLiveData<String> = MutableLiveData()

    val eidTokenLiveData: MutableLiveData<Response<EidTokenResponse>> = MutableLiveData()

    val checkTokenLiveData: MutableLiveData<Boolean> = MutableLiveData()

    private val _checkPinLiveData = MutableLiveData<Boolean>()
    val checkPinLiveData: LiveData<Boolean> = _checkPinLiveData

    val anonimusLoginLiveData: MutableLiveData<String> = MutableLiveData()

    private val _errorLiveData = SingleLiveEvent<Boolean>()
    val errorLiveData: LiveData<Boolean> = _errorLiveData

    val getTicketLiveData: MutableLiveData<Response<TicketResponse>> =
        MutableLiveData()
    val getTicketErrorLiveData: MutableLiveData<Response<TicketResponse>> =
        MutableLiveData()

    private var password = ""
    private var username = ""

    fun login(loginData: LoginRequest) {
        viewModelScope.launch {
            val response = try {
                loginRepository.login(loginData)
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }
            password = loginData.password.toString()
            username = loginData.username.toString()
            if (response.isSuccessful) {
                _loginResponseLiveData.postValue(response)
                RetrofitInstance.AUTH_TOKEN = response.body()?.token!!
                // saveUserInformation(response.body()!!)
                Log.d(TAG, "Succesfull login")
            } else {
                if (response.code() == 422) {
                    response.errorBody()?.string()?.let {
                        val gson = Gson()
                        val loginErrorResponse: LoginErrorResponse =
                            gson.fromJson(it, LoginErrorResponse::class.java)
                        loginResponseErrorLiveData.value = loginErrorResponse
                    }
                } else if (response.code() == 401) {
                    response.errorBody()?.string()?.let {
                        val gson = Gson()
                        val loginErrorResponse: GlobalError =
                            gson.fromJson(it, GlobalError::class.java)
                        loginResponseUnauthorisedLiveData.value = loginErrorResponse
                    }
                } else {
                    Log.i("LoginError", response.code().toString())
                }

            }
        }
    }

    fun rfzoLogin(loginData: RfzoAuthRequest) {
        viewModelScope.launch {
            val response = try {
                loginRepository.loginRfzo(loginData)
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }
            if (response.isSuccessful) {
                _loginRfzoResponseLiveData.postValue(response)
                RetrofitInstance.AUTH_TOKEN = response.body()?.data!!
                Log.i("ssssassa", RetrofitInstance.AUTH_TOKEN)
                Log.d(TAG, "Succesfull login")
            } else {
                if (response.code() == 422) {
                    response.errorBody()?.string()?.let {
                        val gson = Gson()
                        val loginErrorResponse: LoginErrorResponse =
                            gson.fromJson(it, LoginErrorResponse::class.java)
                        loginResponseErrorLiveData.value = loginErrorResponse
                    }
                } else if (response.code() == 401) {
                    response.errorBody()?.string()?.let {
                        val gson = Gson()
                        val loginErrorResponse: GlobalError =
                            gson.fromJson(it, GlobalError::class.java)
                        loginResponseUnauthorisedLiveData.value = loginErrorResponse
                    }
                } else {
                    Log.i("LoginError", response.code().toString())
                }

            }
        }
    }

    fun saveUserInformation(loginResponse: LoginResponse) {
        viewModelScope.launch {
            dataStoreRepository.saveUserLoginResponse(loginResponse)
        }
    }

    fun generate6MonthToken() {
        viewModelScope.launch {
            val response = try {
                loginRepository.generate6MonthToken()
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }

            if (response.isSuccessful) {
                generate6MonthTokenResponseLiveData.value = response.body()

            } else {
                if (response.code() == 422) {

                } else if (response.code() == 401) {
                    _errorLiveData.postValue(true)
                }
            }
        }
    }

    fun getEidToken(eidRequest: EidTokenRequest) {
        viewModelScope.launch {
            val response = try {
                loginRepository.getEidToken(eidRequest)
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }

            if (response.isSuccessful) {
                eidTokenLiveData.value = response
            }
        }
    }

    fun checkToken(deviceInfo: UserDeviceInfo) {
        viewModelScope.launch {
            val response = try {
                loginRepository.checkToken(deviceInfo)
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }

            checkTokenLiveData.value = response.isSuccessful
        }
    }

    fun checkPin(pinRequest: PinRequest) {
        viewModelScope.launch {
            println("checkPin called with pin: ${pinRequest.pin}")
            val response = try {
                loginRepository.checkPin(pinRequest)
            } catch (e: IOException) {
                println("IOException: ${e.message}")
                _checkPinLiveData.postValue(false)
                return@launch
            } catch (e: HttpException) {
                println("HttpException: ${e.message}")
                _checkPinLiveData.postValue(false)
                return@launch
            }
            println("Response isSuccessful: ${response.isSuccessful}")
            _checkPinLiveData.postValue(response.isSuccessful)
        }
    }



    fun anonimusLogin(anonimusLoginRequest: AnonimusLoginRequest) {
        viewModelScope.launch {
            val response = try {
                loginRepository.anonimusLogin(anonimusLoginRequest)
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }

            anonimusLoginLiveData.value = response.body()
        }
    }

    fun startFirebase(firebaseModel: FirebaseModel) {
        viewModelScope.launch {
            val response = try {
                loginRepository.startFirebase(firebaseModel)
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }

            notificationLiveData.value = response
        }
    }

    fun createTicket(ticketRequest: TicketRequest,  filePart: MultipartBody.Part?) {
        viewModelScope.launch {
            val response = try {
                loginRepository.createTicket(ticketRequest, filePart)
            } catch (e: IOException) {
                return@launch
            } catch (e: HttpException) {
                return@launch
            }
            if (response.isSuccessful) {
                getTicketLiveData.value = response
            } else {
                getTicketErrorLiveData.value = response
            }
        }
    }



}