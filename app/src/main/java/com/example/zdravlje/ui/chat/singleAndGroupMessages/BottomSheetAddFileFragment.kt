package com.example.zdravlje.ui.chat.singleAndGroupMessages

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentBottomSheetAddFileBinding

class BottomSheetAddFileFragment : BottomSheetDialogFragment() {

    lateinit var binding: FragmentBottomSheetAddFileBinding

    interface OnBottomSheetAddFileOnClickListener {
        fun onOnBottomSheetAddFileOnClick()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetAddFileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivAddFile.setOnClickListener {
            val listener = targetFragment as? OnBottomSheetAddFileOnClickListener
            listener?.onOnBottomSheetAddFileOnClick()
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }


}