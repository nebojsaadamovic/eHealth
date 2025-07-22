package com.example.zdravlje.ui.calendar.options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.zdravlje.ui.calendar.medicament.withrecipe.MedicamentWithRecipeFragment
import com.example.zdravlje.ui.calendar.personalActivity.CalendarPersonalActivityFragment
import com.example.zdravlje.ui.calendar.screening.CalendarScreeningFragment
import com.example.zdravlje.ui.healthcare.appointment_scheduling.AppointmentSchedulingFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentCalendarOptionsBinding

class CalendarOptionsFragment : Fragment() {

    lateinit var binding: FragmentCalendarOptionsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarOptionsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.clAddMedicamentFromERecipe.setOnClickListener() {
            openFragment(MedicamentWithRecipeFragment())
        }
/*
       binding.ivAddMedicament.setOnClickListener() {
            openFragment(MedicamentWithOutRecipeFragment())
        }
*/
        binding.clPersonalActivity.setOnClickListener() {
            openFragment(CalendarPersonalActivityFragment())
        }

        binding.clScheduleExamination.setOnClickListener() {
            openFragment(AppointmentSchedulingFragment().apply {
                arguments = Bundle().apply {
                    putString("class_name", "CalendarOptionsFragment")
                }
            })
        }

        binding.clScreening.setOnClickListener() {
           openFragment(CalendarScreeningFragment())
        }

        binding.blurBackground.setOnClickListener(){
           requireActivity().finish()
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