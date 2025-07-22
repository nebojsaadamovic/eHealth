package com.example.zdravlje.ui.healthcare.prevention.interactions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class InteractionsSwitchPage(fm: FragmentManager) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> MedicamenteInteractionFragment()
            1 -> PreviousCheckFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}