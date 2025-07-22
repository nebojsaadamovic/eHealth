package com.example.zdravlje.ui.healthcare.appointment_scheduling

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class MyFragmentPagerAdapter(fm: FragmentManager) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ScheduledAppointmentsFragment()
            1 -> AppointmentHistoryFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}
