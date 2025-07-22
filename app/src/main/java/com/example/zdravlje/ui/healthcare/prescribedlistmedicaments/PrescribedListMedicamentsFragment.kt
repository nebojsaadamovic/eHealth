package com.example.zdravlje.ui.healthcare.prescribedlistmedicaments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.request.healthcare.RecipeRequest
import com.example.zdravlje.data.model.response.healthcare.PrescribedListMedicamentResponse
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.ProfileRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.ui.adaptersAndViewHolders.healthcare.PrescribedListMedicamentsAdapter
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.calendar.medicament.withoutrecipe.MedicamentWithOutRecipeFragment
import com.example.zdravlje.ui.healthcare.HealthcareViewModel
import com.example.zdravlje.ui.healthcare.HealthcareViewModelFactory
import com.example.zdravlje.ui.healthcare.medicalcarton.MedicalCartonDateFragment
import com.example.zdravlje.ui.login.LoginActivity
import com.example.zdravlje.ui.login.PinBottomSheetFragment
import com.example.zdravlje.ui.profile.ProfileViewModel
import com.example.zdravlje.ui.profile.ProfileViewModelFactory
import com.example.zdravlje.utilities.CommonUtils
import com.example.zdravlje.utilities.DateUtils
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentPrescribedListMedicamentsBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PrescribedListMedicamentsFragment : BaseFragment(),
    PinBottomSheetFragment.OnPinListener,
    PrescribedListBottomSheetFilterFragment.OnFilterListener {

    lateinit var binding: FragmentPrescribedListMedicamentsBinding
    private lateinit var prescribedListMedicamentsAdapter: PrescribedListMedicamentsAdapter
    private lateinit var healthcareViewModel: HealthcareViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private var prescribedListValue: String? = null
    private var allPrescribedList = mutableListOf<PrescribedListMedicamentResponse>()
    private var isLoading = false
    private var isLastPage = false
    private var offsetIn: Int? = 0
    private var limitIn: Int = 10
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    var dateFrom: String? = null
    var dateTo: String? = null
    var isFilterDate: Boolean? = null
    private var statusThis: Int? = null
    private var typeThis: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPrescribedListMedicamentsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewModels()
        setAdapter(arrayOf())

        prescribedListValue = getString(R.string.fragment_prescribed_list_medicaments_header_adapter)

        val medicamentList =
            arguments?.getParcelableArrayList<PrescribedListMedicamentResponse>("medical_card_list_array")
        medicamentList?.let { allPrescribedList = it }
        isFilterDate = arguments?.getBoolean("filterDate", false) ?: false

        val dateList = healthcareViewModel.dateOllList.value
        statusThis = healthcareViewModel.statusOll.value
        typeThis = healthcareViewModel.typeOll.value

        if (isFilterDate as Boolean) {
            binding.ivCalendar.setImageResource(R.drawable.ic_calendar_medical)
            dateFrom = dateList?.get(0)
            dateTo = dateList?.get(1)
            filterDateChange(dateFrom, dateTo)
            if (statusThis != null || typeThis != null) {
                binding.ivFilter.setImageResource(R.drawable.ic_filter_selected)
            }
        } else if (statusThis != null || typeThis != null) {
            binding.ivCalendar.setImageResource(R.drawable.ic_bnm_calendar)
            binding.ivFilter.setImageResource(R.drawable.ic_filter_selected)
            if (dateList == null) {
                onFilterChange()
            } else if (dateList != null && isFilterDate == true) {
                binding.ivCalendar.setImageResource(R.drawable.ic_calendar_medical)
                filterDateChange(dateList[0], dateList[1])
            }
        }

        subscribeToObservers()
        setupUI()
    }

    private fun setupUI() {
        binding.ivFilter.setOnClickListener {
            val bottomSheet = PrescribedListBottomSheetFilterFragment()
            bottomSheet.setOnFilterListener(this)
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }

        binding.ivInformation.setOnClickListener() {
            val bottomSheetLegendFragment = BottomSheetLegendFragment()
            bottomSheetLegendFragment.show(childFragmentManager, bottomSheetLegendFragment.tag)
        }

        binding.ivPlus.setOnClickListener() {
            val fragment = MedicamentWithOutRecipeFragment().apply {
                arguments = Bundle().apply {
                    putString("header_title", prescribedListValue)
                }
            }
            openFragment(fragment)
        }

        binding.ivCalendar.setOnClickListener() {
            val fragment =
                MedicalCartonDateFragment.newInstance(className = "PrescribedListMedicamentsFragment")
            CommonUtils.openFragmentHome(requireActivity(), fragment)
        }

        binding.ivBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }

        binding.reciclerViewMedicamentDetails.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (!isLoading && !isLastPage && lastVisibleItem >= totalItemCount - 5) {
                    val startDate = if (isFilterDate == true && !dateFrom.isNullOrEmpty()) {
                        formatDateString(dateFrom)
                    } else {
                        LocalDate.now().minusMonths(6).format(formatter)
                    }
                    val endDate = if (isFilterDate == true && !dateTo.isNullOrEmpty()) {
                        formatDateString(dateTo)
                    } else {
                        LocalDate.now().format(formatter)
                    }

                    healthcareViewModel.searchPrescriptions(
                        RecipeRequest(
                            pin = getStoredPin(),
                            limitIn = limitIn,
                            offsetIn = offsetIn,
                            patmedTimeStart = startDate,
                            patmedTimeEnd = endDate,
                            status = statusThis,
                            type = typeThis
                        )
                    )

                    offsetIn = offsetIn?.plus(limitIn)
                    isLoading = true
                }
            }
        })
    }

    private fun setAdapter(toTypedArray: Array<PrescribedListMedicamentResponse>) {
        prescribedListMedicamentsAdapter =
            PrescribedListMedicamentsAdapter(toTypedArray, parentFragmentManager)
        val linearLayoutManager =
            object : LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false) {
                override fun canScrollVertically() = true
            }
        binding.reciclerViewMedicamentDetails.layoutManager = linearLayoutManager
        binding.reciclerViewMedicamentDetails.adapter = prescribedListMedicamentsAdapter
    }

    private fun openFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.home_fragment_container_view, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun fetchPrescribedMedicaments() {
        dateFrom = LocalDate.now().minusMonths(6).format(formatter)
        dateTo = LocalDate.now().format(formatter)
        if (isPinSet()) {
            healthcareViewModel.searchPrescriptions(
                RecipeRequest(
                    pin = getStoredPin(),
                    limitIn = limitIn,
                    offsetIn = 0,
                    patmedTimeStart = dateFrom,
                    patmedTimeEnd = dateTo,
                )
            )
            offsetIn = offsetIn?.plus(limitIn)
            isLoading = true
            binding.ivCalendar.setImageResource(R.drawable.ic_bnm_calendar)
            binding.ivFilter.setImageResource(R.drawable.ic_filter)
        } else {
            showHideShimmer(false)
            binding.wrapper.visibility = View.VISIBLE
            binding.reciclerViewMedicamentDetails.visibility = View.GONE

            val mbpinCode =
                binding.wrapper.findViewById<MaterialButton>(R.id.mbEnterPinCode)
            mbpinCode.setOnClickListener {
                val pinBottomSheetFragment = PinBottomSheetFragment()
                pinBottomSheetFragment.setOnPinListener(this)
                pinBottomSheetFragment.show(parentFragmentManager, pinBottomSheetFragment.tag)
            }

            val mtvNetwork = binding.wrapper.findViewById<MaterialTextView>(R.id.mtvNetwork)
            mtvNetwork.text = getString(R.string.fragment_healthcare_prescribed_list_medicines_title)

            val imageMsg = binding.wrapper.findViewById<ImageView>(R.id.ivMsg)
            imageMsg.setImageResource(R.drawable.ic_oll_wrapper)

            val mbLogInButton = binding.wrapper.findViewById<MaterialButton>(R.id.mbLogIn)
            mbLogInButton.setOnClickListener(View.OnClickListener {
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            })

            val ivBackButton = binding.wrapper.findViewById<ImageView>(R.id.ivBack)
            ivBackButton.setOnClickListener {
                requireActivity().onBackPressed()
            }
        }
    }

    private fun setUpViewModels() {
        val healthcareRepository = HealthcareRepository()
        dataStoreRepository = DataStoreRepository(requireActivity())
        val healthcareViewModelFactory =
            HealthcareViewModelFactory(healthcareRepository, dataStoreRepository)
        healthcareViewModel = ViewModelProvider(requireActivity(), healthcareViewModelFactory)
            .get(HealthcareViewModel::class.java)

        val profileRepository = ProfileRepository()
        val profileViewModelFactory =
            ProfileViewModelFactory(profileRepository, dataStoreRepository)
        profileViewModel = ViewModelProvider(
            requireActivity(),
            profileViewModelFactory
        ).get(ProfileViewModel::class.java)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onPinChange() {
        healthcareViewModel.searchPrescriptions(RecipeRequest(pin = getStoredPin(), limitIn = 10))
        binding.wrapper.visibility = View.GONE
        showHideShimmer(true)
    }

    override fun onPinDismiss() {
        showHideShimmer(true)
        binding.clWrapperData.visibility = View.VISIBLE
        binding.reciclerViewMedicamentDetails.visibility = View.GONE
    }

    private fun showHideShimmer(boolean: Boolean) {
        if (boolean == true) {
            binding.shimmer.visibility = View.VISIBLE
        } else {
            binding.shimmer.visibility = View.GONE
        }
    }

    private fun subscribeToObservers() {
        healthcareViewModel.searchPrescriptionsLiveData.observe(
            viewLifecycleOwner,
            Observer { response ->
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        isLoading = false
                        showHideShimmer(false)

                        if (data.isNotEmpty()) {
                            if (offsetIn == 0) {
                                allPrescribedList.clear()
                                allPrescribedList.addAll(data)
                                prescribedListMedicamentsAdapter.setItems(data.toTypedArray())
                            } else {
                                allPrescribedList.addAll(data)
                                prescribedListMedicamentsAdapter.addItems(data)
                            }

                            binding.clWrapperData.visibility = View.GONE
                            binding.reciclerViewMedicamentDetails.visibility = View.VISIBLE

                            isLastPage = data.size < limitIn
                        } else {
                            isLastPage = true
                            if (allPrescribedList.isEmpty()) {
                                binding.clWrapperData.visibility = View.VISIBLE
                                binding.reciclerViewMedicamentDetails.visibility = View.GONE
                            }
                        }
                    }
                } else {
                    isLoading = false
                    showHideShimmer(false)
                    showToast("${getString(R.string.error)}: ${response.message()}")
                }
            }
        )
    }

    var dateFromValue: String? = null
    var dateToValue: String? = null

    override fun onFilterChange() {
        binding.ivFilter.setImageResource(R.drawable.ic_filter_selected)

        if (healthcareViewModel.statusOll.value == null &&
            healthcareViewModel.typeOll.value == null
        ) {
            binding.ivFilter.setImageResource(R.drawable.ic_filter)
        }
        try {

            dateFromValue = DateUtils.convertVerLineddMyyyyToHorLineyyyyMMdd(dateFrom.toString())
            dateToValue = DateUtils.convertVerLineddMyyyyToHorLineyyyyMMdd(dateTo.toString())
            filterByStatusAndType()

        } catch (e: Exception) {
            dateFromValue = LocalDate.now().minusMonths(6).format(formatter)
            dateToValue = LocalDate.now().format(formatter)
            filterByStatusAndType()
        }
    }

    private fun filterByStatusAndType() {
        offsetIn = 0
        allPrescribedList.clear()
        binding.clWrapperData.visibility = View.GONE
        showHideShimmer(true)

        if (::prescribedListMedicamentsAdapter.isInitialized) {
            prescribedListMedicamentsAdapter.clearItems()
        }

        healthcareViewModel.searchPrescriptions(
            RecipeRequest(
                pin = getStoredPin(),
                limitIn = limitIn,
                offsetIn = offsetIn,
                patmedTimeStart = dateFromValue,
                patmedTimeEnd = dateToValue,
                status = healthcareViewModel.statusOll.value,
                type = healthcareViewModel.typeOll.value
            )
        )
        statusThis = healthcareViewModel.statusOll.value
        typeThis = healthcareViewModel.typeOll.value

        isLoading = true
    }

    private fun filterDateChange(dateFromFilter: String?, dateToFilter: String?) {
        if (isLoading) return

        isLoading = true
        offsetIn = 0
        isLastPage = false
        allPrescribedList.clear()
        showHideShimmer(true)
        prescribedListMedicamentsAdapter.clearItems()

        healthcareViewModel.searchPrescriptions(
            RecipeRequest(
                pin = getStoredPin(),
                limitIn = limitIn,
                offsetIn = offsetIn,
                patmedTimeStart = DateUtils.convertVerLineddMyyyyToHorLineyyyyMMdd(dateFromFilter.toString()),
                patmedTimeEnd = DateUtils.convertVerLineddMyyyyToHorLineyyyyMMdd(dateToFilter.toString()),
                status = healthcareViewModel.statusOll.value,
                type = healthcareViewModel.typeOll.value
            )
        )

        offsetIn = offsetIn?.plus(limitIn)
    }

    private fun formatDateString(dateStr: String?): String? {
        return try {
            val inputFormatter = DateTimeFormatter.ofPattern("d/M/yyyy")
            val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            dateStr?.let {
                LocalDate.parse(it, inputFormatter).format(outputFormatter)
            }
        } catch (e: Exception) {
            null
        }
    }

    override fun onResume() {
        super.onResume()
        handleResumeDataState()
    }

    private fun handleResumeDataState() {
        if (!::prescribedListMedicamentsAdapter.isInitialized) {
            setAdapter(allPrescribedList.toTypedArray())
        } else {
            prescribedListMedicamentsAdapter.setItems(allPrescribedList.toTypedArray())
        }

        val dateList = healthcareViewModel.dateOllList.value
        isFilterDate = !dateList.isNullOrEmpty()

        val status = healthcareViewModel.statusOll.value
        val type = healthcareViewModel.typeOll.value

        when {

            isFilterDate == true && dateList != null -> {
                filterDateChange(dateList[0], dateList[1])
            }

            status != null || type != null -> {
                onFilterChange()
            }

            allPrescribedList.isEmpty() -> {
                fetchPrescribedMedicaments()
            }

            else -> {
                showHideShimmer(false)
                prescribedListMedicamentsAdapter.setItems(allPrescribedList.toTypedArray())
                binding.clWrapperData.visibility = View.GONE
                binding.reciclerViewMedicamentDetails.visibility = View.VISIBLE
            }
        }
    }
}

