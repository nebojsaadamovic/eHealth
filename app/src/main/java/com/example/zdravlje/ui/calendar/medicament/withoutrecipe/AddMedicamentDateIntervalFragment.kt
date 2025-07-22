package com.example.zdravlje.ui.calendar.medicament.withoutrecipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.zdravlje.data.model.request.calendar.AddEvent
import com.example.zdravlje.data.model.request.calendar.Interval
import com.example.zdravlje.data.model.request.calendar.Mandatory
import com.example.zdravlje.data.model.request.calendar.OnClickPayload
import com.example.zdravlje.data.model.request.calendar.Repeat
import com.example.zdravlje.data.model.response.dashboard.event_payload.MedicineEventPayload
import com.example.zdravlje.data.model.response.dashboard.future.Event
import com.example.zdravlje.data.model.response.healthcare.PrescribedListMedicamentResponse
import com.example.zdravlje.data.repository.CalendarRepository
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.calendar.CalendarFragment
import com.example.zdravlje.ui.calendar.CalendarViewModel
import com.example.zdravlje.ui.calendar.CalendarViewModelFactory
import com.example.zdravlje.ui.calendar.medicament.BottomSheetConfirmAddMedicamentCalendarFragment
import com.example.zdravlje.ui.calendar.personalActivity.BottomSheetIntervalHours
import com.example.zdravlje.ui.chat.scheduleCall.BottomSheetTimeFromFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentAddMedicamentDateIntervalBinding
import java.text.SimpleDateFormat
import java.util.Locale

class AddMedicamentDateIntervalFragment(
    private var medicine: PrescribedListMedicamentResponse? = null,
    var dateFrom: String,
    var dateTo: String,
    var dosage: String,
    private var eventObject: Event? = null,
    private var medicineEventPayload: MedicineEventPayload? = null,
    private var headerTitle: String? = null
) : BaseFragment(), BottomSheetTimeFromFragment.OnBottomSheetTimeFromListener,
    BottomSheetIntervalHours.OnBottomSheetTimeToListener,
    BottomSheetConfirmAddMedicamentCalendarFragment.OnConfirmListener {

    lateinit var binding: FragmentAddMedicamentDateIntervalBinding
    private lateinit var calendarViewModel: CalendarViewModel
    private lateinit var dataStoreRepository: DataStoreRepository

    var isIntervalSelected = false
    var isTimeFromSelected = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddMedicamentDateIntervalBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        subscribeToObservers()
        setupUI()
    }

    fun setupUI() {
        headerTitle?.let {
            binding.tvCalendar.text = it
            binding.mbNext.text = getString(R.string.edit)
        }
        if (eventObject != null) {
            binding.mtvActivityNameValue.text = eventObject!!.eventTitle
        } else {
            binding.mtvActivityNameValue.text = medicine?.medicamentProtectedName
        }

        binding.mtvDosageValue.text = dosage
        binding.tvITimeFromValue.text = dateFrom
        binding.tvTimeToValue.text = dateTo

        binding.tvTimeFromValue.setOnClickListener(View.OnClickListener {
            val bottomSheetTimeFromFragment = BottomSheetTimeFromFragment()
            bottomSheetTimeFromFragment.setListener(this)
            bottomSheetTimeFromFragment.show(
                requireActivity().supportFragmentManager,
                bottomSheetTimeFromFragment.tag
            )
        })

        binding.tvDateFromValue.setOnClickListener(View.OnClickListener {
            val bottomSheetIntervalFragment = BottomSheetIntervalHours()
            bottomSheetIntervalFragment.setTargetFragment(this, 0)
            bottomSheetIntervalFragment.show(
                requireActivity().supportFragmentManager,
                bottomSheetIntervalFragment.tag
            )
        })

        binding.mbNext.setOnClickListener() {
            val bottomSheet = BottomSheetConfirmAddMedicamentCalendarFragment(eventObject)
            bottomSheet.setConfirmListener(this)
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }

        binding.ivBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }
    }

    private fun clickConfirm() {
        val intervalText = binding.tvDateFromValue.text.toString()
        val intervalInMinutes = intervalText.toIntOrNull() ?: 1  // Postavi na 0 ako nije broj

        val repeat = Repeat(
            every = intervalInMinutes * 60 * 60,
            day = listOf()
        )

        val interval = Interval(
            from = formatDate(dateFrom) + " " + binding.tvTimeFromValue.text.toString(),
            to = formatDate(dateTo) + " 23:59",
            repeat = repeat
        )

        val onClickPayLoad: OnClickPayload
        if (eventObject != null) {
            onClickPayLoad =
                OnClickPayload(Mandatory(medicineEventPayload?.mandatory!!.lek_id, 0, dosage, "/"))
        } else {
            onClickPayLoad =
                OnClickPayload(Mandatory(medicine?.medicamentId!!.toInt(), 0, dosage, "/"))
        }

        val eveTitle = if (eventObject != null) {
            eventObject!!.eventTitle
        } else {
            medicine!!.medicamentProtectedName
        }

        val event = AddEvent(
            eve_title = eveTitle!!,
            eve_event_type_id = 1,
            interval = interval,
            eve_on_click_payload = onClickPayLoad
        )

        event.pin = getStoredPin()
        if (eventObject != null) {
            event.eve_id = eventObject?.eventId
            calendarViewModel.updateEventAndNotification(addEvent = event)
        } else {
            calendarViewModel.createEventAnNotification(addEvent = event)
        }

    }

    override fun onBottomSheetTimeFromSelected(value: String) {
        binding.tvTimeFromValue.text = value
        binding.tvTimeFromValue.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.text_color_8A8A8D
            )
        )

        isIntervalSelected = true
        if (isIntervalSelected && isTimeFromSelected) {
            binding.mbNext.alpha = 1.0f
            binding.mbNext.isEnabled = true
        }
    }

    override fun onBottomSheetTimeToSelected(value: String) {
        binding.tvDateFromValue.text = value
        binding.tvDateFromValue.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.text_color_8A8A8D
            )
        )

        isTimeFromSelected = true
        if (isIntervalSelected && isTimeFromSelected) {
            binding.mbNext.alpha = 1.0f
            binding.mbNext.isEnabled = true
        }
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

    private fun subscribeToObservers() {
        calendarViewModel.createEventAnNotificationLiveData.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.activity_personal_data_activity_completed),
                    Toast.LENGTH_SHORT
                ).show()
                val calendarFragment = CalendarFragment()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.calendar_options_container_view, calendarFragment)
                    .addToBackStack(null).commit()
            } else {
                Toast.makeText(
                    requireActivity(),
                    "${getString(R.string.error)}: ${response.body()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        calendarViewModel.getCreateEventAnNotificationErrorLiveData.observe(viewLifecycleOwner) { response ->
            Toast.makeText(
                requireActivity(),
                "${getString(R.string.error)}: ${response}",
                Toast.LENGTH_SHORT
            ).show()
        }

        calendarViewModel.getUpdateEventNotificationLiveData.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.activity_personal_data_activity_completed),
                    Toast.LENGTH_SHORT
                ).show()
                val calendarFragment = CalendarFragment()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.drug_fragment_container, calendarFragment)
                    .addToBackStack(null).commit()
            } else {
                Toast.makeText(requireActivity(), "Error", Toast.LENGTH_SHORT).show()
            }
        }

        calendarViewModel.getUpdateEventNotificationErrorLiveData.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.error),
                    Toast.LENGTH_SHORT
                ).show()
                val calendarFragment = CalendarFragment()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.drug_fragment_container, calendarFragment)
                    .addToBackStack(null).commit()
            } else {
                Toast.makeText(requireActivity(), getString(R.string.error), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    fun formatDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date)
    }

    override fun onConfirm() {
        clickConfirm()
    }
}