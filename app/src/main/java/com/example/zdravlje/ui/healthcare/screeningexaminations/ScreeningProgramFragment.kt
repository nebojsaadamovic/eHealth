package com.example.zdravlje.ui.healthcare.screeningexaminations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentScreeningProgramBinding

class ScreeningProgramFragment : Fragment() {

    lateinit var binding: FragmentScreeningProgramBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScreeningProgramBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.clMamografia.setOnClickListener {
            val mamografiaScreeningFragment = MamografiaScreeningFragment.newInstance(
                getString(R.string.fragment_screening_program_national_preventive_breast_cancer_detection_program),
                getString(R.string.fragment_screening_program_mamo)
            )
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.home_fragment_container_view, mamografiaScreeningFragment)
                .addToBackStack(null).commit()
        }

        binding.clRgmScreening.setOnClickListener {
            val mamografiaScreeningFragment = MamografiaScreeningFragment.newInstance(
                getString(R.string.fragment_screening_program_national_preventive_cervical_cancer_detection_program),
                getString(R.string.fragment_screening_program_rgm)
            )
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.home_fragment_container_view, mamografiaScreeningFragment)
                .addToBackStack(null).commit()
        }

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
}