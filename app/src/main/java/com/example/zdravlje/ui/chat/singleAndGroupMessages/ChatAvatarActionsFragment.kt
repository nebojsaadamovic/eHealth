package com.example.zdravlje.ui.chat.singleAndGroupMessages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.democall.composesample.data.chat.list.ChatListResponseItem
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentChatAvatarActionsBinding

class ChatAvatarActionsFragment(
    private val chatResponse: ChatListResponseItem
) : Fragment() {

    private lateinit var binding: FragmentChatAvatarActionsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatAvatarActionsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.ivDeleteMember.setOnClickListener(View.OnClickListener {
            val bundle = Bundle().apply {
                putBoolean("deletedBottomSheet", true)
            }
            val chatMessagesFragment = ChatMessagesFragment(chatResponse).apply {
                arguments = bundle
            }
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.chatOptionFcv, chatMessagesFragment)
                .addToBackStack(null).commit()
        })

        binding.ivDeleteHistory.setOnClickListener(View.OnClickListener {
            val chatMessagesFragment = ChatMessagesFragment(chatResponse)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.chatOptionFcv, chatMessagesFragment).addToBackStack(null).commit()

            val bottomSheetDeleteHistory = BottomSheetDeleteHistory(chatResponse.id)
            bottomSheetDeleteHistory.setTargetFragment(this, 0)
            bottomSheetDeleteHistory.show(
                requireActivity().supportFragmentManager, bottomSheetDeleteHistory.tag
            )
        })

        binding.ivInformation.setOnClickListener(View.OnClickListener {
            val bottomSheetContactInformation = BottomSheetContactInformation(chatResponse)
            bottomSheetContactInformation.setTargetFragment(this, 0)
            bottomSheetContactInformation.show(
                requireActivity().supportFragmentManager, bottomSheetContactInformation.tag
            )
        })
        binding.ivAddMember.setOnClickListener(View.OnClickListener {
            val addMemberFragment = AddMemberFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.chatOptionFcv, addMemberFragment)
                .addToBackStack(null).commit()
        })

        binding.ivClose.setOnClickListener(View.OnClickListener {
            requireActivity().finish()
        })
    }

}