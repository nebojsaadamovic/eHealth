package com.example.zdravlje.ui.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.zdravlje.data.api.RetrofitInstance
import com.example.zdravlje.data.model.request.UserDeviceInfo
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.LoginRepository
import com.example.zdravlje.ui.base.BaseActivity
import com.example.zdravlje.ui.home.HomeActivity
import com.example.zdravlje.ui.login.LoginActivity
import com.example.zdravlje.ui.login.LoginViewModel
import com.example.zdravlje.ui.login.LoginViewModelFactory
import com.example.zdravlje.ui.onBoarding.OnBoardingActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.nites.ezdravljeapp.R

class SplashActivity : BaseActivity() {

    private val TAG = SplashActivity::class.java.simpleName

    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var loginViewModel: LoginViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        this.window.navigationBarColor = ContextCompat.getColor(this, R.color.white)

        setViewModels()

        val res = dataStoreRepository.userLoginResponse
        val userData = dataStoreRepository.userLoginResponse

        CoroutineScope(Dispatchers.Main).launch {
            userData.collect {
                if (it.token!!.isEmpty()) {
                    Log.i("ttd1", "none")
                } else {
                    Log.i("ttd1", it.token.toString())
                }
            }
        }

        lifecycleScope.launch {
            val isOnBoardingPass = dataStoreRepository.isOnBoardingPass.first()
            if (isOnBoardingPass) {
                if (isTokenSet()){
                    RetrofitInstance.AUTH_TOKEN = getStoredToken()
                    loginViewModel.checkToken(UserDeviceInfo("sr", getStoredFcmToken(), 2))
                }else{
                    startActivity(
                        Intent(this@SplashActivity, LoginActivity::class.java)
                    )
                    finish()
                }
            } else {
                // Navigacija ka OnBoardingActivity
                startActivity(Intent(this@SplashActivity, OnBoardingActivity::class.java))
                finish()
            }

        }

    }

    private fun setViewModels() {
        val loginRepository = LoginRepository()

        dataStoreRepository = DataStoreRepository(this)

        val loginViewModelFactory = LoginViewModelFactory(loginRepository, dataStoreRepository)

        loginViewModel = ViewModelProvider(
            this,
            loginViewModelFactory
        ).get(LoginViewModel::class.java)

        checkToken()
    }

    private fun checkToken() {
        loginViewModel.checkTokenLiveData.observe(this) { response ->
            if (response) {
                startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
                finish()
            } else {
                clearPin()
                startActivity(
                    Intent(this@SplashActivity, LoginActivity::class.java)
                )
                finish()
            }
        }
    }
}