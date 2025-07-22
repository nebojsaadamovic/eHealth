package com.example.zdravlje.ui.initialParameter

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.zdravlje.ui.base.BaseActivity
import com.example.zdravlje.ui.login.LoginActivity
import com.nites.ezdravljeapp.databinding.ActivityInitialParametersBinding

class InitialParametersActivity : BaseActivity() {
    lateinit var binding: ActivityInitialParametersBinding
    private val TAG = InitialParametersActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInitialParametersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
    }

    private fun setupUI() {
//        binding.clNext.setOnClickListener(View.OnClickListener {
//            startActivity(Intent(this@InitialParametersActivity, LoginActivity::class.java))
//            finish()
//        })
    }
}