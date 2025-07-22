package com.example.zdravlje.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.zdravlje.ui.base.BaseActivity
import com.nites.ezdravljeapp.databinding.ActivityLoginBinding

class LoginActivity : BaseActivity() {

    lateinit var binding: ActivityLoginBinding
    private val TAG = LoginActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}