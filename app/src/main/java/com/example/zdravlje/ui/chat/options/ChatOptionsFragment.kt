package com.example.zdravlje.ui.chat.options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.zdravlje.ui.chat.createGroup.CreateChatGroupInitial
import com.example.zdravlje.ui.chat.scheduleCall.ScheduleCallFragment
import com.nites.ezdravljeapp.databinding.FragmentChatOptionsBinding


class ChatOptionsFragment : Fragment() {

    lateinit var binding: FragmentChatOptionsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatOptionsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
    }

    private fun setupUi() {
        binding.ivClose.setOnClickListener(View.OnClickListener {
            requireActivity().finish()
        })

        val activity = requireActivity() as ChatOptionsActivity
        binding.ivAddMember.setOnClickListener(View.OnClickListener {
            activity.startNewFragmentFromChatOptions(ChatAddContactFragment())
        })

        binding.ivCreateChatGroup.setOnClickListener(View.OnClickListener {
            activity.startNewFragmentFromChatOptions(CreateChatGroupInitial())
        })


        binding.ivScheduleCall.setOnClickListener(View.OnClickListener {
            activity.startNewFragmentFromChatOptions(ScheduleCallFragment())
        })

        binding.ivLinkCall.setOnClickListener(View.OnClickListener {
            activity.startNewFragmentFromChatOptions(ChatLinkCallFragment())
        })
    }
}