package com.example.zdravlje.ui.profile.sharedProfile

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.zdravlje.ui.base.BaseActivity
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.ActivitySharedProfileBinding

class SharedProfileActivity : BaseActivity() {

    lateinit var binding: ActivitySharedProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySharedProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.window.navigationBarColor= ContextCompat.getColor(this,R.color.gray_E6E6E6)
    }

    fun startNewFragmentFromShareProfile(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            .replace(R.id.shared_profile_fragment_container_view, fragment)
            .addToBackStack(null)
            .commit()
    }
}