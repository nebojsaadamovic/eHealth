package com.example.zdravlje.ui.healthcare.medicalcarton

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.healthcare.HealthcareViewModel
import com.example.zdravlje.ui.healthcare.HealthcareViewModelFactory
import com.example.zdravlje.ui.healthcare.prescribedlistmedicaments.PrescribedListMedicamentsFragment
import com.example.zdravlje.utilities.CommonUtils
import com.example.zdravlje.utilities.DateUtils
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentMedicalCartonDateBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class MedicalCartonDateFragment : BaseFragment() {

    private lateinit var binding: FragmentMedicalCartonDateBinding
    private lateinit var healthcareViewModel: HealthcareViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private var isTimeToSelected = false
    private var isTimeFromSelected = false
    private var dateFromStr: String? = null
    private var dateToStr: String? = null

    private var dateFromStrE: String? = null
    private var dateToStrE: String? = null
    private var className: String? = null

    companion object {
        fun newInstance(className: String?=null): MedicalCartonDateFragment {
            val fragment = MedicalCartonDateFragment()
            val args = Bundle()
            args.putString("class_name", className)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMedicalCartonDateBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModels()
        arguments?.let {
            className = it.getString("class_name")
        }

        if (className == "PrescribedListMedicamentsFragment") {

            if (healthcareViewModel.dateOllList.value.isNullOrEmpty()) {
                val dateFormat = SimpleDateFormat("dd/M/yyyy", Locale.getDefault())
                val calendarTo = Calendar.getInstance()
                val calendarFrom = Calendar.getInstance().apply {
                    add(Calendar.MONTH, -6)
                }

                dateFromStr = dateFormat.format(calendarFrom.time)
                dateToStr = dateFormat.format(calendarTo.time)
                binding.mtvDateFromValue.text = dateFromStr
                binding.mtvDateToValue.text = dateToStr

                binding.mbSearch.isEnabled = true
                binding.mbSearch.alpha = 1.0f
                healthcareViewModel.dateOllList.value = null

                healthcareViewModel.dateOllList.value = listOf(
                    dateFormat.format(calendarFrom.time) ?: "",
                    dateFormat.format(calendarTo.time) ?: ""
                )

            } else {
                dateFromStr = healthcareViewModel.dateOllList.value?.get(0)
                dateToStr = healthcareViewModel.dateOllList.value?.get(1)
                binding.mtvDateToValue.text = dateFromStr
                binding.mtvDateFromValue.text = dateToStr
            }

        } else {
            if (healthcareViewModel.dateECartonList.value.isNullOrEmpty()) {
                val dateFormat = SimpleDateFormat("dd/M/yyyy", Locale.getDefault())
                val calendarTo = Calendar.getInstance()
                val calendarFrom = Calendar.getInstance().apply {
                    add(Calendar.MONTH, -6)
                }

                binding.mtvDateToValue.text = dateFormat.format(calendarTo.time)
                binding.mtvDateFromValue.text = dateFormat.format(calendarFrom.time)
                binding.mbSearch.isEnabled = true
                binding.mbSearch.alpha = 1.0f
                healthcareViewModel.dateECartonList.value = null
                healthcareViewModel.dateECartonList.value = listOf(
                    dateFormat.format(calendarFrom.time) ?: "",
                    dateFormat.format(calendarTo.time) ?: ""
                )

            } else {
                dateFromStrE = healthcareViewModel.dateECartonList.value?.get(0)
                dateToStrE = healthcareViewModel.dateECartonList.value?.get(1)
                binding.mtvDateToValue.text = dateFromStrE
                binding.mtvDateFromValue.text = dateFromStrE
            }
        }

        if (dateFromStr != null && dateToStr != null) {
            binding.mtvDateFromValue.text = dateFromStr
            binding.mtvDateToValue.text = dateToStr
            binding.mbSearch.alpha = 1.0f
            binding.mbSearch.isEnabled = true
            isTimeToSelected = true
            isTimeFromSelected = true
        }
        if (dateFromStrE != null && dateToStrE != null) {
            binding.mtvDateFromValue.text = dateFromStrE
            binding.mtvDateToValue.text = dateToStrE
            binding.mbSearch.alpha = 1.0f
            binding.mbSearch.isEnabled = true
            isTimeToSelected = true
            isTimeFromSelected = true
        }

        setupUI()
    }

    fun setupUI() {
        if (className == "PrescribedListMedicamentsFragment") {
            binding.mtvDateFromValue.setOnClickListener {
                DateUtils.showDatePickerDialog(
                    requireContext(),
                    binding.mtvDateFromValue
                ) { selectDate ->
                    binding.mtvDateFromValue.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.text_color_8A8A8D
                        )
                    )
                    isTimeFromSelected = true

                    if (isTimeFromSelected && isTimeToSelected) {
                        dateFromStr = selectDate
                        dateToStr = binding.mtvDateToValue.text.toString()

                        val dateFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())

                        try {
                            val dateFrom = dateFormat.parse(dateFromStr)
                            val dateTo = dateFormat.parse(dateToStr)

                            if (dateFrom != null && dateTo != null && dateFrom.after(dateTo)) {
                                Toast.makeText(
                                    requireActivity(),
                                    " ${getString(R.string.fragment_medical_carton_date_dates_toast)}" +
                                            " $dateFromStr i $dateToStr " +
                                            " ${getString(R.string.fragment_medical_carton_date_toast_msg)}.",
                                    Toast.LENGTH_SHORT
                                ).show()

                                binding.mtvDateFromValue.setTextColor(
                                    ContextCompat.getColor(requireContext(), R.color.base_blue_color)
                                )
                                binding.mtvDateToValue.setTextColor(
                                    ContextCompat.getColor(requireContext(), R.color.base_blue_color)
                                )
                                binding.mbSearch.alpha = 0.3f
                                binding.mbSearch.isEnabled = false
                                return@showDatePickerDialog
                            }

                            binding.mtvDateToValue.setTextColor(
                                ContextCompat.getColor(requireContext(), R.color.text_color_8A8A8D)
                            )
                            binding.mtvDateFromValue.setTextColor(
                                ContextCompat.getColor(requireContext(), R.color.text_color_8A8A8D)
                            )
                            binding.mbSearch.alpha = 1.0f
                            binding.mbSearch.isEnabled = true

                        } catch (e: Exception) {
                            Toast.makeText(
                                requireActivity(),
                                getString(R.string.fragment_medical_carton_date_error_processing_date),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

            binding.mtvDateToValue.setOnClickListener {
                DateUtils.showDatePickerDialog(requireContext(), binding.mtvDateToValue) { selectDate ->
                    binding.mtvDateToValue.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.text_color_8A8A8D)
                    )

                    isTimeToSelected = true

                    if (isTimeFromSelected && isTimeToSelected) {
                        dateFromStr = binding.mtvDateFromValue.text.toString()
                        dateToStr = selectDate

                        val dateFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())

                        try {
                            val dateFrom = dateFormat.parse(dateFromStr)
                            val dateTo = dateFormat.parse(dateToStr)

                            if (dateFrom != null && dateTo != null && dateFrom.after(dateTo)) {
                                Toast.makeText(
                                    requireActivity(),
                                    " ${getString(R.string.fragment_medical_carton_date_dates_toast)}" +
                                            " $dateFromStr i $dateToStr " +
                                            " ${getString(R.string.fragment_medical_carton_date_toast_msg)}.",
                                    Toast.LENGTH_SHORT
                                ).show()

                                binding.mtvDateFromValue.setTextColor(
                                    ContextCompat.getColor(requireContext(), R.color.base_blue_color)
                                )
                                binding.mtvDateToValue.setTextColor(
                                    ContextCompat.getColor(requireContext(), R.color.base_blue_color)
                                )
                                binding.mbSearch.alpha = 0.2f
                                binding.mbSearch.isEnabled = false
                                return@showDatePickerDialog
                            }

                            binding.mtvDateToValue.setTextColor(
                                ContextCompat.getColor(requireContext(), R.color.text_color_8A8A8D)
                            )
                            binding.mtvDateFromValue.setTextColor(
                                ContextCompat.getColor(requireContext(), R.color.text_color_8A8A8D)
                            )
                            binding.mbSearch.alpha = 1.0f
                            binding.mbSearch.isEnabled = true

                        } catch (e: Exception) {
                            Toast.makeText(
                                requireActivity(),
                                getString(R.string.fragment_medical_carton_date_error_processing_date),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

            binding.mbSearch.setOnClickListener {
               healthcareViewModel.dateOllList.value = listOf(dateFromStr?: "", dateToStr?: "")
                CommonUtils.openFragmentHome(
                    requireActivity(),
                    PrescribedListMedicamentsFragment().apply {
                        arguments = Bundle().apply {
                            putBoolean("filterDate", true)
                        }
                    }
                )
            }

            binding.mbCancelSearch.setOnClickListener {
                val defaultValue =
                    getString(R.string.fragment_calendar_personal_activity_date_choose_label)
                binding.mtvDateFromValue.text = defaultValue
                binding.mtvDateFromValue.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.base_blue_color
                    )
                )
                binding.mtvDateToValue.text = defaultValue
                binding.mtvDateToValue.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.base_blue_color
                    )
                )
                binding.mbSearch.alpha = 0.3f
                binding.mbSearch.isEnabled = false
                dateFromStr = LocalDate.now().minusMonths(6).format(formatter)
                dateToStr = LocalDate.now().format(formatter)
                healthcareViewModel.dateOllList.value = listOf(dateFromStr?: "", dateToStr?: "")
                CommonUtils.openFragmentHome(requireActivity(), PrescribedListMedicamentsFragment().apply {
                    arguments = Bundle().apply {
                        putBoolean("filterDate", false)
                    }
                })
            }

            binding.ivBack.setOnClickListener {
                if (healthcareViewModel.dateOllList.value!=null) {
                    requireActivity().onBackPressed().apply {
                        arguments = Bundle().apply {
                            putBoolean("filterDate", true)
                        }
                    }

                }else{
                    requireActivity().onBackPressed().apply {
                        healthcareViewModel.dateOllList.value=null
                        arguments = Bundle().apply {
                            putBoolean("filterDate", false)
                        }
                    }
                }
            }

        } else {
            binding.ivBack.setOnClickListener {
                MedicalCartonFragment.SharedCartonData.selectedCarton = "MedicalCarton"
                requireActivity().onBackPressed()
            }

            binding.mtvDateFromValue.setOnClickListener {
                DateUtils.showDatePickerDialog(
                    requireContext(),
                    binding.mtvDateFromValue
                ) { selectDate ->
                    binding.mtvDateFromValue.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.text_color_8A8A8D
                        )
                    )
                    isTimeFromSelected = true

                    if (isTimeFromSelected && isTimeToSelected) {
                        dateFromStrE = selectDate
                        dateToStrE = binding.mtvDateToValue.text.toString()

                        val dateFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())

                        try {
                            val dateFrom = dateFormat.parse(dateFromStrE)
                            val dateTo = dateFormat.parse(dateToStrE)

                            if (dateFrom != null && dateTo != null && dateFrom.after(dateTo)) {
                                Toast.makeText(
                                    requireActivity(),
                                    " ${getString(R.string.fragment_medical_carton_date_dates_toast)}" +
                                            " $dateFromStrE i $dateToStrE " +
                                            " ${getString(R.string.fragment_medical_carton_date_toast_msg)}.",
                                    Toast.LENGTH_SHORT
                                ).show()

                                binding.mtvDateFromValue.setTextColor(
                                    ContextCompat.getColor(requireContext(), R.color.base_blue_color)
                                )
                                binding.mtvDateToValue.setTextColor(
                                    ContextCompat.getColor(requireContext(), R.color.base_blue_color)
                                )
                                binding.mbSearch.alpha = 0.3f
                                binding.mbSearch.isEnabled = false
                                return@showDatePickerDialog
                            }

                            binding.mtvDateToValue.setTextColor(
                                ContextCompat.getColor(requireContext(), R.color.text_color_8A8A8D)
                            )
                            binding.mtvDateFromValue.setTextColor(
                                ContextCompat.getColor(requireContext(), R.color.text_color_8A8A8D)
                            )
                            binding.mbSearch.alpha = 1.0f
                            binding.mbSearch.isEnabled = true

                        } catch (e: Exception) {
                            Toast.makeText(
                                requireActivity(),
                                getString(R.string.fragment_medical_carton_date_error_processing_date),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

            binding.mtvDateToValue.setOnClickListener {
                DateUtils.showDatePickerDialog(requireContext(), binding.mtvDateToValue) { selectDate ->
                    binding.mtvDateToValue.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.text_color_8A8A8D)
                    )

                    isTimeToSelected = true

                    if (isTimeFromSelected && isTimeToSelected) {
                        dateFromStrE = binding.mtvDateFromValue.text.toString()
                        dateToStrE = selectDate

                        val dateFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())

                        try {
                            val dateFrom = dateFormat.parse(dateFromStrE)
                            val dateTo = dateFormat.parse(dateToStrE)

                            if (dateFrom != null && dateTo != null && dateFrom.after(dateTo)) {
                                Toast.makeText(
                                    requireActivity(),
                                    " ${getString(R.string.fragment_medical_carton_date_dates_toast)}" +
                                            " $dateFromStrE i $dateToStrE " +
                                            " ${getString(R.string.fragment_medical_carton_date_toast_msg)}.",
                                    Toast.LENGTH_SHORT
                                ).show()

                                binding.mtvDateFromValue.setTextColor(
                                    ContextCompat.getColor(requireContext(), R.color.base_blue_color)
                                )
                                binding.mtvDateToValue.setTextColor(
                                    ContextCompat.getColor(requireContext(), R.color.base_blue_color)
                                )
                                binding.mbSearch.alpha = 0.2f
                                binding.mbSearch.isEnabled = false
                                return@showDatePickerDialog
                            }

                            binding.mtvDateToValue.setTextColor(
                                ContextCompat.getColor(requireContext(), R.color.text_color_8A8A8D)
                            )
                            binding.mtvDateFromValue.setTextColor(
                                ContextCompat.getColor(requireContext(), R.color.text_color_8A8A8D)
                            )
                            binding.mbSearch.alpha = 1.0f
                            binding.mbSearch.isEnabled = true

                        } catch (e: Exception) {
                            Toast.makeText(
                                requireActivity(),
                                getString(R.string.fragment_medical_carton_date_error_processing_date),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

            binding.mbSearch.setOnClickListener {
                healthcareViewModel.dateECartonList.value = listOf(dateFromStrE?: "", dateToStrE?: "")
                CommonUtils.openFragmentHome(requireActivity(), MedicalCartonFragment(className = "MedicalCartonDateFragment"))
            }

            binding.mbCancelSearch.setOnClickListener {
                val defaultValue = getString(R.string.fragment_calendar_personal_activity_date_choose_label)
                binding.mtvDateFromValue.text = defaultValue
                binding.mtvDateFromValue.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.base_blue_color
                    )
                )

                binding.mtvDateToValue.text = defaultValue
                binding.mtvDateToValue.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.base_blue_color
                    )
                )

                binding.mbSearch.alpha = 0.3f
                binding.mbSearch.isEnabled = false
                healthcareViewModel.dateECartonList.value = null
                CommonUtils.openFragmentHome(requireActivity(), MedicalCartonFragment())
            }
        }
    }

    private fun setViewModels() {
        val healthcareRepository = HealthcareRepository()
        dataStoreRepository = DataStoreRepository(requireActivity())
        val healthcareViewModelFactory =
            HealthcareViewModelFactory(healthcareRepository, dataStoreRepository)
        healthcareViewModel = ViewModelProvider(
            requireActivity(), healthcareViewModelFactory
        ).get(HealthcareViewModel::class.java)
    }
}