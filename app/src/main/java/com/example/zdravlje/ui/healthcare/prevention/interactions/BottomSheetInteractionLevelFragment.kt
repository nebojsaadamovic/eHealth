package com.example.zdravlje.ui.healthcare.prevention.interactions

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import com.example.zdravlje.ui.base.BaseBottomSheetDialogFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentBottomSheetInteractionLevelBinding


class BottomSheetInteractionLevelFragment(
    private var levelInteraction: String
) : BaseBottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomSheetInteractionLevelBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetInteractionLevelBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        Toast.makeText(requireActivity(),levelInteraction,Toast.LENGTH_SHORT).show()
        when (levelInteraction) {
            getString(R.string.fragment_interactions_description_no_interaction)-> binding.rbNoInteractions.isChecked = true
            getString(R.string.fragment_interactions_description_moderate_interaction) -> binding.rbModerateInteraction.isChecked = true
            getString(R.string.fragment_interactions_description_severe_interaction) -> binding.rbSeriousInteraction.isChecked = true
        }

        binding.mbSelect.setOnClickListener(){
            val checkedRadioButtonId = binding.rgInteractions.checkedRadioButtonId
            if (checkedRadioButtonId != -1) {
                val selectedRadioButton = binding.rgInteractions.findViewById<RadioButton>(checkedRadioButtonId)
                val selectedText = selectedRadioButton.text.toString()

                parentFragmentManager.setFragmentResult(
                    "rk-BottomSheetInteractionLevelFragment",
                    Bundle().apply {
                        putString("className", "BottomSheetInteractionLevelFragment")
                        putString("selectedText", selectedText)
                    }
                )
            }

            dismiss()
        }

        binding.mbClose.setOnClickListener(){
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }
}