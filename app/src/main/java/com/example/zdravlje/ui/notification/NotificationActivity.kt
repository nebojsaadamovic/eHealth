package com.example.zdravlje.ui.notification

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.zdravlje.ui.base.BaseActivity
import com.nites.ezdravljeapp.R

class NotificationActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        this.window.navigationBarColor= ContextCompat.getColor(this,R.color.white_bg)
        this.window.statusBarColor = ContextCompat.getColor(this, R.color.white_bg)
    }

    fun startNewFragmentFromNotification(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            .replace(R.id.notification_fragment_container_view, fragment)
            .addToBackStack(null)
            .commit()
    }
}