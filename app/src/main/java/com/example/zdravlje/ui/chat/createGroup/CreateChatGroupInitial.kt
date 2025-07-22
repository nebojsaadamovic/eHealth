package com.example.zdravlje.ui.chat.createGroup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.services.di.MainViewModel
import com.example.zdravlje.ui.chat.options.ChatOptionsActivity
import com.nites.ezdravljeapp.databinding.FragmentCreateChatGroupInitialBinding

class CreateChatGroupInitial : Fragment() {

    private lateinit var binding: FragmentCreateChatGroupInitialBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var dataStoreRepository: DataStoreRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateChatGroupInitialBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        setupUI()
    }

    private fun setupUI() {
        binding.etGroupName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Ne koristimo
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s.toString().trim()
                if (text.length > 0) {
                    binding.mbContinue.isEnabled = true
                    binding.mbContinue.alpha = 1.0f
                }
                else {
                    binding.mbContinue.isEnabled = false
                    binding.mbContinue.alpha = 0.3f
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Ne koristimo
            }
        })

        binding.mbContinue.setOnClickListener(View.OnClickListener {
            val activity = requireActivity() as ChatOptionsActivity
            activity.startNewFragmentFromChatOptions(CreateGroupMyContactFragment(binding.etGroupName.text.toString()))
        })

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setViewModels() {
        val chatActivity = requireActivity() as ChatOptionsActivity
        mainViewModel = chatActivity.getMainViewModel()
        dataStoreRepository = chatActivity.getDataStoreRepository()
    }
}