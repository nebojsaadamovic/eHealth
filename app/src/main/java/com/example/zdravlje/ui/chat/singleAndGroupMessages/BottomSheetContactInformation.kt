package com.example.zdravlje.ui.chat.singleAndGroupMessages

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.democall.composesample.data.chat.list.ChatListResponseItem
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentBottomSheetContactInformationBinding

class BottomSheetContactInformation(
    private val chatResponse: ChatListResponseItem
) : BottomSheetDialogFragment() {

    lateinit var binding: FragmentBottomSheetContactInformationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetContactInformationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.tvNameValue.text=chatResponse.participants.lastOrNull()?.sgcoName
        // binding.tvEmailValue.text=chatResponse.participants.firstOrNull()?.email
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }
}