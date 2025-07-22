package com.example.zdravlje.ui.chat.singleAndGroupMessages


import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.app.democall.composesample.data.chat.list.ChatListResponseItem
import com.example.zdravlje.data.model.request.chat.RemoveContactRequest
import com.example.zdravlje.data.repository.ChatRepository
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.ui.base.BaseBottomSheetDialogFragment
import com.example.zdravlje.ui.chat.ChatViewModel
import com.example.zdravlje.ui.chat.ChatViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentBottomSheetDeleteContactBinding

class BottomSheetDeleteContact(
    private val chatData: ChatListResponseItem
) : BaseBottomSheetDialogFragment() {

    lateinit var binding: FragmentBottomSheetDeleteContactBinding
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private var userPin: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetDeleteContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        observeToObservers()
        setupUI()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }


    private fun setupUI() {
        binding.mbCancel.setOnClickListener(View.OnClickListener {
            dismiss()
        })

        binding.mbDelete.setOnClickListener(View.OnClickListener {
            val participant = chatData?.participants?.lastOrNull()?.sgcoId!!
            chatViewModel.removeContact(
                RemoveContactRequest(
                    pin = userPin,
                    sgco_id = participant,
                    vcc_id = chatData.id
                )
            )
            dismiss()
            requireActivity().finish()
        })
    }

    private fun setViewModels() {
        val chatRepository = ChatRepository()
        dataStoreRepository = DataStoreRepository(requireActivity())
        val chatViewModelFactory =
            ChatViewModelFactory(chatRepository, dataStoreRepository)
        chatViewModel = ViewModelProvider(
            requireActivity(),
            chatViewModelFactory
        ).get(ChatViewModel::class.java)
    }

    private fun observeToObservers() {
        userPin=getStoredPin()
    }
}
