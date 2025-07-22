package com.example.zdravlje.ui.healthcare.screeningexaminations

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.zdravlje.data.model.request.PinRequest
import com.example.zdravlje.data.model.request.calendar.ScreeningProgramsRequest
import com.example.zdravlje.data.model.response.calendar.Timeslot
import com.example.zdravlje.data.repository.CalendarRepository
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.calendar.CalendarViewModel
import com.example.zdravlje.ui.calendar.CalendarViewModelFactory
import com.example.zdravlje.ui.calendar.apointment.BottomSheetFreeAppointmentsFragment
import com.example.zdravlje.utilities.DateUtils
import kotlinx.coroutines.launch
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentMamografiaScreeningBinding

class MamografiaScreeningFragment : BaseFragment(),
    ScreeningExaminationsLocationBottomSheetFragment.OnValueListener , BottomSheetFreeAppointmentsFragment.OnAppointmentListener{

    lateinit var binding: FragmentMamografiaScreeningBinding
    private lateinit var calendarViewModel: CalendarViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    var dataResponse: ScreeningProgramsRequest?=null

    companion object {
        private const val ARG_mtvNameValue = "arg_name"
        private const val ARG_type = "arg_type"

        fun newInstance(name: String, type: String): MamografiaScreeningFragment {
            val fragment = MamografiaScreeningFragment()
            val bundle = Bundle()
            bundle.putString(ARG_mtvNameValue, name)
            bundle.putString(ARG_type, type)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMamografiaScreeningBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewModels()
        subscribeToObservers()
        setupUI()
    }

    private fun setupUI() {

        try{
            calendarViewModel.availableScreeningPrograms(PinRequest(getStoredPin()))
        }catch (e: Exception){
            Toast.makeText(
                requireContext(),
                getString(R.string.error),
                Toast.LENGTH_SHORT
            ).show()
            Log.e(getString(R.string.error), "${getString(R.string.add_reminder_fragment_error_saving_data)}: ${e.message}"
            )
        }

        val details = arguments?.getString(ARG_mtvNameValue)
        val type = arguments?.getString(ARG_type)
        binding.mtvNameValue.text =  dataResponse?.description
        binding.mtvTypeValue.text = type

        binding.mtvDurationValue.text= dataResponse?.validFrom +" - "+dataResponse?.validTo

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.tvAppointments.setOnClickListener {
            val screeningExaminationBottomSheetFragment = ScreeningExaminationBottomSheetFragment()
            screeningExaminationBottomSheetFragment.show(
                requireActivity().supportFragmentManager,
                screeningExaminationBottomSheetFragment.tag
            )
        }

        binding.mtvLocationValue.setOnClickListener {
            val screeningExaminationsLocationBottomSheetFragment =
                ScreeningExaminationsLocationBottomSheetFragment()
            screeningExaminationsLocationBottomSheetFragment.setOnValueListener(this)
            screeningExaminationsLocationBottomSheetFragment.show(
                requireActivity().supportFragmentManager,
                screeningExaminationsLocationBottomSheetFragment.tag
            )
        }

        binding.mtvDateValue.setOnClickListener {
            DateUtils.showDatePickerDialog(requireContext(), binding.mtvDateValue) {
                binding.mtvDateValue.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.text_color_8A8A8D
                    )
                )
                binding.mtvFreeAppointmentsValue.isEnabled = true
                binding.mtvFreeAppointmentsValue.alpha = 1.0f
            }
        }


        binding.mtvFreeAppointmentsValue.setOnClickListener {
            val testTimeslot = Timeslot(
                id = "123",                          // ID termina
                from = "2025-04-20T09:00:00",       // Početak termina
                to = "2025-04-20T09:30:00",         // Kraj termina
                available = true,                   // Da li je termin slobodan
                note = listOf("Pregled kod dr Petrović", "Poneti zdravstvenu knjižicu") // Napomene
            )



            val bottomSheet = BottomSheetFreeAppointmentsFragment(listOf(testTimeslot))
            bottomSheet.setOnAppointmentListener(this)
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }

    }

    override fun OnValueChange(value: String) {
        binding.mtvLocationValue.text = value
        binding.mtvDateValue.isEnabled = true
        binding.mtvDateValue.alpha = 1.0f
    }

    override fun onAppointmentChange(from: String, to: String, timeslotId: String) {
        TODO("Not yet implemented")
    }


    private fun setUpViewModels() {
        dataStoreRepository = DataStoreRepository(requireActivity())

        val calendarRepository = CalendarRepository()
        val calendarViewModelFactory =
            CalendarViewModelFactory(calendarRepository, dataStoreRepository)
        calendarViewModel = ViewModelProvider(
            requireActivity(),
            calendarViewModelFactory
        ).get(CalendarViewModel::class.java)
    }

    private fun subscribeToObservers(){
        calendarViewModel.getAvailableScreeningProgramsErrorLiveData.observe(viewLifecycleOwner) { response ->
            dataResponse=response.body()
            Toast.makeText(requireActivity(),"$response", Toast.LENGTH_SHORT).show()
        }
    }
}

