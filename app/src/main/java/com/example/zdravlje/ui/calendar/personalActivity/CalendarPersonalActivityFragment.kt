package com.example.zdravlje.ui.calendar.personalActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentCalendarPersonalActivityBinding

class CalendarPersonalActivityFragment : Fragment(),
    BottomSheetActivityNameFragment.OnNameListener {

    lateinit var binding: FragmentCalendarPersonalActivityBinding
    lateinit var activityName: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarPersonalActivityBinding.inflate(layoutInflater)
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

        binding.clNameActivity.setOnClickListener(View.OnClickListener {
            val bottomSheetActivityNameFragment = BottomSheetActivityNameFragment()
            bottomSheetActivityNameFragment.setOnAddressListener(this)
            bottomSheetActivityNameFragment.show(
                parentFragmentManager,
                bottomSheetActivityNameFragment.tag
            )
        })

        binding.mbNext.setOnClickListener(View.OnClickListener {
            val calendarPersonalActivityDateFragment = PersonalActivityDateFragment(activityName)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.calendar_options_container_view, calendarPersonalActivityDateFragment)
                .addToBackStack(null)
                .commit()
        })
    }

    override fun onAddressChange(address: String) {
        activityName = address
        binding.tvEnterActivity.text = address
        binding.mbNext.alpha = 1.0f
        binding.mbNext.isEnabled = true
    }
}