package com.example.zdravlje.ui.calendar.options

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.zdravlje.ui.base.BaseActivity
import com.example.zdravlje.ui.calendar.CalendarFragment
import com.example.zdravlje.ui.calendar.apointment.CalendarAppointmentExaminationFragment
import com.example.zdravlje.ui.calendar.medicament.withoutrecipe.MedicamentWithOutRecipeFragment
import com.example.zdravlje.ui.calendar.medicament.withrecipe.MedicamentWithRecipeFragment
import com.example.zdravlje.ui.calendar.personalActivity.CalendarPersonalActivityFragment
import com.example.zdravlje.ui.calendar.personalActivity.PersonalActivityDateDetailsFragment
import com.example.zdravlje.ui.calendar.personalActivity.PersonalActivityDateFragment
import com.example.zdravlje.ui.calendar.personalActivity.PersonalActivityDateIntervalFragment
import com.example.zdravlje.ui.calendar.personalActivity.PersonalActivityTimeActivityFragment
import com.example.zdravlje.ui.calendar.screening.CalendarScreeningFragment
import com.example.zdravlje.ui.chat.ChatInboxFragment
import com.example.zdravlje.ui.fitparameters.FitParametersFragment
import com.example.zdravlje.ui.healthcare.HealthcareFragment
import com.example.zdravlje.ui.healthcare.medicalcarton.MedicalCartonFragment
import com.example.zdravlje.ui.healthcare.prescribedlistmedicaments.PrescribedListMedicamentsFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.ActivityCalendarOptionsBinding

class CalendarOptionsActivity : BaseActivity() {

    lateinit var binding: ActivityCalendarOptionsBinding

    enum class CalendarOptionsFragments {
        INITIAL_FRAGMENT,
        ADD_MEDICAMENTE_WITH_OUT_RECIPE,
        ADD_MEDICAMENTE_FROM_E_RECIPE,
        SCHEDULE_EXAMINATION,
        APPLICATION_FOR_SCREENING,
        PERSONAL_ACTIVITY,
        CALENDAR_FRAGMENT
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.window.statusBarColor = ContextCompat.getColor(this, R.color.white_bg)
        this.window.navigationBarColor = ContextCompat.getColor(this, R.color.white_bg)

        val fragmentOption =
            intent.getSerializableExtra("fragment_option") as? CalendarOptionsFragments
                ?: CalendarOptionsFragments.INITIAL_FRAGMENT
        openFragment(fragmentOption)
    }

    private fun openFragment(calendarOptionsFragments: CalendarOptionsFragments) {
        val fragment = when (calendarOptionsFragments) {
            CalendarOptionsFragments.ADD_MEDICAMENTE_WITH_OUT_RECIPE -> MedicamentWithOutRecipeFragment()
            CalendarOptionsFragments.PERSONAL_ACTIVITY -> CalendarPersonalActivityFragment()
            CalendarOptionsFragments.SCHEDULE_EXAMINATION -> CalendarAppointmentExaminationFragment()
            CalendarOptionsFragments.APPLICATION_FOR_SCREENING -> CalendarScreeningFragment()
            CalendarOptionsFragments.ADD_MEDICAMENTE_FROM_E_RECIPE -> MedicamentWithRecipeFragment()
            CalendarOptionsFragments.INITIAL_FRAGMENT -> CalendarOptionsFragment()
            CalendarOptionsFragments.CALENDAR_FRAGMENT -> CalendarFragment()
        }

        this.supportFragmentManager.beginTransaction()
            .replace(R.id.calendar_options_container_view, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.calendar_options_container_view)

        if(currentFragment is PersonalActivityDateFragment
            || currentFragment is PersonalActivityDateDetailsFragment
            || currentFragment is PersonalActivityDateIntervalFragment
            || currentFragment is PersonalActivityTimeActivityFragment

            ){
            openFragment(CalendarOptionsFragments.CALENDAR_FRAGMENT)
            return
        }

        if (currentFragment is CalendarOptionsFragment) {
            finish()
        } else {
            super.onBackPressed()
        }
    }
}