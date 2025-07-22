package com.example.zdravlje.ui.home.dashboard.event_details

import android.os.Bundle
import android.util.Log
import com.example.zdravlje.data.model.response.dashboard.event_payload.CaseEventPayload
import com.example.zdravlje.data.model.response.dashboard.future.Event
import com.example.zdravlje.ui.base.BaseActivity
import com.example.zdravlje.utilities.DateUtils
import com.nites.ezdravljeapp.databinding.ActivityRealisedVisitedDoctorBinding

class RealisedVisitedDoctorActivity : BaseActivity() {

    lateinit var binding: ActivityRealisedVisitedDoctorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRealisedVisitedDoctorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //    this.window.navigationBarColor = ContextCompat.getColor(this, R.color.white_bg)

        val item = intent.getSerializableExtra("caseEventPayload") as? CaseEventPayload
        val event = intent.getSerializableExtra("event") as? Event

        if (item != null) {
            if (event != null) {
                setupUI(item, event)
            }
        }
    }

    private fun setupUI(item: CaseEventPayload, event: Event) {
        binding.activityTitle.text = event.eventStartTime?.let { DateUtils.getDateWithWeekName(it) }
        binding.mtvPlaceValue.text = item.caseInstitution
        binding.mtvPlaceInHospitalValue.text = item.detail.admissionCabinetName
        binding.mtvVisitTypeValue.text = item.detail.referralResponseTypeName
        binding.mtvDoctorNameValue.text = item.detail.admissionDoctor
        binding.mtvMainTypeValue.text = item.detail.referralType
        binding.mtvDateValue.text = DateUtils.formatDate(event.eventEndTime!!)
        binding.ivBack.setOnClickListener {
            this.onBackPressed()
        }
    }
}