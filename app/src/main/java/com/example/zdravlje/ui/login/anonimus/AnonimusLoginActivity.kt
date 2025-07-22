package com.example.zdravlje.ui.login.anonimus

import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.example.zdravlje.ui.base.BaseActivity
import com.nites.ezdravljeapp.R

class AnonimusLoginActivity : BaseActivity() {

    private var chatId:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anonimus_login)


        val data: Uri? = intent?.data
        data?.let {
            chatId = it.lastPathSegment.toString()
            val fullLink = it.toString()


            // Obradite podatke iz deep link-a
            Log.d("DeepLink", "Path segment: $chatId")
            Log.d("DeepLink", "Full link: $fullLink")
        }
    }

    public fun getChatId():String{
        return chatId
    }

}