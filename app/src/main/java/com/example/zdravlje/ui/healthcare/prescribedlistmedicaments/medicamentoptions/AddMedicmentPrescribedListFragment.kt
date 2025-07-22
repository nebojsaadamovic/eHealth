package com.example.zdravlje.ui.healthcare.prescribedlistmedicaments.medicamentoptions

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.zdravlje.data.model.request.healthcare.AddMedicamentRequest
import com.example.zdravlje.data.model.request.healthcare.PatmedDataRequest
import com.example.zdravlje.data.model.request.healthcare.PatmedNotes
import com.example.zdravlje.data.model.request.healthcare.PatmedNotesRequest
import com.example.zdravlje.data.model.response.calendar.MedicineResponse
import com.example.zdravlje.data.model.response.healthcare.PrescribedListMedicamentResponse
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.calendar.BottomSheetInsertValue
import com.example.zdravlje.ui.healthcare.HealthcareViewModel
import com.example.zdravlje.ui.healthcare.HealthcareViewModelFactory
import com.example.zdravlje.ui.healthcare.prescribedlistmedicaments.PrescribedListMedicamentsFragment
import com.example.zdravlje.utilities.CommonUtils
import com.example.zdravlje.utilities.DateUtils
import com.google.gson.Gson
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentAddMedicmentPrescribedListBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddMedicmentPrescribedListFragment(
    private val prescribedObject: PrescribedListMedicamentResponse? = null
) : BaseFragment(), BottomSheetInsertValue.OnValueListener {

    private lateinit var binding: FragmentAddMedicmentPrescribedListBinding
    private lateinit var healthcareViewModel: HealthcareViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private var medicineItem: MedicineResponse? = null
    private var fieldTag: String? = null
    private var title: String? = null
    private var subtitle: String? = null
    private var nameClass: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddMedicmentPrescribedListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewModels()
        arguments?.let {
            medicineItem = it.getParcelable("medicine_response")
            nameClass = it.getString("edit_medicamente")
        }
        subscribeToObservers()
        setupUI()
    }

    private fun setupUI() {
        binding.mtvHeaderTitle.text =
            DateUtils.getDateWithWeekName(DateUtils.getCurrentDateTime())

        binding.mtvNameMedicamenteValue.text = medicineItem?.cbmed_protected_name

        binding.mtvDosageValue.setOnClickListener() {
            fieldTag = "dosage"
            title = getString(R.string.fragment_add_medicamente_prescibed_list_enter_dosage)
            subtitle = getString(R.string.enter_value)
            openEnterValueBottomSheet()
        }

        binding.mtvQuantityValue.setOnClickListener() {
            fieldTag = "quantity"
            title = getString(R.string.fragment_add_medicamente_prescibed_list_enter_quantity)
            subtitle = getString(R.string.enter_value)
            openEnterValueBottomSheet()
        }

        binding.mtvDiagnosisValue.setOnClickListener() {
            fieldTag = "diagnosis"
            title = getString(R.string.fragment_add_medicamente_prescibed_list_enter_diagnosis)
            subtitle = getString(R.string.enter_value)
            openEnterValueBottomSheet()
        }

        binding.mtvPrescribedByDoctorValue.setOnClickListener() {
            fieldTag = "prescribedByDoctor"
            title = getString(R.string.fragment_add_medicamente_prescibed_list_prescribed_by_doctor)
            subtitle = getString(R.string.enter_value)
            openEnterValueBottomSheet()
        }

        binding.mtvRealizedInPharmacyValue.setOnClickListener() {
            fieldTag = "realizedInPharmacy"
            title = getString(R.string.fragment_add_medicamente_prescibed_list_dispensed_pharmacy)
            subtitle = getString(R.string.enter_value)
            openEnterValueBottomSheet()
        }

        binding.mtvPharmacistValue.setOnClickListener() {
            fieldTag = "pharmacist"
            title = getString(R.string.fragment_add_medicamente_prescibed_list_pharmacist)
            subtitle = getString(R.string.enter_value)
            openEnterValueBottomSheet()
        }

        binding.mtvPeriodFromValue.setOnClickListener() {
            fieldTag = "periodFrom"
            title = getString(R.string.fragment_add_medicamente_prescibed_list_date_prescribed)
            subtitle = getString(R.string.enter_value)
            openEnterValueBottomSheet()
        }

        binding.mtvPeriodFromValue.setOnClickListener() {
            DateUtils.showDatePickerDialog(requireContext(), binding.mtvPeriodFromValue) {
                binding.mtvPeriodFromValue.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.text_color_8A8A8D
                    )
                )
            }
        }

        binding.mtvPeriodToValue.text = DateUtils.getCurrentTimeHorLineddMyyyy()

        if (nameClass == "edit_medicamente") {
            val patmedNotesJson = prescribedObject?.patmedNotes
            val payload: PatmedNotes? = Gson().fromJson(patmedNotesJson, PatmedNotes::class.java)

            binding.mtvTitleMedicamente.text = getString(R.string.fragment_add_medicamente_prescibed_list_edit_medicine)
            val mtvNameMedicamenteValue = prescribedObject?.medicamentProtectedName
            binding.mtvNameMedicamenteValue.text = if (mtvNameMedicamenteValue.isNullOrBlank()) getString(R.string.select) else mtvNameMedicamenteValue
            binding.mtvNameMedicamenteValue.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.text_color_8A8A8D
                )
            )

            val mtvDosageValue = payload?.dosage
            binding.mtvDosageValue.text =
                if (mtvDosageValue.isNullOrBlank()) getString(R.string.select) else mtvDosageValue

            val mtvQuantityValue = payload?.quantity
            binding.mtvDiagnosisValue.text =
                if (mtvQuantityValue.isNullOrBlank()) getString(R.string.select) else mtvQuantityValue

            val mtvDiagnosisValue = payload?.diagnosis
            binding.mtvDiagnosisValue.text =
                if (mtvDiagnosisValue.isNullOrBlank()) getString(R.string.select) else mtvDiagnosisValue

            val mtvPrescribedByDoctorValue = payload?.issuingUser
            binding.mtvPrescribedByDoctorValue.text =
                if (mtvPrescribedByDoctorValue.isNullOrBlank()) getString(R.string.select) else mtvPrescribedByDoctorValue

            val mtvPharmacistValue = payload?.pharmacistName
            binding.mtvPharmacistValue.text =
                if (mtvPharmacistValue.isNullOrBlank()) getString(R.string.select) else mtvPharmacistValue

            val mtvRealizedInPharmacyValue = payload?.pharmacyName
            binding.mtvRealizedInPharmacyValue.text =
                if (mtvRealizedInPharmacyValue.isNullOrBlank()) getString(R.string.select) else mtvRealizedInPharmacyValue

            var mtvPeriodFromValue = payload?.createDate
            binding.mtvPeriodFromValue.text =
                if (mtvPeriodFromValue.isNullOrBlank()) getString(R.string.select)
                else DateUtils.convertDotddMMyyyToVerLineddMyyyy(mtvPeriodFromValue)

            binding.mtvPeriodFromValue.setOnClickListener() {
                DateUtils.showDatePickerDialog(
                    requireContext(),
                    binding.mtvPeriodFromValue
                ) { newDate ->
                    mtvPeriodFromValue = newDate
                    binding.mtvPeriodFromValue.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.text_color_8A8A8D
                        )
                    )
                }
            }

            val mtvPeriodToValue = DateUtils.getCurrentTimeHorLineddMyyyy()
            binding.mtvPeriodToValue.text =
                if (mtvPeriodToValue.isNullOrBlank()) getString(R.string.select) else mtvPeriodToValue

            binding.mtvSave.setOnClickListener() {
                healthcareViewModel.updateMedicamente(
                    PatmedDataRequest(
                        patmed_id = prescribedObject?.patmedId!!.toInt(),
                        pin = getStoredPin(),
                        patmed_medicine_id = prescribedObject.medicamentId?.toInt(),
                        patmed_notes = PatmedNotesRequest(
                            createDate = formatDate(mtvPeriodFromValue.toString()),
                            arrivalDate = formatDate(mtvPeriodToValue),
                            issuingUser = mtvPrescribedByDoctorValue,
                            pharmacist = mtvPharmacistValue,
                            pharmacy = mtvRealizedInPharmacyValue,
                            dosage = mtvDosageValue,
                            quantity = mtvQuantityValue,
                            prescriptionDiagnosis = mtvDiagnosisValue,
                        )
                    )
                )
            }

        } else {
            var dateCreated= SimpleDateFormat("d.M.y", Locale.getDefault()).format(Date())
            binding.mtvPeriodFromValue.text =  SimpleDateFormat("d/M/y", Locale.getDefault()).format(Date())
            binding.mtvSave.setOnClickListener() {

                healthcareViewModel.addMedicamentPrescribed(
                    AddMedicamentRequest(
                        medicineId = medicineItem?.cbmed_id,
                        pin = getStoredPin(),
                        patmedNotes = PatmedNotes(
                            createDate = dateCreated,
                            arrivalDate = formatDate(binding.mtvPeriodToValue.text.toString()),
                            issuingUser = binding.mtvPrescribedByDoctorValue.getUserInputOrDefault(),
                            pharmacistName = binding.mtvPharmacistValue.getUserInputOrDefault(),
                            pharmacyName = binding.mtvRealizedInPharmacyValue.getUserInputOrDefault(),
                            dosage = binding.mtvDosageValue.getUserInputOrDefault(),
                            quantity = binding.mtvQuantityValue.getUserInputOrDefault(),
                            diagnosis = binding.mtvDiagnosisValue.getUserInputOrDefault(),
                        )
                    )
                )
            }
        }

        binding.ivBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }
    }

    override fun onValueChange(value: String) {
        when (fieldTag) {
            "dosage" -> binding.mtvDosageValue.text = value
            "quantity" -> binding.mtvQuantityValue.text = value
            "diagnosis" -> binding.mtvDiagnosisValue.text = value
            "prescribedByDoctor" -> binding.mtvPrescribedByDoctorValue.text = value
            "realizedInPharmacy" -> binding.mtvRealizedInPharmacyValue.text = value
            "pharmacist" -> binding.mtvPharmacistValue.text = value
        }
    }

    private fun openEnterValueBottomSheet() {
        val bSiV = BottomSheetInsertValue().apply {
            arguments = Bundle().apply {
                putString("title", title)
                putString("subtitle", subtitle)
            }
        }
        bSiV.setOnValueListener(this, fieldTag = fieldTag ?: "")
        bSiV.show(requireActivity().supportFragmentManager, bSiV.tag)
    }

    private fun setUpViewModels() {
        val healthcareRepository = HealthcareRepository()
        dataStoreRepository = DataStoreRepository(requireActivity())
        val healthcareViewModelFactory =
            HealthcareViewModelFactory(healthcareRepository, dataStoreRepository)
        healthcareViewModel = ViewModelProvider(requireActivity(), healthcareViewModelFactory)
            .get(HealthcareViewModel::class.java)

    }

    private fun subscribeToObservers() {


        healthcareViewModel.addMedicamentPrescribedLiveData.observe(
            viewLifecycleOwner,
            Observer { response ->
                if (response?.isSuccessful == true) {
                    Toast.makeText(requireActivity(), "${response.body()}", Toast.LENGTH_SHORT)
                        .show()

                    CommonUtils.openFragmentHome(
                        requireActivity(),
                        PrescribedListMedicamentsFragment()
                    )

                    healthcareViewModel.addMedicamentPrescribedLiveData.value = null
                }
            })

        healthcareViewModel.addMedicamentPrescribedErrorLiveData.observe(
            viewLifecycleOwner
        ) { response ->
            response?.let {
                Toast.makeText(requireActivity(), "$it", Toast.LENGTH_SHORT).show()
                CommonUtils.openFragmentHome(requireActivity(), PrescribedListMedicamentsFragment())
                Handler(Looper.getMainLooper()).postDelayed({
                    healthcareViewModel.addMedicamentPrescribedErrorLiveData.value = null
                }, 500)
            }
        }


        healthcareViewModel.updateMedicamenteLiveData.observe(
            viewLifecycleOwner,
            Observer { response ->
                response?.let {
                    if (it.isSuccessful) {
                        val data = it.body()
                        Toast.makeText(
                            requireActivity(),
                            "${getString(R.string.successful_save_data)} :$data",
                            Toast.LENGTH_SHORT
                        ).show()

                        healthcareViewModel.updateMedicamenteLiveData.value = null
                        CommonUtils.openFragmentHome(
                            requireActivity(),
                            PrescribedListMedicamentsFragment()
                        )
                    }
                }
            }
        )

        healthcareViewModel.updateMedicamenteErrorLiveData.observe(
            viewLifecycleOwner,
            Observer { response ->
                Toast.makeText(requireActivity(), "${getString(R.string.error)}: ${response}", Toast.LENGTH_SHORT).show()
                CommonUtils.openFragmentHome(requireActivity(), PrescribedListMedicamentsFragment())
            })
    }

    fun formatDate(inputDate: String): String {
        try {
            val inputDateFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
            val outputDateFormat = SimpleDateFormat("d.M.y", Locale.getDefault())
            val parsedDate = inputDateFormat.parse(inputDate)
            return if (parsedDate != null) {
                outputDateFormat.format(parsedDate)
            } else {
                inputDate
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return inputDate
        }
    }
    fun TextView.getUserInputOrDefault(defaultPlaceholder: String = getString(R.string.fragment_add_medicamente_prescibed_list_input)): String {
        val input = this.text.toString().trim()
        return if (input == defaultPlaceholder) "" else input
    }
}