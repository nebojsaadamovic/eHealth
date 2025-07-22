package com.example.zdravlje.ui.chat

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.zdravlje.data.model.request.PinRequest
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.LoginRepository
import com.example.zdravlje.ui.base.BaseBottomSheetDialogFragment
import com.example.zdravlje.ui.login.LoginViewModel
import com.example.zdravlje.ui.login.LoginViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentPinChatBottomSheetBinding
import kotlinx.coroutines.launch

class PinChatBottomSheetFragment : BaseBottomSheetDialogFragment() {

    lateinit var binding: FragmentPinChatBottomSheetBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private var pinChatListener: OnPinChatListener? = null

    interface OnPinChatListener {
        fun onPinChatChange()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPinChatBottomSheetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        subscribeToObservers()
        setupUI()
        binding.pinView.requestFocus()
        setupPinViewTextWatcher()
        binding.pinView.setTextColor(Color.parseColor("#808080"))
        binding.pinView.transformationMethod = PasswordTransformationMethod()

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


    private fun setViewModels() {
        val loginRepository = LoginRepository()
        dataStoreRepository = DataStoreRepository(requireContext())
        val loginViewModelFactory = LoginViewModelFactory(loginRepository, dataStoreRepository)
        loginViewModel =
            ViewModelProvider(this, loginViewModelFactory).get(LoginViewModel::class.java)
    }

    private fun subscribeToObservers() {
        loginViewModel.checkPinLiveData.observe(viewLifecycleOwner) { response ->
            val pin = binding.pinView.text.toString()
            if (pin.isNullOrEmpty()) {
                binding.errorPin.text =
                    getString(R.string.fragment_pin_chat_bottom_sheet_pin_cannot_be_empty)
                binding.pinView.requestFocus()
                return@observe
            }
            if (response) {
                lifecycleScope.launch {
                        savePin(pin)
                        pinChatListener?.onPinChatChange()
                       dismiss()
                }
        } else {
                binding.errorPin.text =
                    getString(R.string.fragment_pin_chat_bottom_sheet_wrong_pin_try_again)
                binding.pinView.setText("")
                binding.pinView.requestFocus()
            }
        }
    }

    private fun setupUI() {
        binding.mbSavePin.setOnClickListener(View.OnClickListener {
            loginViewModel.checkPin(PinRequest(binding.pinView.text.toString()))
        })
        binding.mbWithOutPin.setOnClickListener(View.OnClickListener {
            dismiss()
        })
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }

    fun setOnPinChatListener(listener: OnPinChatListener) {
        pinChatListener = listener
    }
}