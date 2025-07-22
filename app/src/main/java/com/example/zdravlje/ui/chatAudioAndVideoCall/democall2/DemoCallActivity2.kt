package com.example.zdravlje.ui.chatAudioAndVideoCall.democall2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import com.example.zdravlje.ui.base.BaseActivity
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.ActivityDemoCall2Binding


class DemoCallActivity2 : BaseActivity() {

    private lateinit var binding: ActivityDemoCall2Binding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding = ActivityDemoCall2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            // Preuzmite podatke iz Intent-a
            val url = intent.getStringExtra(EXTRA_URL) ?: ""
            val token = intent.getStringExtra(EXTRA_TOKEN) ?: ""
            val isVideo = intent.getBooleanExtra(EXTRA_IS_VIDEO, false)
            val chatId = intent.getStringExtra(EXTRA_CHAT_ID) ?: ""

            // Kreirajte instance fragmenata sa argumentima
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.chat_call_fragment_container,
                    ChatInCallFragment.newInstance(chatId)
                )
                .commit()

            supportFragmentManager.beginTransaction()
                .replace(R.id.call_fragment_container,
                    AudioVideoCallFragment.newInstance(url, token, isVideo, chatId))
                .commit()
        }
    }


     fun resizeFrameToBig() {
        val marginInPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            0f,
            resources.displayMetrics
        ).toInt()

        binding.callFragmentContainer.updateLayoutParams<ConstraintLayout.LayoutParams> {
            width = ConstraintLayout.LayoutParams.MATCH_PARENT
            height = ConstraintLayout.LayoutParams.MATCH_PARENT
            setMargins(marginInPx, marginInPx, marginInPx, marginInPx)
        }
    }

    fun hideVideo() {
        binding.callFragmentContainer.visibility=View.INVISIBLE
    }

    fun showVideo() {
        binding.callFragmentContainer.visibility=View.VISIBLE
    }


    companion object {
        const val KEY_ARGS = "args"

        private const val EXTRA_URL = "url"
        private const val EXTRA_TOKEN = "token"
        private const val EXTRA_IS_VIDEO = "isVideo"
        private const val EXTRA_CHAT_ID = "chatId"

        fun newIntent(
            context: Context,
            url: String,
            token: String,
            isVideo: Boolean,
            chatId: String,
        ): Intent {
            return Intent(context, DemoCallActivity2::class.java).apply {
                putExtra(EXTRA_URL, url)
                putExtra(EXTRA_TOKEN, token)
                putExtra(EXTRA_IS_VIDEO, isVideo)
                putExtra(EXTRA_CHAT_ID, chatId)
            }
        }

    }
}