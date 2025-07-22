package com.example.zdravlje.ui.adaptersAndViewHolders.profile

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.zdravlje.ui.profile.sharedProfile.ProfileICanSeeFragment
import com.example.zdravlje.ui.profile.sharedProfile.ProfileWhoCanSeeMyDataFragment
import com.nites.ezdravljeapp.R

class SharedProfileAdapter(
    fm: FragmentManager,
    private val context: Context
) : FragmentPagerAdapter(fm) {

    override fun getCount(): Int {
        return 2 // Broj tabova
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ProfileICanSeeFragment()  // moje dozvole
            1 -> ProfileWhoCanSeeMyDataFragment()  // date dozvole
            else -> throw IllegalArgumentException("Invalid position")
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> context.getString(R.string.shared_profile_adapter_who_can_see_data)
            1 -> context.getString(R.string.shared_profile_adapter_who_can_see_my_data)
            else -> null
        }
    }
}