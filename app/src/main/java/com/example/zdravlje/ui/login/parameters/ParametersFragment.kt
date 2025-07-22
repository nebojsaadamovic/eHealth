package com.example.zdravlje.ui.login.parameters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentParametersBinding

class ParametersFragment : BottomSheetDialogFragment(),
    WaistCircumferenceFragment.OnWaistCircumferenceSelectedListener,
    BloodGroupFragment.OnBloodGroupSelectedListener,
    ChestCircumferenceFragment.OnChestCircumferenceListener,
    HeightFragment.OnHeightSelectedListener, WeightFragment.OnWeightListener {

    lateinit var binding: FragmentParametersBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentParametersBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvBloodGroupValue.setOnClickListener(View.OnClickListener {
            val bloodGroupFragment = BloodGroupFragment()
            bloodGroupFragment.setTargetFragment(this, 0)
            bloodGroupFragment.show(
                requireActivity().supportFragmentManager,
                bloodGroupFragment.tag
            )
        })

        binding.tvChestCircumferenceValue.setOnClickListener(View.OnClickListener {
            val chestCircumferenceFragment = ChestCircumferenceFragment()
            chestCircumferenceFragment.setTargetFragment(this, 0)
            chestCircumferenceFragment.show(
                requireActivity().supportFragmentManager,
                chestCircumferenceFragment.tag
            )
        })

        binding.tvWaistCircumferenceValue.setOnClickListener {
            val waistCircumferenceFragment = WaistCircumferenceFragment()
            waistCircumferenceFragment.setTargetFragment(this, 0)
            waistCircumferenceFragment.show(
                requireActivity().supportFragmentManager, waistCircumferenceFragment.tag
            )
        }

        binding.tvHeightValue.setOnClickListener(View.OnClickListener {
            val heightFragment = HeightFragment()
            heightFragment.setTargetFragment(this, 0)
            heightFragment.show(requireActivity().supportFragmentManager, heightFragment.tag)
        })



        binding.tvWeightValue.setOnClickListener(View.OnClickListener {
            val weightFragment = WeightFragment()
            weightFragment.setTargetFragment(this, 0)
            weightFragment.show(requireActivity().supportFragmentManager, weightFragment.tag)
        })
    }

    override fun onWaistCircumferenceSelected(value: String) {
        binding.tvWaistCircumferenceValue.text = value
        Toast.makeText(
            requireContext(),
            "${getString(R.string.fragment_parameters_your_field_of_expertise)}: $value",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onBloodGroupSelected(value: String) {
        binding.tvBloodGroupValue.text = value
        Toast.makeText(
            requireContext(),
            "${getString(R.string.fragment_parameters_your_blood_type)}: $value",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onChestCircumferenceSelected(value: String) {
        binding.tvChestCircumferenceValue.text = value
        Toast.makeText(
            requireContext(),
            "${getString(R.string.fragment_parameters_your_chest_circumference)}: $value",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onHeightSelected(value: String) {
        binding.tvHeightValue.text = value
        Toast.makeText(
            requireContext(),
            "${getString(R.string.fragment_parameters_your_height)}: $value",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onWeightSelected(value: String) {
        binding.tvWeightValue.text = value
        Toast.makeText(
            requireContext(),
            "${getString(R.string.fragment_parameters_your_weight)}:$value",
            Toast.LENGTH_SHORT
        ).show()
    }
}