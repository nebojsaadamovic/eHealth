package com.example.zdravlje.ui.chat.singleAndGroupMessages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.democall.composesample.data.chat.list.ChatListResponseItem
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentGroupOptionsChatBinding

class GroupChatOptionsFragment(
    private val chatResponse: ChatListResponseItem
) : Fragment() {

    private lateinit var binding: FragmentGroupOptionsChatBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGroupOptionsChatBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.mtvGrupNameValue.text = chatResponse.title

        binding.ivParticipants.setOnClickListener() {
            val chatAddContactFragment = GroupParticipantsFragment(chatResponse.id)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.chatOptionFcv, chatAddContactFragment)
                .addToBackStack(null).commit()
        }

        binding.ivClose.setOnClickListener(View.OnClickListener {
            requireActivity().finish()
        })
    }
}