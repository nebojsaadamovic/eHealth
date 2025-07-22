package com.example.zdravlje.ui.calendar.personalActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.zdravlje.data.model.request.calendar.AddEvent
import com.example.zdravlje.data.model.request.calendar.Interval
import com.example.zdravlje.data.model.request.calendar.Repeat
import com.example.zdravlje.data.model.response.dashboard.future.Event
import com.example.zdravlje.data.repository.CalendarRepository
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.calendar.CalendarViewModel
import com.example.zdravlje.ui.calendar.CalendarViewModelFactory
import com.example.zdravlje.utilities.CommonUtils
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentPersonalActivityTimeActivityBinding
import java.text.SimpleDateFormat
import java.util.Locale

class PersonalActivityTimeActivityFragment(
    private var name: String,
    private var dateFrom: String,
    private var dateTo: String,
    private var className: String? = null,
    private var eventFuture: com.example.zdravlje.data.model.response.dashboard.future.Event? = null
) : BaseFragment(), TimeActivityBottomSheetFragment.OnTimeActivitySheetTimeFromListener {

    lateinit var binding: FragmentPersonalActivityTimeActivityBinding
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
    ): View? {
        binding =
            FragmentPersonalActivityTimeActivityBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        subscribeToObservers()
        setupUI()
    }

    private fun setupUI() {

        if (className == "PersonalDataActivity_EditArray") {
            binding.mbAdd.text = getString(R.string.edit)
            binding.tvCalendar.text ="Izmeni ličnu aktivnost"
        }

        binding.tvActivityNameValue.text = name
        binding.tvITimeFromValue.text = dateFrom
        binding.tvTimeToValue.text = dateTo

        binding.tvChooseTime.setOnClickListener() {
            isTimeSelected = true
            isTimeSelected1 = false
            isTimeSelected2 = false
            isTimeSelected3 = false
            val timeActivityBottomSheet = TimeActivityBottomSheetFragment()
            timeActivityBottomSheet.setTargetFragment(this, 0)
            timeActivityBottomSheet.show(parentFragmentManager, timeActivityBottomSheet.tag)
        }

        binding.ivAddTimeActivity.setOnClickListener() {
            if (isIcPlus) {
                binding.ivAddTimeActivity.setImageResource(R.drawable.ic_remove_time_activity)
                binding.clAddTimeActivities1.visibility = View.VISIBLE
                isIcPlus = false
            } else {
                binding.ivAddTimeActivity.setImageResource(R.drawable.ic_add_time_activity)
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

        binding.ivAddTimeActivity1.setOnClickListener() {
            if (isIcPlus1) {
                binding.ivAddTimeActivity1.setImageResource(R.drawable.ic_remove_time_activity)
                binding.clAddTimeActivities2.visibility = View.VISIBLE
                isIcPlus1 = false
            } else {
                binding.clAddTimeActivities1.visibility = View.GONE
                binding.ivAddTimeActivity1.setImageResource(R.drawable.ic_add_time_activity)
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

        binding.ivAddTimeActivity2.setOnClickListener() {
            if (isIcPlus2) {
                binding.clAddTimeActivities3.visibility = View.VISIBLE
                binding.ivAddTimeActivity2.setImageResource(R.drawable.ic_remove_time_activity)
                binding.ivAddTimeActivity3.setImageResource(R.drawable.ic_remove_time_activity)
                isIcPlus2 = false
            } else {
                binding.clAddTimeActivities2.visibility = View.GONE
                binding.ivAddTimeActivity2.setImageResource(R.drawable.ic_add_time_activity)
                binding.tvChooseTime2.text = getString(R.string.select)
                isIcPlus2 = true
            }
        }

        binding.ivAddTimeActivity3.setOnClickListener() {
            binding.clAddTimeActivities3.visibility = View.GONE
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
            if (className == "PersonalDataActivity_EditArray") {
                val personalActivityDateDetailsFragment = PersonalActivityDateDetailsFragment(
                    name = name,
                    dateFrom = dateFrom,
                    dateTo = dateTo,
                    className = className,
                    event = eventFuture
                )

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.activity_personal_fragment_container, personalActivityDateDetailsFragment)
                    .addToBackStack(null).commit()

            }else {
                CommonUtils.openFragmentCalendar(
                    requireActivity(),
                    PersonalActivityDateDetailsFragment(
                        name = name,
                        dateFrom = dateFrom,
                        dateTo = dateTo
                    )
                )
            }
        }

        binding.mbAdd.setOnClickListener() {
            val repeat = Repeat(
                every = 0,
                day = selectedTimes
            )

            val interval = Interval(
                from = formatDate(dateFrom) + " " + binding.tvChooseTime.text.toString(),
                to = formatDate(dateTo) + " 23:59",
                repeat = repeat
            )

            val event= AddEvent(
                eve_id = eventFuture?.eventId,
                eve_calendar_id =eventFuture?.eventCalendarId ,
                eve_title = name,
                eve_event_type_id = 4,
                interval = interval,
                pin = getStoredPin()
            )

            if (className == "PersonalDataActivity" || className == "PersonalDataActivity_EditArray"){
                calendarViewModel.updateEventAndNotification(addEvent = event)
            }else{
                calendarViewModel.createEventAnNotification(addEvent = event)
            }
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
                    getString(R.string.toast_data_save),
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().finish()
            } else {
                Toast.makeText(requireActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show()
            }
        }

        calendarViewModel.getCreateEventAnNotificationErrorLiveData.observe(viewLifecycleOwner) { response ->
            Toast.makeText(requireActivity(), "${getString(R.string.error)} ${response.body()}", Toast.LENGTH_SHORT).show()
        }

        calendarViewModel.getUpdateEventNotificationLiveData.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.success_change_data),
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().finish()
            } else {
                Toast.makeText(requireActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show()
            }
        }

        calendarViewModel.getUpdateEventNotificationErrorLiveData.observe(viewLifecycleOwner) { response ->
            Toast.makeText(requireActivity(), "${getString(R.string.error)} ${response.body()}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onTimeActivitySelected(value: String) {
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
}