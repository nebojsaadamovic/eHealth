package com.example.zdravlje.ui.chat.singleAndGroupMessages

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.services.di.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentBottomSheetDeleteHistoryBinding

class BottomSheetDeleteHistory(
    private val chatId: String
) : BottomSheetDialogFragment() {

    lateinit var binding: FragmentBottomSheetDeleteHistoryBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var dataStoreRepository: DataStoreRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetDeleteHistoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
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
            mainViewModel.deleteChatHistory(chatId)
            val chatMessagesFragment = ChatMessagesFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.chatOptionFcv, chatMessagesFragment)
                .addToBackStack(null)
                .commit()
            dismiss()
            subscribeToObservers()
        })
    }

    private fun subscribeToObservers() {
        mainViewModel.deleteChatHistoryResponse.observe(viewLifecycleOwner) { response ->
            Toast.makeText(requireContext(), "Istoija poruka je obrisana", Toast.LENGTH_LONG).show()
            mainViewModel.getMessages(response.chatId)
        }
        mainViewModel.deleteChatHistoryResponseErrorLiveData.observe(viewLifecycleOwner) { response ->
            mainViewModel.getMessages(response.chatId)
        }
    }
}
