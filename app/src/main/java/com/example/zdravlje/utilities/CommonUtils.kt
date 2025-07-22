package com.example.zdravlje.utilities

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.nites.ezdravljeapp.R
import java.util.Locale

class CommonUtils {

    companion object {
        const val PREFERENCES_NAME = "preferences"
        const val USER_DOES_NOT_EXIST = "USER_DOES_NOT_EXIST"

        fun openFragmentCalendar(activity: FragmentActivity, nameFragment: Fragment) {
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.calendar_options_container_view, nameFragment)
                .addToBackStack(null)
                .commit()
        }

        fun openFragmentHome(activity: FragmentActivity, nameFragment: Fragment) {
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.home_fragment_container_view, nameFragment)
                .addToBackStack(null)
                .commit()
        }

        fun openFragmentActivityPersonal(activity: FragmentActivity, nameFragment: Fragment) {
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.activity_personal_fragment_container, nameFragment)
                .addToBackStack(null)
                .commit()
        }

        fun getCurrentAppLocale(): Locale {
            return when (Locale.getDefault().toString()) {
                "sr" -> Locale.Builder().setLanguage("sr").setScript("Cyrl").setRegion("RS").build()
                "sr-rRS" -> Locale.Builder().setLanguage("sr").setScript("Latn").setRegion("RS")
                    .build()

                "en" -> Locale("en")
                else -> Locale("sr", "Latn")
            }
        }

        fun getInitials(fullName: String?): String {
            if (fullName.isNullOrBlank()) return "?"
            val parts = fullName.trim().split(" ")
            return when {
                parts.size >= 2 -> "${parts[0].first()}${parts[1].first()}".uppercase()
                parts.size == 1 -> parts[0].take(2).uppercase()
                else -> "?"
            }
        }
    }
}