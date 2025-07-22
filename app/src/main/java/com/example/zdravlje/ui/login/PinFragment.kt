package com.example.zdravlje.ui.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.zdravlje.data.model.request.PinRequest
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.LoginRepository
import com.example.zdravlje.data.repository.ProfileRepository
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.home.HomeActivity
import com.example.zdravlje.ui.profile.ProfileViewModel
import com.example.zdravlje.ui.profile.ProfileViewModelFactory
import kotlinx.coroutines.launch
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentPinBinding

class PinFragment : BaseFragment() {

    lateinit var binding: FragmentPinBinding
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPinBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModels()

        profileViewModel.getUserInformation()
        checkPin()
        saveUserInformation()


        binding.pinView.requestFocus()
        setupPinViewTextWatcher()
        binding.pinView.setTextColor(Color.parseColor("#808080"))
        binding.pinView.transformationMethod = PasswordTransformationMethod()

        binding.btnSavePin.setOnClickListener(View.OnClickListener {
            binding.progressBar.visibility=View.VISIBLE
            loginViewModel.checkPin(PinRequest(binding.pinView.text.toString()))
        })

        binding.btnSkipPin.setOnClickListener(View.OnClickListener {
            lifecycleScope.launch {
                dataStoreRepository.deletePin()

            }
            clearPin()
            startActivity(Intent(requireActivity(), HomeActivity::class.java))
            requireActivity().finish()
        })
    }

    private fun setViewModels() {
        val loginRepository = LoginRepository()
        dataStoreRepository = DataStoreRepository(requireContext())
        val loginViewModelFactory = LoginViewModelFactory(loginRepository, dataStoreRepository)

        loginViewModel = ViewModelProvider(
            this,
            loginViewModelFactory
        ).get(LoginViewModel::class.java)

        val profileRepository = ProfileRepository()
        dataStoreRepository = DataStoreRepository(requireActivity())
        val profileViewModelFactory =
            ProfileViewModelFactory(profileRepository, dataStoreRepository)
        profileViewModel = ViewModelProvider(
            requireActivity(),
            profileViewModelFactory
        ).get(ProfileViewModel::class.java)

    }

    private fun setupPinViewTextWatcher() {
        binding.pinView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 6) {
                    // Here you can process the PIN code (e.g., validate or send it ..Optionally, add code to handle the input data and clear the pinView  binding.pinView.setText("") Reset PinView
                } else if (s != null && s.length > 10) {
                    binding.pinView.setText(s.substring(0, 10))
                } else {
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun checkPin() {
        loginViewModel.checkPinLiveData.observe(viewLifecycleOwner) { response ->
            binding.progressBar.visibility=View.GONE
            if (response) {

                savePin(binding.pinView.text.toString())
                startActivity(Intent(requireActivity(), HomeActivity::class.java))

                lifecycleScope.launch {
                    dataStoreRepository.savePin(binding.pinView.text.toString())
                    startActivity(Intent(requireActivity(), HomeActivity::class.java))
                }

            } else {
                Toast.makeText(requireContext(), getString(R.string.error), Toast.LENGTH_SHORT).show()
                binding.pinView.setText("");
                binding.pinView.requestFocus()
            }
        }
    }

    private fun saveUserInformation(){
        profileViewModel.userInfoLiveData.observe(viewLifecycleOwner) { userInfo ->

            lifecycleScope.launch {
                dataStoreRepository.saveAvatar(userInfo.body()?.avt_encoded_value.toString())
            }
        }
    }
}

