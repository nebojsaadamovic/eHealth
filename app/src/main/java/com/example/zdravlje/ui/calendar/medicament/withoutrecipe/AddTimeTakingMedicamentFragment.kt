package com.example.zdravlje.ui.calendar.medicament.withoutrecipe

import android.os.Bundle
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
import com.example.zdravlje.data.model.response.dashboard.event_payload.MedicineEventPayload
import com.example.zdravlje.data.model.response.dashboard.future.Event
import com.example.zdravlje.data.model.response.healthcare.PrescribedListMedicamentResponse
import com.example.zdravlje.data.repository.CalendarRepository
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.calendar.CalendarViewModel
import com.example.zdravlje.ui.calendar.CalendarViewModelFactory
import com.example.zdravlje.ui.calendar.medicament.BottomSheetConfirmAddMedicamentCalendarFragment
import com.example.zdravlje.ui.calendar.personalActivity.TimeActivityBottomSheetFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentAddTimeTakingMedicamentBinding
import java.text.SimpleDateFormat
import java.util.Locale

class AddTimeTakingMedicamentFragment(
    private var medicine: PrescribedListMedicamentResponse?=null,
    var dateFrom: String,
    var dateTo: String,
    private var dosage: String,
    private var eventObject: Event? = null,
    private var medicineEventPayload: MedicineEventPayload? = null,
    private var headerTitle: String  ?=null
) : BaseFragment(), TimeActivityBottomSheetFragment.OnTimeActivitySheetTimeFromListener,
    BottomSheetConfirmAddMedicamentCalendarFragment.OnConfirmListener {

    lateinit var binding: FragmentAddTimeTakingMedicamentBinding
    private lateinit var calendarViewModel: CalendarViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private val selectedTimes: MutableList<String> = mutableListOf()
    var isIcPlus = true
    var isIcPlus1 = true
    var isIcPlus2 = true
    var isTimeSelected = false
    var isTimeSelected1 = false
    var isTimeSelected2 = false
    var isTimeSelected3 = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddTimeTakingMedicamentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        subscribeToObservers()
        setupUI()
    }

    private fun setupUI() {
        headerTitle?.let {
            binding.mtvHeaderTitle.text = it
            binding.mbAdd.text = getString(R.string.edit)
        }

        if (eventObject != null) {
            binding.mtvMedicamentNameValue.text = eventObject!!.eventTitle
        } else {
            binding.mtvMedicamentNameValue.text = medicine?.medicamentProtectedName
        }

        binding.mtvDateFromValue.text = dateFrom
        binding.mtvDateToValue.text = dateTo
        binding.mtvDosageValue.text = dosage

        binding.tvChooseTime.setOnClickListener() {
            isTimeSelected = true
            isTimeSelected1 = false
            isTimeSelected2 = false
            isTimeSelected3 = false
            val timeActivityBottomSheet = TimeActivityBottomSheetFragment()
            timeActivityBottomSheet.setTargetFragment(this, 0)
            timeActivityBottomSheet.show(parentFragmentManager, timeActivityBottomSheet.tag)
        }
        binding.ivAddTimeMedicament.setOnClickListener() {
            if (isIcPlus) {
                binding.ivAddTimeMedicament.setImageResource(R.drawable.ic_remove_time_activity)
                binding.clAddTimeTakeMedicament1.visibility = View.VISIBLE
                isIcPlus = false
            } else {
                binding.ivAddTimeMedicament.setImageResource(R.drawable.ic_add_time_activity)
                binding.tvChooseTime.text = getString(R.string.select)
                isIcPlus = true
            }
        }

        binding.tvChooseTime1.setOnClickListener() {
            isTimeSelected = false
            isTimeSelected1 = true
            isTimeSelected2 = false
            isTimeSelected3 = false
            val timeActivityBottomSheet = TimeActivityBottomSheetFragment()
            timeActivityBottomSheet.setTargetFragment(this, 0)
            timeActivityBottomSheet.show(parentFragmentManager, timeActivityBottomSheet.tag)
        }
        binding.ivAddTimeMedicament1.setOnClickListener() {
            if (isIcPlus1) {
                binding.ivAddTimeMedicament1.setImageResource(R.drawable.ic_remove_time_activity)
                binding.clAddTimeTakeMedicament2.visibility = View.VISIBLE
                isIcPlus1 = false
            } else {
                binding.clAddTimeTakeMedicament1.visibility = View.GONE
                binding.ivAddTimeMedicament1.setImageResource(R.drawable.ic_add_time_activity)
                binding.tvChooseTime1.text = getString(R.string.select)
                isIcPlus1 = true
            }
        }

        binding.tvChooseTime2.setOnClickListener() {
            isTimeSelected = false
            isTimeSelected1 = false
            isTimeSelected2 = true
            isTimeSelected3 = false
            val timeActivityBottomSheet = TimeActivityBottomSheetFragment()
            timeActivityBottomSheet.setTargetFragment(this, 0)
            timeActivityBottomSheet.show(parentFragmentManager, timeActivityBottomSheet.tag)
        }

        binding.ivAddTimeMedicament2.setOnClickListener() {
            if (isIcPlus2) {
                binding.clAddTimeTakeMedicament3.visibility = View.VISIBLE
                binding.ivAddTimeMedicament2.setImageResource(R.drawable.ic_remove_time_activity)
                binding.ivAddTimeMedicament3.setImageResource(R.drawable.ic_remove_time_activity)
                isIcPlus2 = false
            } else {
                binding.clAddTimeTakeMedicament2.visibility = View.GONE
                binding.ivAddTimeMedicament2.setImageResource(R.drawable.ic_add_time_activity)
                binding.tvChooseTime2.text = getString(R.string.select)
                isIcPlus2 = true
            }
        }

        binding.ivAddTimeMedicament3.setOnClickListener() {
            binding.clAddTimeTakeMedicament3.visibility = View.GONE
            binding.tvChooseTime3.text = getString(R.string.select)
        }

        binding.tvChooseTime3.setOnClickListener() {
            isTimeSelected = false
            isTimeSelected1 = false
            isTimeSelected2 = false
            isTimeSelected3 = true
            val timeActivityBottomSheet = TimeActivityBottomSheetFragment()
            timeActivityBottomSheet.setTargetFragment(this, 0)
            timeActivityBottomSheet.show(parentFragmentManager, timeActivityBottomSheet.tag)
        }

        binding.ivBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }

        binding.mbAdd.setOnClickListener() {
            val bottomSheet = BottomSheetConfirmAddMedicamentCalendarFragment(eventObject)
            bottomSheet.setConfirmListener(this)
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }
    }

    private fun clickConfirm() {
        val repeat = Repeat(
            every = 0,
            day = selectedTimes
        )

        val interval = Interval(
            from = formatDate(dateFrom) + " " + binding.tvChooseTime.text.toString(),
            to = formatDate(dateTo) + " 23:59",
            repeat = repeat
        )

        val onClickPayLoad: OnClickPayload
        if (eventObject != null) {
            onClickPayLoad =
                OnClickPayload(Mandatory(medicineEventPayload?.mandatory!!.lek_id, 0, dosage, "/"))
        } else {
            onClickPayLoad = OnClickPayload(Mandatory(medicine?.medicamentId!!.toInt(), 0, dosage, "/"))
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

        event.pin=getStoredPin()
        if (eventObject != null) {
            event.eve_id = eventObject?.eventId
            calendarViewModel.updateEventAndNotification(addEvent = event)
        } else {
            calendarViewModel.createEventAnNotification(addEvent = event)
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
            } else {
                Toast.makeText(
                    requireActivity(),
                    "${getString(R.string.error)}:${response.body()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        calendarViewModel.getCreateEventAnNotificationErrorLiveData.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                Toast.makeText(
                    requireActivity(),
                    "${getString(R.string.error)}: ${response.body()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onTimeActivitySelected(value: String) {
        if(!value.isNullOrEmpty()){
            binding.mbAdd.alpha = 1.0f
            binding.mbAdd.isEnabled = true
        }

        if (isTimeSelected) {
            binding.tvChooseTime.text = value
            if (binding.tvChooseTime.text != getString(R.string.select)) {
                selectedTimes.add(value)
            }
        }
        if (isTimeSelected1) {
            binding.tvChooseTime1.text = value
            if (binding.tvChooseTime1.text != getString(R.string.select)) {
                selectedTimes.add(value)
            }
        }
        if (isTimeSelected2) {
            binding.tvChooseTime2.text = value
            if (binding.tvChooseTime2.text != getString(R.string.select)) {
                selectedTimes.add(value)
            }
        }
        if (isTimeSelected3) {
            binding.tvChooseTime3.text = value
            if (binding.tvChooseTime3.text != getString(R.string.select)) {
                selectedTimes.add(value)
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