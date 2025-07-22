package com.example.zdravlje.ui.base

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.app.democall.composesample.data.call.IncomingCallResponse
import com.app.democall.composesample.data.call.JoinCallResponse
import com.app.democall.composesample.util.observers.network.ConnectivityObserverImpl
import com.app.democall.sdk.manager.api.AvcSdkManager
import com.app.democall.socket.SocketManager
import com.example.zdravlje.data.repository.ChatRepository
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.sharedPref.PreferenceHelper
import com.example.zdravlje.services.avc.SampleApplication
import com.example.zdravlje.services.di.MainViewModel
import com.example.zdravlje.services.di.MainViewModelFactory
import com.example.zdravlje.ui.chat.ChatViewModel
import com.example.zdravlje.ui.chat.ChatViewModelFactory
import com.example.zdravlje.ui.chatAudioAndVideoCall.democall2.DemoCallActivity2
import com.example.zdravlje.ui.dialogs.IncomingCallDialog
import com.example.zdravlje.ui.profile.settings.BottomSheetChooseLanguage
import com.google.firebase.messaging.FirebaseMessaging
import java.util.Locale

open class BaseActivity : AppCompatActivity(), IncomingCallDialog.IncomingCallActions,
    BottomSheetChooseLanguage.OnLanguageSelectedListener {

    private lateinit var chatViewModel: ChatViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var avcSdkManager: AvcSdkManager
    private lateinit var sampleApp: SampleApplication
    private lateinit var incomingCallResp: IncomingCallResponse
    private lateinit var sharedPreferences: SharedPreferences
    var isVideoCall: Boolean = false
    var callId: String = ""

    override fun attachBaseContext(newBase: Context) {
        sharedPreferences = newBase.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val languageCode = sharedPreferences.getString("selected_language", "sr") ?: "sr"
        val locale = Locale.forLanguageTag(languageCode)

        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocales(LocaleList(locale))
        }

        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(this, getSavedLanguage())
        setupViewModels()
    }
    
     fun getSavedLanguage(): String {
        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("selected_language", "sr") ?: "sr"
    }

    private fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            config.setLocales(LocaleList(locale))
        }

        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    fun updateLanguage(languageCode: String) {
        setLocale(this, languageCode)
        getFcmTokenFromFirebase()
        saveLanguage(languageCode)
        recreate()
    }

    private fun saveLanguage(languageCode: String) {
        with(sharedPreferences.edit()) {
            putString("selected_language", languageCode)
            apply()
        }
    }

    override fun onLanguageSelected(language: String, languageTag: Int) {
        updateLanguage(language)
    }

    fun startAVCSdk(
        userToken: String,
        langCode: String,
        firebaseToken: String,
        deviceType: String
    ) {
        Log.i("sdkParameters", "userToken- $userToken")
        Log.i("sdkParameters", "langCode- $langCode")
        Log.i("sdkParameters", "firebaseToken- $firebaseToken")
        Log.i("sdkParameters", "deviceType- $deviceType")

        mainViewModel.startSdk(userToken, langCode, firebaseToken, deviceType)
        startListenEvents(avcSdkManager.container.socketManager)
        mainViewModel.getChatList()
    }

    private fun startListenEvents(socketManager: SocketManager) {
        socketManager.listenEvent(IncomingCallResponse, IncomingCallResponse::class.java) {
            incomingCallResp = it
            isVideoCall = it.isVideo
            callId = it.chatId
            runOnUiThread {
                val dialog = IncomingCallDialog(this, this, it)
                dialog.show()
            }
        }

        socketManager.listenEvent(JoinCallResponse, JoinCallResponse::class.java) {
            val context = this
            val url = it.url
            val token = it.token
            val isVideo = isVideoCall
            val chatId = callId

            val intent = DemoCallActivity2.newIntent(
                context = context,
                url = url,
                token = token,
                isVideo = isVideo,
                chatId = chatId
            )

            startActivity(intent)
        }
    }


    fun setupViewModels() {
        val chatRepository = ChatRepository()
        dataStoreRepository = DataStoreRepository(this)
        val chatViewModelFactory = ChatViewModelFactory(chatRepository, dataStoreRepository)
        chatViewModel = ViewModelProvider(this, chatViewModelFactory)
            .get(ChatViewModel::class.java)

        sampleApp = this.application as SampleApplication
        avcSdkManager = sampleApp.getAVC()

        val mainViewModelFactory = MainViewModelFactory(
            avcSdkManager,
            ConnectivityObserverImpl(this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager)
        )

        mainViewModel = ViewModelProvider(this, mainViewModelFactory).get(MainViewModel::class.java)
    }

    fun getMainViewModel(): MainViewModel {
        return mainViewModel
    }

    fun getChatViewModel(): ChatViewModel {
        return chatViewModel
    }

    fun getDataStoreRepository(): DataStoreRepository {
        return dataStoreRepository
    }

    override fun onAcceptCall() {
        mainViewModel.acceptCall(incomingCallResp.chatId, incomingCallResp.isVideo)
    }

    override fun onDeclineCall() {
        mainViewModel.declineCall(incomingCallResp.chatId)
    }

    fun getStoredPin(): String {
        return this?.let { PreferenceHelper.getPin(it) }?: ""
    }

    fun savePin(pin: String) {
        this?.let { PreferenceHelper.savePin(it, pin) }
    }

    fun clearPin() {
        this?.let { PreferenceHelper.clearPin(it) }
    }

    fun isPinSet(): Boolean {
        val pin = getStoredPin()
        return !pin.isNullOrBlank()
    }

    fun getStoredToken(): String {
        return this?.let { PreferenceHelper.getToken(it) }?: ""
    }

    fun saveToken(pin: String) {
        this?.let { PreferenceHelper.saveToken(it, pin) }
    }

    fun clearToken() {
        this?.let { PreferenceHelper.clearToken(it) }
    }

    fun isTokenSet(): Boolean {
        val token = getStoredToken()
        return !token.isNullOrBlank()
    }

    fun getStoredFcmToken(): String{
        return let { PreferenceHelper.getFcmToken(it) } ?: ""
    }

    fun getFcmTokenFromFirebase() {
        this?.let { ctx ->
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task ->
                    try {
                        val token = task.result
                        PreferenceHelper.saveFcmToken(ctx, token)
                    } catch (e: Exception) {
                        // todo: log or handle error
                    }
                }
        }
    }
}
