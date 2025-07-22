package com.example.zdravlje.ui.chat.options

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.zdravlje.ui.base.BaseActivity
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.ActivityChatOptionsBinding

class ChatOptionsActivity : BaseActivity() {

    lateinit var binding: ActivityChatOptionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.window.navigationBarColor = ContextCompat.getColor(this, R.color.white_bg)
        this.window.statusBarColor = ContextCompat.getColor(this, R.color.white_bg)
    }

    fun startNewFragmentFromChatOptions(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            .replace(R.id.chatOptionFcv, fragment)
            .addToBackStack(null)
            .commit()
    }
}