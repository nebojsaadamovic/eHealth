package com.example.zdravlje.ui.login

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.zdravlje.data.model.request.PinRequest
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.LoginRepository
import com.example.zdravlje.ui.base.BaseBottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentPinBottomSheetBinding

class PinBottomSheetFragment : BaseBottomSheetDialogFragment() {

    interface OnPinListener {
        fun onPinChange()
        fun onPinDismiss()
    }

    private var pinListener: OnPinListener? = null

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    lateinit var binding: FragmentPinBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPinBottomSheetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        subscribeToObservers()
        setupUI()

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
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
            if (pin.isEmpty()) {
                binding.errorPin.text = getString(R.string.fragment_pin_bottom_sheet_empty_pin)
                binding.pinView.requestFocus()
                return@observe
            }
            if (response) {
                savePin(pin)
                pinListener?.onPinChange()
                dismiss()
            } else {
                binding.errorPin.text = getString(R.string.fragment_pin_bottom_sheet_incorrect_pin)
                binding.pinView.setText("")
                binding.pinView.requestFocus()
            }
        }
    }

    private fun setupUI() {
        binding.mbSavePin.setOnClickListener {
            loginViewModel.checkPin(PinRequest(binding.pinView.text.toString()))
        }
        binding.mbWithOutPin.setOnClickListener {
            pinListener?.onPinDismiss()
            dismiss()
        }
    }

    fun setOnPinListener(listener: OnPinListener) {
        pinListener = listener
    }
}
