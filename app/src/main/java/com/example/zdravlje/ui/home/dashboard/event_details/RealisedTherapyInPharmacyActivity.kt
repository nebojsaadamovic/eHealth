package com.example.zdravlje.ui.home.dashboard.event_details

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.zdravlje.data.model.response.dashboard.event_payload.TherapyEventPayload
import com.example.zdravlje.data.model.response.dashboard.future.Event
import com.example.zdravlje.ui.base.BaseActivity
import com.example.zdravlje.utilities.DateUtils
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.ActivityRealisedTherapyInPharmacyBinding

class RealisedTherapyInPharmacyActivity : BaseActivity() {

    lateinit var binding: ActivityRealisedTherapyInPharmacyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRealisedTherapyInPharmacyBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        binding.mtvNameValue.text = item.details

        binding.mtvTimeValue.text = item.date
        binding.mtvPharmacyValue.text = item.place
        binding.mtvTypeValue.text = getString(R.string.activity_realised_therapy_in_pharmacy_erecept)
        binding.ivBack.setOnClickListener {
            this.finish()
        }
    }
}