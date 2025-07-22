package com.example.zdravlje.ui.login.info

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentHelpAndSupportBottomSheetBinding

class HelpAndSupportBottomSheetFragment : BottomSheetDialogFragment() {

    lateinit var binding: FragmentHelpAndSupportBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHelpAndSupportBottomSheetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.mbFreqAskQuest.setOnClickListener(View.OnClickListener {
            val frequentlyAskedQuestionsFragment = FrequentlyAskedQuestionsFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.home_fragment_container_view, frequentlyAskedQuestionsFragment)
                .addToBackStack(null)
                .commit()
            dismiss()
        })

        binding.mbAskQuest.setOnClickListener(View.OnClickListener {
            val askQuestionFragment = AskQuestionFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.home_fragment_container_view, askQuestionFragment)
                .addToBackStack(null)
                .commit()
            dismiss()
        })
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }
}