package com.example.zdravlje.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.zdravlje.data.model.request.RfzoAuthRequest
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.LoginRepository
import com.example.zdravlje.ui.base.BaseFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentLoginRfzoBinding

class LoginRfzoFragment : BaseFragment() {

    lateinit var binding: FragmentLoginRfzoBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var dataStoreRepository: DataStoreRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginRfzoBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setViewModels()
        subscribeToObservers()
        setOnLoginClickListener()
        setOnTextChangesListener()
    }

    private fun setupUI() {
        binding.tilUserName.setStartIconOnClickListener {
            val bottomSheet = LBOBottomSheetFragment()
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)

            binding.tilPassword.setStartIconOnClickListener {
                val bottomSheet = LBOBottomSheetFragment()
                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
            }
        }

        binding.mbLogin.setOnClickListener(View.OnClickListener {
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

    private fun setOnLoginClickListener() {
        binding.mbLogin.setOnClickListener(View.OnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            val userName = binding.ietUserName.text.toString().trim()
            val password = binding.ietPasswordLogin.text.toString().trim()
            val loginRequest = RfzoAuthRequest(userName, password)
            loginViewModel.rfzoLogin(loginRequest)
        })
    }

    private fun subscribeToObservers() {
        loginViewModel.loginRfzoResponseLiveData.observe(requireActivity()) { response ->
            if (response.isSuccessful) {
                saveToken(response.body()!!.data!!)
                binding.progressBar.visibility = View.GONE
                val pinFragment = PinFragment()
                requireFragmentManager().beginTransaction().apply {
                    replace(R.id.home_fragment_container_view, pinFragment)
                    addToBackStack(null)
                    commit()
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.error), Toast.LENGTH_SHORT).show()
            }
        }

        loginViewModel.loginResponseErrorLiveData.observe(requireActivity()) { response ->
            if (response.isNotEmpty()) {
                binding.progressBar.visibility = View.GONE
                for (errorItem in response) {
                    if (errorItem.field == "username") {
                        binding.errorUserName.text =
                            getString(R.string.fragment_login_email_password_username_empty)
                    } else if (errorItem.field == "password") {
                        binding.errorPassword.text =
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
}