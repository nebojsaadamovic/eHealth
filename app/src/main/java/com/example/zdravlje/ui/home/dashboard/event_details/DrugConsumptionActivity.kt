package com.example.zdravlje.ui.home.dashboard.event_details

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.zdravlje.data.model.request.calendar.DeleteEvent
import com.example.zdravlje.data.model.response.dashboard.event_payload.MedicineEventPayload
import com.example.zdravlje.data.model.response.dashboard.future.Event
import com.example.zdravlje.data.repository.CalendarRepository
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.services.avc.SampleApplication.Companion.context
import com.example.zdravlje.ui.base.BaseActivity
import com.example.zdravlje.ui.calendar.CalendarViewModel
import com.example.zdravlje.ui.calendar.CalendarViewModelFactory
import com.example.zdravlje.ui.calendar.apointment.BottomSheetDialogConfirmFragment
import com.example.zdravlje.ui.calendar.nextOnSchedule.BottomSheetConfirmDeleteReminderFragment
import com.example.zdravlje.ui.chat.scheduleCall.BottomSheetTimeFromFragment
import com.example.zdravlje.utilities.DateUtils
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.ActivityDrugComsumptionBinding

class DrugConsumptionActivity : BaseActivity(),
    BottomSheetConfirmDeleteReminderFragment.OnCheckedListener,
    BottomSheetTimeFromFragment.OnBottomSheetTimeFromListener {

    lateinit var binding: ActivityDrugComsumptionBinding

    private lateinit var calendarViewModel: CalendarViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var event: Event
    private var className: String ?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrugComsumptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //   this.window.navigationBarColor = ContextCompat.getColor(this, R.color.white)
        setViewModels()
        subscribeToObservers()

        val item = intent.getSerializableExtra("medicineEventPayload") as? MedicineEventPayload
        event = (intent.getSerializableExtra("event") as? Event)!!
        className = (intent.getStringExtra("class_name"))

        if (item != null && event != null) {
            setupUI(item, event)
        }
    }

    private fun setViewModels() {
        val calendarRepository = CalendarRepository()
        dataStoreRepository = DataStoreRepository(this)
        val calendarViewModelFactory = CalendarViewModelFactory(
            calendarRepository, dataStoreRepository
        )
        calendarViewModel = ViewModelProvider(
            this, calendarViewModelFactory
        ).get(CalendarViewModel::class.java)
    }

    private fun subscribeToObservers() {
        calendarViewModel.getDeleteEventLiveData.observe(this) { response ->
            if (response.isSuccessful) {
                Toast.makeText(
                    this,
                    getString(R.string.data_deleted_successfully),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }

        calendarViewModel.getUpdateEventLiveData.observe(this) { response ->
            if (response.isSuccessful) {
                Toast.makeText(context, R.string.toast_data_save, Toast.LENGTH_SHORT).show()
                finish()
            }
        }

    }

    fun setupUI(medicineEventPayload: MedicineEventPayload, event: Event) {
        if (event.isFuture()) {
            if (className == "HomeFragment"){
                binding.mbChangeAllInArray.visibility = View.GONE
                binding.mtvSave.visibility =  View.GONE
                binding.ivDelete.visibility = View.GONE

            }else{
                binding.mtvTimeConsumptionValue.isClickable = true
                binding.mtvTimeConsumptionValue.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.base_blue_color
                    )
                )
                binding.mtvTimeConsumptionValue.setOnClickListener() {
                    val bottomSheetTimeFromFragment = BottomSheetTimeFromFragment()
                    bottomSheetTimeFromFragment.setListener(this)
                    bottomSheetTimeFromFragment.show(
                        supportFragmentManager,
                        bottomSheetTimeFromFragment.tag
                    )
                }

                binding.ivDelete.setOnClickListener() {
                    val bottomSheet = BottomSheetConfirmDeleteReminderFragment()
                    bottomSheet.setCheckedListener(this)
                    bottomSheet.setTextSource("DeleteReminder-Drug consumption")
                    bottomSheet.show(supportFragmentManager, bottomSheet.tag)
                }
            }
        }

        if (event.isPastTime()) {
            binding.mbChangeAllInArray.visibility = View.GONE
        }

        binding.dateTitle.text = event.eventStartTime?.let { DateUtils.getDateWithWeekName(it) }
        binding.mtvNameValue.text = event.eventTitle

        binding.mtvTimeFrom.text = event.eventStartTime?.let { DateUtils.getFormattedDate(it) }
        binding.mtvTimeTo.text = event.eventEndTime?.let { DateUtils.getFormattedDate(it) }
        binding.mtvDescValue.text = medicineEventPayload.mandatory.doziranje
        binding.mtvTimeConsumptionValue.text = event.eventStartTime?.let {
            DateUtils.getHoursFromDate(
                it
            )
        }

        val title = "drugConsumptionActivity"
        binding.mbChangeAllInArray.setOnClickListener() {
            val bundle = Bundle().apply {
                putSerializable("medicine_event_payload", medicineEventPayload)
                putSerializable("event", event)
                putString("title", title)
            }

            val content =
                getString(R.string.activity_drug_consumption_enter_consumption_content_bottom_sheet)
            val header =
                getString(R.string.activity_drug_consumption_enter_consumption_header_bottom_sheet)

            val bsDcf = BottomSheetDialogConfirmFragment().apply {
                arguments = Bundle().apply {
                    putString("title", "drugConsumptionActivity")
                    putString("header", header)
                    putString("header_title", "Izmena dodavanja leka")
                    putString("content", content)
                    putBundle("bundle", bundle)
                }
            }
            bsDcf.show(supportFragmentManager, bsDcf.tag)
        }

        binding.ivBack.setOnClickListener {
            this.finish()
        }

        binding.mtvSave.setOnClickListener {
            val consumptionTime = binding.mtvTimeConsumptionValue.text.toString()
            if (consumptionTime.isNotEmpty()) {
                try {
                    val updateEvent = Event(
                        eventId = event.eventId,
                        eventStartTime = consumptionTime,
                        pin = getStoredPin()
                    )
                    calendarViewModel.updateEvent(updateEvent)
                } catch (e: Exception) {
                    Toast.makeText(
                        applicationContext,
                        e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.activity_drug_consumption_enter_consumption_time),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onBottomSheetTimeFromSelected(value: String) {
        binding.mtvTimeConsumptionValue.text = value
    }

    override fun onChecked(isChecked: Int) {
        if (isPinSet()){
            val deleteEvent = DeleteEvent(event.eventId, isChecked, getStoredPin())
            calendarViewModel.deleteEvent(deleteEvent)
        }
    }
}