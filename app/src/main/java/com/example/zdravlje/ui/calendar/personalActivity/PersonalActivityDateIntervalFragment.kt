package com.example.zdravlje.ui.calendar.personalActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.zdravlje.data.model.request.calendar.AddEvent
import com.example.zdravlje.data.model.request.calendar.Interval
import com.example.zdravlje.data.model.request.calendar.Repeat
import com.example.zdravlje.data.repository.CalendarRepository
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.calendar.CalendarViewModel
import com.example.zdravlje.ui.calendar.CalendarViewModelFactory
import com.example.zdravlje.ui.chat.scheduleCall.BottomSheetTimeFromFragment
import com.example.zdravlje.utilities.CommonUtils
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentPersonalActivityDateIntervalBinding
import java.text.SimpleDateFormat
import java.util.Locale

class PersonalActivityDateIntervalFragment(
    var name: String,
    var dateFrom: String,
    var dateTo: String,
    private var className: String? = null,
    private var eventFuture: com.example.zdravlje.data.model.response.dashboard.future.Event? = null
) : BaseFragment(),
    BottomSheetTimeFromFragment.OnBottomSheetTimeFromListener,
    BottomSheetIntervalHours.OnBottomSheetTimeToListener {

    lateinit var binding: FragmentPersonalActivityDateIntervalBinding
    private lateinit var calendarViewModel: CalendarViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    var isIntervalSelected = false
    var isTimeFromSelected = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPersonalActivityDateIntervalBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        subscribeToObservers()
        setupUI()
    }

    fun setupUI() {
        if (className == "PersonalDataActivity_EditArray") {
            binding.mbNext.text = getString(R.string.edit)
            binding.tvCalendar.text = "Izmeni ličnu aktivnost"
        }

        binding.tvActivityNameValue.text = name
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

        binding.mbNext.setOnClickListener(View.OnClickListener {

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

            val event= AddEvent(
                eve_id = eventFuture?.eventId,
                eve_calendar_id =eventFuture?.eventCalendarId ,
                eve_title = name,
                eve_event_type_id = 4,
                interval = interval,
                pin = getStoredPin()
            )

            if (className == "PersonalDataActivity"){
                calendarViewModel.updateEventAndNotification(addEvent = event)
            }else{
                calendarViewModel.createEventAnNotification(addEvent = event)
            }
        })

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
                    .replace(R.id.activity_personal_fragment_container, personalActivityDateDetailsFragment
                    ).addToBackStack(null).commit()

            } else {
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

    fun formatDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date)
    }
}