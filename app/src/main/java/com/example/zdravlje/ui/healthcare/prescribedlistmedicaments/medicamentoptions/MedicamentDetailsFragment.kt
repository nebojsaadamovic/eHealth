package com.example.zdravlje.ui.healthcare.prescribedlistmedicaments.medicamentoptions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.zdravlje.data.model.response.healthcare.PatmedNotes
import com.example.zdravlje.data.model.response.healthcare.PrescribedListMedicamentResponse
import com.google.gson.Gson
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentMedicamentDetailsBinding

class MedicamentDetailsFragment(
    private val prescribedObject: PrescribedListMedicamentResponse
) : Fragment() {

    private lateinit var binding: FragmentMedicamentDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMedicamentDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
       val gson = Gson()
       val payload = gson.fromJson(prescribedObject.patmedNotes, PatmedNotes::class.java)

        binding.mtvMedicineNameValue.text = if (prescribedObject.medicamentProtectedName.isNullOrBlank()) "/" else prescribedObject.medicamentProtectedName
        binding.mtvPackageValue.text = if (payload.medicamentPackageForm.isNullOrBlank()) "/" else payload.medicamentPackageForm
        binding.mtvManufacturerValue.text = when {
            !prescribedObject.companyMaker.isNullOrBlank() -> prescribedObject.companyMaker
            !payload.manufacturer.isNullOrBlank() -> payload.manufacturer
            else -> "/"
        }
        binding.mtvDosageValue.text = if (payload.dosage.isNullOrBlank()) "/" else payload.dosage
        binding.mtvQuantityValue.text =  if (payload.quantity.isNullOrBlank()) "/" else payload.quantity
        binding.mtvDiagnosisValue.text = if (payload.prescriptionDiagnosis.isNullOrBlank()) "/" else payload.prescriptionDiagnosis
        binding.mtvPrimaryPackageValue.text = if (payload.drugPrimaryPackageForm.isNullOrBlank()) "/" else payload.drugPrimaryPackageForm
        binding.mtvDosageValue.text = if (payload.dosage.isNullOrBlank()) "/" else payload.dosage
        binding.mtvQuantityValue.text = if (payload.quantity.isNullOrBlank()) "/" else payload.quantity

        binding.mtvStatusValue.text = when (prescribedObject.patmed_rcpt_status) {
            "1" -> getString(R.string.fragment_prescribed_list_medicaments_request_approved)
            "2" -> getString(R.string.fragment_prescribed_list_medicaments_request_completed)
            "4" -> getString(R.string.fragment_prescribed_list_medicaments_expired)
            "6" -> getString(R.string.fragment_prescribed_list_medicaments_requests_canceled)
            "100" -> getString(R.string.fragment_prescribed_list_medicaments_manual_entry)
            else -> getString(R.string.fragment_prescribed_list_medicaments_prescription_status_unknown)
        }

        binding.mtvDiagnosisValue.text = if (payload.prescriptionDiagnosis.isNullOrBlank()) "/" else payload.prescriptionDiagnosis
        binding.mtvJKLValue.text = if (prescribedObject.jkl.isNullOrBlank()) "/" else prescribedObject.jkl

        binding.mtvPrescriptionTypeValue.text = when (prescribedObject.prescriptionType) {
            "1" -> getString(R.string.fragment_prescribed_list_medicaments_prescription_type_e_therapy)
            "2" -> getString(R.string.fragment_prescribed_list_medicaments_prescription_type_e_prescription)
            "3" -> getString(R.string.fragment_prescribed_list_medicaments_prescription_type_no_prescription)
            else -> getString(R.string.fragment_prescribed_list_medicaments_prescription_type_unknown)
        }

        binding.mtvDatePrescribedValue.text = if (payload.createDate.isNullOrBlank()) "/" else payload.createDate
        binding.mtvDateDueValue.text =  if (payload.arrivalDate.isNullOrBlank()) "/" else payload.arrivalDate
        binding.mtvPrescribedByValue.text = if (payload.issuingUser.isNullOrBlank()) "/" else payload.issuingUser

//        binding.mtvDoctorValue.text = if (payload.issuingUser.isNullOrBlank()) "/" else payload.issuingUser
//        binding.mtvDateIssuedValue.text = if (payload.createDate.isNullOrBlank()) "/" else payload.createDate
//        binding.mtvDateReceivedValue.text =  if (payload.arrivalDate.isNullOrBlank()) "/" else payload.arrivalDate
//        binding.mtvPharmacyValue.text =if (payload.pharmacy.isNullOrBlank()) "/" else payload.pharmacy
//        binding.mtvPharmacistValue.text = if (payload.pharmacist.isNullOrBlank()) "/" else payload.pharmacist

        binding.ivBack.setOnClickListener() {
          requireActivity().onBackPressed()
        }
    }
}