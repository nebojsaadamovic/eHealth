package com.example.zdravlje.ui.healthcare.prescribedlistmedicaments.medicamentoptions

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.zdravlje.data.model.request.calendar.AddEvent
import com.example.zdravlje.data.model.request.calendar.Interval
import com.example.zdravlje.data.model.request.calendar.Mandatory
import com.example.zdravlje.data.model.request.calendar.OnClickPayload
import com.example.zdravlje.data.model.request.calendar.Repeat
import com.example.zdravlje.data.model.response.healthcare.PatmedNotes
import com.example.zdravlje.data.model.response.healthcare.PrescribedListMedicamentResponse
import com.example.zdravlje.data.repository.CalendarRepository
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.calendar.CalendarViewModel
import com.example.zdravlje.ui.calendar.CalendarViewModelFactory
import com.example.zdravlje.ui.chat.scheduleCall.BottomSheetTimeToFragment
import com.example.zdravlje.ui.healthcare.prescribedlistmedicaments.PrescribedMedicamentOptionsFragment
import com.example.zdravlje.utilities.DateUtils
import com.google.gson.Gson
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentAddReminderBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddReminderFragment(
    private val prescribedObject: PrescribedListMedicamentResponse
) : BaseFragment(), BottomSheetDosageFragment.OnValueListener,
    BottomSheetTimeToFragment.OnBottomSheetTimeToListener {

    private lateinit var binding: FragmentAddReminderBinding
    private lateinit var calendarViewModel: CalendarViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var dateTo: String
    private lateinit var dateFrom: String
    private  var timeFrom: String ?= "00:00"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddReminderBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        setupUI()
    }

    private fun setViewModels() {
        val calendarRepository = CalendarRepository()
        dataStoreRepository = DataStoreRepository(requireActivity())
        val calendarViewModelFactory =
            CalendarViewModelFactory(calendarRepository, dataStoreRepository)

        calendarViewModel = ViewModelProvider(
            requireActivity(),
            calendarViewModelFactory
        ).get(CalendarViewModel::class.java)
    }

    private fun setupUI() {
        if (prescribedObject != null) {
            binding.mtvNameMedicamenteValue.text = prescribedObject.medicamentProtectedName
            val gson = Gson()
            val patmedNotesJson = prescribedObject.patmedNotes
            val payload: PatmedNotes? = gson.fromJson(patmedNotesJson, PatmedNotes::class.java)
            binding.mtvDosageValue.text =  if (payload?.dosage.isNullOrBlank()) "/" else payload?.dosage
        } else {
            return
        }

        binding.mtvDosageValue.setOnClickListener() {
            val dosage = getString(R.string.fragment_add_reminder_dosage)
            val bottomSheetDosagePeriodFragment =
                BottomSheetDosageFragment(dosage, BottomSheetDosageFragment.FieldType.DOSAGE)
            bottomSheetDosagePeriodFragment.setOnValueListener(this)
            bottomSheetDosagePeriodFragment.show(
                parentFragmentManager,
                bottomSheetDosagePeriodFragment.tag
            )
            checkIfSaveEnabled()
        }

        binding.mtvEveryHourValue.isEnabled = binding.rbEveryHour.isChecked
        binding.rbEveryHour.setOnCheckedChangeListener { _, isChecked ->
            binding.mtvEveryHourValue.isEnabled = isChecked
        }
        binding.mtvEveryHourValue.setOnClickListener {
            val hours = getString(R.string.fragment_add_reminder_hours)
            if (binding.rbEveryHour.isChecked) {
                val bottomSheetDosagePeriodFragment = BottomSheetDosageFragment(
                    hours,
                    BottomSheetDosageFragment.FieldType.EVERY_HOUR
                )
                bottomSheetDosagePeriodFragment.setOnValueListener(this)
                bottomSheetDosagePeriodFragment.show(
                    parentFragmentManager,
                    bottomSheetDosagePeriodFragment.tag
                )
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.add_reminder_fragment_option_disabled),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.mtvTermConsumationValue.setOnClickListener() {
            val bottomSheetFragment = BottomSheetTimeToFragment()
            bottomSheetFragment.setTargetFragment(this, 0)
            bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
        }

        binding.mtvPeriodFromValue.setOnClickListener() {
            DateUtils.showDatePickerDialog(requireContext(), binding.mtvPeriodFromValue) {
                dateFrom = binding.mtvPeriodFromValue.text.toString()
                checkIfSaveEnabled()
            }
        }

        binding.mtvPeriodToValue.setOnClickListener {
            DateUtils.showDatePickerDialog(requireContext(), binding.mtvPeriodToValue) {
                val selectedDateTo = binding.mtvPeriodToValue.text.toString()
                if (isDateToValid(dateFrom, selectedDateTo)) {
                    dateTo = selectedDateTo
                    checkIfSaveEnabled()
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.add_reminder_fragment_end_date_greater_than_start_date),
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.mtvPeriodFromValue.text =
                        getString(R.string.add_reminder_fragment_enter_date)
                    binding.mtvPeriodToValue.text =
                        getString(R.string.add_reminder_fragment_enter_date)
                }
            }
        }

        binding.mtvSave.setOnClickListener {
            val hours = binding.mtvEveryHourValue.text.toString()
            val intervalInHours = hours.toIntOrNull() ?: 0

            val intervalInSeconds = intervalInHours * 3600

            val repeat = Repeat(
                every = intervalInSeconds,
                day = listOf()
            )

            var prepisan_flag = prescribedObject.prescribed!!.toInt()
            if (prepisan_flag == 1) {
                prepisan_flag = 2
            }

            val onClickPayload = OnClickPayload(
                mandatory = Mandatory(
                    lek_id = prescribedObject.medicamentId!!.toInt(),
                    prepisan_flag = prepisan_flag,
                    doziranje = binding.mtvDosageValue.text.toString(),
                    erx_doziranje = prescribedObject.strength.toString()
                )
            )

            val interval = Interval(
                from = formatDate(dateFrom) + timeFrom,
                to = formatDate(dateTo) + " 23:59",
                repeat = repeat
            )
            val event = AddEvent(
                eve_title = prescribedObject.medicamentName!!,
                eve_event_type_id = 1,
                eve_on_click_payload = onClickPayload,
                interval = interval
            )
            try {
                event.pin = getStoredPin()
                calendarViewModel.createEventAnNotification(addEvent = event)
                val fragment = PrescribedMedicamentOptionsFragment(prescribedObject)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.home_fragment_container_view, fragment)
                    .addToBackStack(null).commit()

                Toast.makeText(
                    requireContext(),
                    getString(R.string.add_reminder_fragment_data_saved_successfully),
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.add_reminder_fragment_data_save_failed),
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(
                    getString(R.string.error),
                    "${getString(R.string.add_reminder_fragment_error_saving_data)}: ${e.message}"
                )
            }
        }

        binding.ivBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }
    }

    private fun checkIfSaveEnabled() {
        val isDosageValueEntered =
            binding.mtvDosageValue.text.toString() != getString(R.string.add_reminder_fragment_enter_value)
        val isPeriodFromEntered =
            binding.mtvPeriodFromValue.text.toString() != getString(R.string.add_reminder_fragment_enter_date)
        val isPeriodToEntered =
            binding.mtvPeriodToValue.text.toString() != getString(R.string.add_reminder_fragment_enter_date)

        val isSaveEnabled = isDosageValueEntered && isPeriodFromEntered && isPeriodToEntered

        binding.mtvSave.isEnabled = isSaveEnabled
        binding.mtvSave.alpha = if (isSaveEnabled) 1.0f else 0.2f
    }

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("EEEE dd.MM.yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(Date())
        return formattedDate.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    override fun onBottomSheetTimeToSelected(value: String) {
        binding.mtvTermConsumationValue.text = value
        timeFrom = value
    }

    private fun isDateToValid(dateFrom: String, dateTo: String): Boolean {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return try {
            val from = dateFormat.parse(dateFrom)
            val to = dateFormat.parse(dateTo)
            to != null && from != null && !to.before(from)
        } catch (e: ParseException) {
            false
        }
    }

    override fun onValueChange(value: String, fieldType: BottomSheetDosageFragment.FieldType) {
        if (fieldType == BottomSheetDosageFragment.FieldType.DOSAGE) {
            binding.mtvDosageValue.text = value
        }
        if (fieldType == BottomSheetDosageFragment.FieldType.EVERY_HOUR) {
            binding.mtvEveryHourValue.text = value
        }
    }

    fun formatDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date)
    }
}