package com.example.zdravlje.ui.profile

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.zdravlje.ui.base.BaseActivity
import com.nites.ezdravljeapp.R

class ProfileActivity : BaseActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        this.window.navigationBarColor= ContextCompat.getColor(this,R.color.gray_E6E6E6)

    }

     fun startNewFragmentFromProfile(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            .replace(R.id.profile_fragment_container_view, fragment)
            .addToBackStack(null)
            .commit()
    }
}