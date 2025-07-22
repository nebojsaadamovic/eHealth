package com.example.zdravlje.ui.login.anonimus

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentEnterUserNameBottomSheetBinding


class EnterUserNameBottomSheet  : BottomSheetDialogFragment()  {

    lateinit var binding: FragmentEnterUserNameBottomSheetBinding


    interface OnEnterUserNameListener {
        fun onEnterUserName(address: String)
    }

    private var listener: OnEnterUserNameListener? = null


    fun setOnUserNameListener(listener: OnEnterUserNameListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEnterUserNameBottomSheetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.mbConfirm.setOnClickListener(View.OnClickListener {
            val address = binding.ietAddress.text.toString().trim()
            listener?.onEnterUserName(address)
            dismiss()
        })

        binding.mbCancel.setOnClickListener(View.OnClickListener {
            dismiss()
        })
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }

}