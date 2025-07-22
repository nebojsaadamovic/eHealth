package com.example.zdravlje.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.zdravlje.data.model.request.FirebaseModel
import com.example.zdravlje.data.model.request.LoginRequest
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.LoginRepository
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.login.qrCode.QrCodeActivity
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentLoginEmailPasswordBinding

class LoginEmailPasswordFragment : BaseFragment() {

    lateinit var binding: FragmentLoginEmailPasswordBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var dataStoreRepository: DataStoreRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginEmailPasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.navigationBarColor =
            ContextCompat.getColor(requireContext(), R.color.white)
        setViewModels()
        subscribeToObservers()
        setOnLoginClickListener()
        setOnTextChangesListener()
        startQrCodeActivity()

        binding.mbClose.setOnClickListener(){
            requireActivity().onBackPressed()
        }

        binding.ivLogo.setOnClickListener(View.OnClickListener {
            val token = getSavedFcmToken(requireActivity())!!
            if (token != null) {
                Log.i("sdasdasdsd",token)
            }
            loginViewModel.startFirebase(FirebaseModel(token,7))
        })

    }

    private fun setViewModels() {
        val loginRepository = LoginRepository()
        dataStoreRepository = DataStoreRepository(requireActivity())
        val loginViewModelFactory = LoginViewModelFactory(loginRepository, dataStoreRepository)

        loginViewModel = ViewModelProvider(
            requireActivity(),
            loginViewModelFactory
        ).get(LoginViewModel::class.java)
    }

    private fun subscribeToObservers() {
        loginViewModel.loginResponseLiveData.observe(requireActivity()) { response ->
            if (response.isSuccessful) {
                binding.progressBar.visibility = View.GONE
                // startActivity(Intent(requireActivity(), HomeActivity::class.java))
                saveToken(response.body()?.token!!)
                val pinFragment = PinFragment()
                requireFragmentManager().beginTransaction().apply {
                    replace(R.id.home_fragment_container_view, pinFragment)
                    addToBackStack(null)
                    commit()
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.error), Toast.LENGTH_SHORT)
                    .show()
            }
        }

        loginViewModel.loginResponseErrorLiveData.observe(viewLifecycleOwner) { response ->
            if (isAdded && response.isNotEmpty()) { // isAdded = fragment is attached
                binding.progressBar.visibility = View.GONE
                for (errorItem in response) {
                    when (errorItem.field) {
                        "username" -> binding.errorUserName.text =
                            getString(R.string.fragment_login_email_password_username_empty)
                        "password" -> binding.errorPassword.text =
                            getString(R.string.fragment_login_email_password_password_empty)
                    }
                }
            }
        }


        loginViewModel.loginResponseUnauthorisedLiveData.observe(requireActivity()) { response ->
            binding.progressBar.visibility = View.GONE
            Toast.makeText(
                requireContext(),
                getString(R.string.fragment_login_email_password_invalid_credentials),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun setOnLoginClickListener() {
        binding.mbLogin.setOnClickListener(View.OnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            val userName = binding.ietUserName.text.toString().trim()
            val password = binding.ietPasswordLogin.text.toString().trim()
            val loginRequest = LoginRequest(userName, password)
            loginViewModel.login(loginRequest)
        })
    }

    private fun startQrCodeActivity() {
        binding.mbLoginQrCode.setOnClickListener(View.OnClickListener {
            startActivity(Intent(requireActivity(), QrCodeActivity::class.java))
        })
    }

    private fun setOnTextChangesListener() {
        binding.ietUserName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.errorUserName.text = ""
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.ietPasswordLogin.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.errorPassword.text = ""
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    fun getSavedFcmToken(context: Context): String? {
        val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString("fcm_token", null) // Ako nema tokena, vraća `null`
    }
}