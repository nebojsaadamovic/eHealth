package com.example.zdravlje.ui.calendar.screening

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.zdravlje.ui.healthcare.screeningexaminations.MamografiaScreeningFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentCalendarScreeningBinding

class CalendarScreeningFragment : Fragment() {

    lateinit var binding: FragmentCalendarScreeningBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarScreeningBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.ivBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }

        binding.clMamografia.setOnClickListener() {
            val mamografiaScreeningFragment = MamografiaScreeningFragment.newInstance(
                getString(R.string.fragment_screening_program_national_preventive_breast_cancer_detection_program),
                getString(R.string.fragment_screening_program_mamo)
            )

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.calendar_options_container_view, mamografiaScreeningFragment)
                .addToBackStack(null).commit()
        }

        binding.clRgmScreening.setOnClickListener() {
            openFragment(MamografiaScreeningFragment())
        }
    }

    private fun openFragment(nameFragment: Fragment) {
        val fragment = nameFragment
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.calendar_options_container_view, fragment)
            .addToBackStack(null)
            .commit()
    }
}