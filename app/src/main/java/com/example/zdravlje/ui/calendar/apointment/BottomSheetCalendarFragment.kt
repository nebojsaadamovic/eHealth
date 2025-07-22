package com.example.zdravlje.ui.calendar.apointment

import FirstAvailableDateDecorator
import FreeAppointmentDecorator
import SelectedDateDecorator
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentBottomSheetCalendarBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BottomSheetCalendarFragment : BottomSheetDialogFragment() {

    lateinit var binding: FragmentBottomSheetCalendarBinding
    private var availableDates: List<CalendarDay> = emptyList()
    private var dateSelectedListener: DateSelectedListener? = null
    private lateinit var selectedDateDecorator: SelectedDateDecorator
    private lateinit var sharedPreferences: SharedPreferences

    private val monthsInLatin = listOf(
        "Januar", "Februar", "Mart", "April", "Maj", "Jun", "Jul", "Avgust",
        "Septembar", "Oktobar", "Novembar", "Decembar"
    )

    private val monthsInCyrillic = listOf(
        "јануар", "фебруар", "март", "април", "мај", "јун", "јул", "август",
        "септембар", "октобар", "новембар", "децембар"
    )

    private val monthsInEnglish = listOf(
        "January", "February", "March", "April", "May", "June", "July", "August",
        "September", "October", "November", "December"
    )

    private val weekdaysInLatin = listOf(
        "Pon", "Uto", "Sre", "Čet", "Pet", "Sub", "Ned"
    )

    private val weekdaysInCyrillic = listOf(
        "пон", "уто", "сре", "чет", "пет", "суб", "нед"
    )

    private val weekdaysInEnglish = listOf(
        "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"
    )

    interface DateSelectedListener {
        fun onDateSelected(date: String)
    }

    fun setDateSelectedListener(listener: DateSelectedListener) {
        dateSelectedListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetCalendarBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        setupCalendar()
    }


    private fun setupCalendar() {
        val calendarView = binding.materialCalendarView
        selectedDateDecorator = SelectedDateDecorator(requireContext())

        val months = getMonthListBasedOnLanguage()
        val weekdays = getWeekdaysListBasedOnLanguage()

        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY

        if (availableDates.isNotEmpty()) {
            val firstAvailableDate = availableDates.first()
            val adjustedDate = CalendarDay.from(
                firstAvailableDate.year,
                firstAvailableDate.month,
                firstAvailableDate.day
            )

            calendarView.setCurrentDate(adjustedDate)
            calendarView.setSelectedDate(adjustedDate)

            calendarView.addDecorator(FreeAppointmentDecorator(requireContext(), availableDates))
            calendarView.addDecorator(
                FirstAvailableDateDecorator(
                    requireContext(),
                    adjustedDate
                )
            )
        }

        calendarView.addDecorator(selectedDateDecorator)
        calendarView.setTitleFormatter { day ->
            val monthIndex = day.month - 1
            val month = months[monthIndex]
            val year = day.year
            "$month $year"
        }

        calendarView.setWeekDayFormatter { day ->
            val dayOfWeekIndex = (day.value - 1) % 7
            weekdays[dayOfWeekIndex]
        }

        calendarView.setOnDateChangedListener { _, date, _ ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(date.year, date.month - 1, date.day)
            val formattedDate =
                SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(selectedCalendar.time)

            dateSelectedListener?.onDateSelected(formattedDate)
            selectedDateDecorator.setSelectedDate(date)

            calendarView.invalidateDecorators()
            dismiss()
        }
    }

    private fun isLatinLocale(): Boolean {
        val currentLocale = Locale.getDefault().toString().lowercase()
        return currentLocale.contains("latn") || currentLocale.contains("sr-rrs")
    }

    private fun getSavedLanguage(): String {
        return sharedPreferences.getString("selected_language", "sr") ?: "sr"
    }

    private fun getMonthListBasedOnLanguage(): List<String> {
        val language = getSavedLanguage()

        return when (language) {
            "sr" -> {
                if (isLatinLocale()) {
                    monthsInLatin
                } else {
                    monthsInCyrillic
                }
            }

            "en" -> monthsInEnglish
            else -> monthsInLatin
        }
    }

    private fun getWeekdaysListBasedOnLanguage(): List<String> {
        val language = getSavedLanguage()

        return when (language) {
            "sr" -> {
                if (isLatinLocale()) {
                    weekdaysInLatin
                } else {
                    weekdaysInCyrillic
                }
            }

            "en" -> weekdaysInEnglish
            else -> weekdaysInLatin
        }
    }

    fun setAvailableDates(dates: List<CalendarDay>) {
        availableDates = dates
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }
}