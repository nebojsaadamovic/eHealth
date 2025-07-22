package com.example.zdravlje.ui.home.dashboard.event_details

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.zdravlje.data.model.response.dashboard.event_payload.TherapyEventPayload
import com.example.zdravlje.data.model.response.dashboard.future.Event
import com.example.zdravlje.ui.base.BaseActivity
import com.example.zdravlje.utilities.DateUtils
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.ActivityAvaliableTherapyInPharmacyBinding

class AvaliableTherapyInPharmacyActivity : BaseActivity() {

    lateinit var binding: ActivityAvaliableTherapyInPharmacyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAvaliableTherapyInPharmacyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val languageCode = (this as? BaseActivity)?.getSavedLanguage() ?: "sr"
        val item = intent.getSerializableExtra("therapyEventPayload") as? TherapyEventPayload
        val event = intent.getSerializableExtra("event") as? Event
        if (item != null) {
            if (event != null) {
                setupUI(item, event)
            }
        }
    }

    private fun setupUI(item: TherapyEventPayload, event: Event) {
        binding.activityTitle.text = event.eventStartTime?.let { DateUtils.getDateWithWeekName(it) }
        binding.mtvTerValue.text = item.details
        binding.mtvTimeValue.text = getString(R.string.activity_available_therapy_all_day)
        binding.mtvInfoValue.text = getString(R.string.activity_available_therapy_in_pharmacy_message)
        binding.ivBack.setOnClickListener {
            this.finish()
        }
    }
}