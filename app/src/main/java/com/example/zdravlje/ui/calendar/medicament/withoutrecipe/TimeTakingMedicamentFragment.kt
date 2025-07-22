package com.example.zdravlje.ui.calendar.medicament.withoutrecipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.zdravlje.data.model.response.dashboard.event_payload.MedicineEventPayload
import com.example.zdravlje.data.model.response.dashboard.future.Event
import com.example.zdravlje.data.model.response.healthcare.PrescribedListMedicamentResponse
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentTimeTakingMedicamentBinding

class TimeTakingMedicamentFragment(
    private var medicine: PrescribedListMedicamentResponse?=null,
    private var dateFrom: String,
    private var dateTo: String,
    private var dosage: String,
    private var event: Event  ?=null,
    private var medicineEventPayload: MedicineEventPayload?=null,
    private var headerTitle: String? = null
) : Fragment() {

    lateinit var binding: FragmentTimeTakingMedicamentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTimeTakingMedicamentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (event != null) {
            setupUIfromEvent()
        } else {
            setupUI()
        }
    }

    private fun setupUI() {
        binding.mtvMedicamenteValue.text = medicine?.medicamentProtectedName
        binding.mtvDateFromValue.text = dateFrom
        binding.mtvDateToValue.text = dateTo
        binding.mtvDosageValue.text = dosage

        binding.clTimeTackingMedicamente.setOnClickListener() {
            val addTimeTakingMedicamentFragment =
                AddTimeTakingMedicamentFragment(medicine!!, dateFrom, dateTo, dosage,null,null)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.calendar_options_container_view, addTimeTakingMedicamentFragment)
                .addToBackStack(null).commit()
        }

        binding.clInterval.setOnClickListener() {
            val addMedicamentDateIntervalFragment =
                AddMedicamentDateIntervalFragment(medicine!!, dateFrom, dateTo, dosage, null, null)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.calendar_options_container_view, addMedicamentDateIntervalFragment)
                .addToBackStack(null).commit()
        }

        binding.ivBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }
    }

    private fun setupUIfromEvent() {
        headerTitle?.let { binding.tvHeaderTitle.text = it}
        binding.mtvMedicamenteValue.text = event?.eventTitle
        binding.mtvDateFromValue.text = dateFrom
        binding.mtvDateToValue.text = dateTo
        binding.mtvDosageValue.text = dosage

        binding.clTimeTackingMedicamente.setOnClickListener() {
            val addTimeTakingMedicamentFragment =
                AddTimeTakingMedicamentFragment(null, dateFrom, dateTo, dosage, event, medicineEventPayload, headerTitle= headerTitle)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.drug_fragment_container, addTimeTakingMedicamentFragment)
                .addToBackStack(null).commit()
        }

        binding.clInterval.setOnClickListener() {
            val addMedicamentDateIntervalFragment =
                AddMedicamentDateIntervalFragment(null, dateFrom, dateTo, dosage, event, medicineEventPayload, headerTitle =headerTitle)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.drug_fragment_container, addMedicamentDateIntervalFragment)
                .addToBackStack(null).commit()
        }

        binding.ivBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }
    }
}