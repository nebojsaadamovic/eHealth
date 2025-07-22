package com.example.zdravlje.ui.home.dashboard.event_details

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.zdravlje.data.model.request.calendar.DeleteEvent
import com.example.zdravlje.data.model.response.dashboard.future.Event
import com.example.zdravlje.data.repository.CalendarRepository
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.ui.base.BaseActivity
import com.example.zdravlje.ui.calendar.CalendarViewModel
import com.example.zdravlje.ui.calendar.CalendarViewModelFactory
import com.example.zdravlje.ui.calendar.apointment.BottomSheetDialogConfirmFragment
import com.example.zdravlje.ui.calendar.nextOnSchedule.BottomSheetConfirmDeleteReminderFragment
import com.example.zdravlje.ui.calendar.nextOnSchedule.NextOnScheduleActivity
import com.example.zdravlje.ui.chat.scheduleCall.BottomSheetTimeFromFragment
import com.example.zdravlje.ui.home.HomeActivity
import com.example.zdravlje.utilities.DateUtils
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.ActivityPersonalDataBinding
import kotlinx.coroutines.launch

class PersonalDataActivity : BaseActivity(),
    BottomSheetChangeNameFragment.OnNameListener,
    BottomSheetTimeFromFragment.OnBottomSheetTimeFromListener,
    BottomSheetConfirmDeleteReminderFragment.OnCheckedListener {

    lateinit var binding: ActivityPersonalDataBinding
    private lateinit var calendarViewModel: CalendarViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var event: Event
    private var className: String? = null
    private var timeUpdateValue: String? = null
    private var activityName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //    this.window.navigationBarColor = ContextCompat.getColor(this, R.color.white)
        setViewModels()
        subscribeToObservers()
        event = (intent.getSerializableExtra("event") as? Event)!!
        className = intent.getStringExtra("className")

        if (event != null) {
            setupUI(event)
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

                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("open_calendar", true)
                startActivity(intent)
                this.finish()
            }
        }

        calendarViewModel.getUpdateEventLiveData.observe(this) { response ->
            if (response.isSuccessful) {
                val intent = Intent(this, NextOnScheduleActivity::class.java)
                intent.putExtra("time_update_value", event.eventStartTime?.substring(0, 10))
                startActivity(intent)
                finish()
            }
        }
    }

    fun setupUI(event: Event) {
        if (event.isFuture()) {
            if (className == "HomeFragment") {
                binding.ivDelete.visibility = View.GONE
                binding.mtvSave.visibility = View.GONE
                binding.mtvActivityNameValue.isClickable = false
                binding.mtvActivityNameValue.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.text_color_8A8A8D
                    )
                )
                binding.mtvTimeValue.isClickable = false
                binding.mtvTimeValue.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.text_color_8A8A8D
                    )
                )
            } else {
                binding.ivDelete.visibility = View.VISIBLE
                binding.mtvSave.visibility = View.VISIBLE
                binding.mtvActivityNameValue.isClickable = true
                binding.mtvActivityNameValue.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.base_blue_color
                    )
                )
                binding.mtvActivityNameValue.setOnClickListener() {
                    val bottomSheetChangeNameFragment = BottomSheetChangeNameFragment()
                    bottomSheetChangeNameFragment.setOnNameListener(this, "activityName")
                    bottomSheetChangeNameFragment.show(
                        supportFragmentManager,
                        bottomSheetChangeNameFragment.tag
                    )
                }

                binding.mtvTimeValue.isClickable = true
                binding.mtvTimeValue.setTextColor(
                    ContextCompat.getColor(this, R.color.base_blue_color)
                )
                binding.mtvTimeValue.setOnClickListener() {
                    val bottomSheetTimeFromFragment = BottomSheetTimeFromFragment()
                    bottomSheetTimeFromFragment.setListener(this)
                    bottomSheetTimeFromFragment.show(
                        supportFragmentManager, bottomSheetTimeFromFragment.tag
                    )
                }

                binding.ivDelete.setOnClickListener() {
                    val bottomSheet = BottomSheetConfirmDeleteReminderFragment()
                    bottomSheet.setCheckedListener(this)
                    bottomSheet.setTextSource("DeleteReminder-Personal data activity")
                    bottomSheet.show(supportFragmentManager, bottomSheet.tag)
                }

                binding.mbChangeAllInArray.visibility= View.VISIBLE
            }
        }

        binding.activityDate.text = event.eventStartTime?.let { DateUtils.getDateWithWeekName(it) }
        binding.mtvActivityNameValue.text = event.eventTitle
        binding.mtvTimeValue.text = event.eventStartTime?.let { DateUtils.getHoursFromDate(it) }
        binding.mtvDateFromValue.text =
            event.eventStartTime?.let { DateUtils.convertYYYYMMDDHHssToDotDDMMYYYY(it) }
        binding.mtvDateToValue.text =
            event.eventEndTime?.let { DateUtils.convertYYYYMMDDHHssToDotDDMMYYYY(it) }
        binding.ivBack.setOnClickListener {
            this.finish()
        }

        if (activityName == null) {
            activityName = event.eventTitle
        }

        binding.mbChangeAllInArray.setOnClickListener() {
            val content = "“Naredni podsetnici za ličnu aktivnost će biti obrisani. Izmena podrazumeva kreiranje novih budućih podsetnika za datu ličnu aktivnost"
                //getString(R.string.activity_drug_consumption_enter_consumption_content_bottom_sheet)
            val header =
                getString(R.string.activity_drug_consumption_enter_consumption_header_bottom_sheet)


            val bsDcf = BottomSheetDialogConfirmFragment().apply {
                arguments = Bundle().apply {
                    putString("title", "PersonalDataActivity_EditArray")
                    putString("header", header)
                    putString("content", content)
                   // putBundle("bundle", bundle)
                    putSerializable("event", event)
                    putString("activityName", activityName)
                }
            }
            bsDcf.show(supportFragmentManager, bsDcf.tag)

//            val fragament = PersonalActivityDateFragment(
//                activityName.toString(),
//                event,
//                "PersonalDataActivity"
//            )
//            this.supportFragmentManager.beginTransaction()
//                .replace(R.id.activity_personal_fragment_container, fragament)
//                .addToBackStack(null)
//                .commit()
        }

        binding.mtvSave.setOnClickListener {
            lifecycleScope.launch {
                if (isPinSet()) {
                    val mtvTerValue = binding.mtvActivityNameValue.text.toString()

                    val updateEvent = Event(
                        eventCalendarId = event.eventCalendarId,
                        eventTypeId = event.eventTypeId,
                        eventId = event.eventId,
                        eventStartTime = timeUpdateValue,
                        eventTitle = mtvTerValue,
                        pin = getStoredPin()
                    )
                    calendarViewModel.updateEvent(updateEvent)
                } else {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.tivity_personal_data_invalid_time),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onNameChange(name: String, title: String) {
        when (title) {
            "activityName" -> {
                binding.mtvActivityNameValue.text = name // Update only 'mtvActivityNameValue'
                activityName=name
            }
            //"info" -> binding.mtvInfoValue.text = name // Update only 'mtvInfoValue'
        }
    }

    override fun onBottomSheetTimeFromSelected(value: String) {
        binding.mtvTimeValue.text = value
        timeUpdateValue = value
    }

    override fun onChecked(isChecked: Int) {
        if (isPinSet()) {
            val deleteEvent = DeleteEvent(event.eventId, isChecked, getStoredPin())
            calendarViewModel.deleteEvent(deleteEvent)
        }
    }

//    override fun onBackPressed() {
//        val currentFragment = supportFragmentManager.findFragmentById(R.id.activity_personal_fragment_container)
//
//
//        if(currentFragment is PersonalActivityDateDetailsFragment
//
//        ){
//            setCurrentFragment(PersonalActivityDateFragment())
//            return
//        }
//        else if (currentFragment !is ChatInboxFragment &&
//            currentFragment !is HealthcareFragment &&
//            currentFragment !is FitParametersFragment &&
//            currentFragment !is CalendarFragment
//        ) {
//            super.onBackPressed()
//            return
//        }

//        if (currentFragment is CalendarOptionsFragment) {
//            finish()
//        } else {
//            super.onBackPressed()
//        }
//    }
//
//    fun setCurrentFragment(fragment: Fragment) {
//        supportFragmentManager.beginTransaction().apply {
//            replace(R.id.activity_personal_fragment_container, fragment)
//            addToBackStack(null)
//            commit()
//        }
//    }



}