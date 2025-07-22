package com.example.zdravlje.ui.healthcare.medicalcarton

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zdravlje.data.model.request.healthcare.MedicalCartonRequest
import com.example.zdravlje.data.model.response.healthcare.MedicalCartonDetailResponse
import com.example.zdravlje.data.model.response.healthcare.MedicalCartonResponse
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.ui.adaptersAndViewHolders.healthcare.MedicalCartonAdapter
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.healthcare.HealthcareViewModel
import com.example.zdravlje.ui.healthcare.HealthcareViewModelFactory
import com.example.zdravlje.ui.login.LoginActivity
import com.example.zdravlje.ui.login.PinBottomSheetFragment
import com.example.zdravlje.utilities.CommonUtils
import com.example.zdravlje.utilities.DateUtils
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentMedicalCartonBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MedicalCartonFragment(
    private var cartonArr: Array<MedicalCartonResponse>? = null,
    private var className: String? = null,
    private var dateFrom: String? = null,
    private var dateTo: String? = null
) : BaseFragment(), MedicalCartonSortDialog.IMedicalCartonSortOptions,
    PinBottomSheetFragment.OnPinListener {

    lateinit var binding: FragmentMedicalCartonBinding
    private lateinit var medicalCartonAdapter: MedicalCartonAdapter
    private lateinit var healthcareViewModel: HealthcareViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private var cartonArrData: MutableSet<MedicalCartonResponse> = mutableSetOf()
    private var page = 1
    private var isLoading = false
    private var isLastPage = false
    private var isAscending = false
    private var lastFetchedPageSize = 0
    var formatDateFrom: String? = null
    var formatDateTo: String? = null
    var institutionCode: String? = ""

    object SharedCartonData {
        var selectedCarton: String? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMedicalCartonBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        showHideShimmer(true)

        formatDateFrom =
            healthcareViewModel.dateECartonList.value?.getOrNull(0)?.takeIf { it.isNotBlank() }
                ?.let {
                    DateUtils.convertVerLineddMyyyyToHorLineyyyyMMdd(it)
                }

        formatDateTo =
            healthcareViewModel.dateECartonList.value?.getOrNull(1)?.takeIf { it.isNotBlank() }
                ?.let {
                    DateUtils.convertVerLineddMyyyyToHorLineyyyyMMdd(it)
                }

        institutionCode = healthcareViewModel.institutionCode.value ?: ""

        if (institutionCode == null) {
            institutionCode = ""
        }

        healthcareViewModel.dateECartonList.value?.let { list ->
            if (list.size == 2) {
                binding.ivCalendar.setImageResource(R.drawable.ic_calendar_medical)
            }
        } ?: binding.ivCalendar.setImageResource(R.drawable.ic_bnm_calendar)


        healthcareViewModel.institutionCode.value?.let { list ->
            binding.ivInstitution.setImageResource(R.drawable.ic_institution_selected)
        } ?: binding.ivInstitution.setImageResource(R.drawable.ic_institutions)

        if (className == "MedicalCartonDateFragment") {
            page = 1
            isLastPage = false
            isLoading = true
            cartonArrData.clear()
            healthcareViewModel.getCasesData(
                MedicalCartonRequest(
                    dateFrom = formatDateFrom,
                    dateTo = formatDateTo,
                    institutionCode = institutionCode,
                    pin = getStoredPin(),
                    sortBy = "descending",
                    pageNumber = 1
                )
            )
            isLoading = true

        } else if (className == "Institution") {
            healthcareViewModel.getCasesData(
                MedicalCartonRequest(
                    dateFrom = formatDateFrom,
                    dateTo = formatDateTo,
                    institutionCode = institutionCode,
                    pin = getStoredPin(),
                    sortBy = "descending",
                    pageNumber = 1
                )
            )
            isLoading = true
            className = "MedicalCarton"

        } else {
            if (isPinSet()) {
                binding.wrapper.visibility = View.GONE
                val dateFormat = SimpleDateFormat("dd/M/yyyy", Locale.getDefault())
                val calendarTo = Calendar.getInstance()
                val calendarFrom = Calendar.getInstance().apply {
                    add(Calendar.MONTH, -6)
                }

                healthcareViewModel.getCasesData(
                    MedicalCartonRequest(
                        dateFrom = dateFormat.format(calendarFrom.time),
                        dateTo = dateFormat.format(calendarTo.time),
                        institutionCode = institutionCode,
                        pin = getStoredPin(),
                        sortBy = "descending",
                        pageNumber = 1
                    )
                )
                isLoading = true

            } else {
                showHideShimmer(false)
                binding.wrapper.visibility = View.VISIBLE
                binding.reciclerViewMedicalCard.visibility = View.GONE

                val mbpinCode =
                    binding.wrapper.findViewById<MaterialButton>(R.id.mbEnterPinCode)
                mbpinCode.setOnClickListener {
                    val pinBottomSheetFragment = PinBottomSheetFragment()
                    pinBottomSheetFragment.setOnPinListener(this)
                    pinBottomSheetFragment.show(parentFragmentManager, pinBottomSheetFragment.tag)
                }

                val mtvNetwork = binding.wrapper.findViewById<MaterialTextView>(R.id.mtvNetwork)
                mtvNetwork.text = getString(R.string.fragment_healthcare_medical_card_title)

                val imageMsg = binding.wrapper.findViewById<ImageView>(R.id.ivMsg)
                imageMsg.setImageResource(R.drawable.ic_medical_carton_wrapper)

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

        observeToObservers()
        setupUI()
    }

    private fun setupUI() {
        binding.ivSort.setOnClickListener {
            val medicalCartonSortDialog = MedicalCartonSortDialog(requireContext(), this)
            medicalCartonSortDialog.setCanceledOnTouchOutside(true)
            medicalCartonSortDialog.show()
        }

        binding.ivAdd.setOnClickListener() {
            val medicalCartonOptionsFragment = MedicalCartonOptionsFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.home_fragment_container_view, medicalCartonOptionsFragment)
                .addToBackStack(null).commit()
        }

        binding.ivInstitution.setOnClickListener() {
            val institutionsArrayFragment = InstitutionsArrayFragment("MedicalCarton")
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.home_fragment_container_view, institutionsArrayFragment)
                .addToBackStack(null).commit()
        }

        binding.ivCalendar.setOnClickListener() {
            CommonUtils.openFragmentHome(
                requireActivity(),
                MedicalCartonDateFragment.newInstance(className = "MedicalCartonFragment")
            )
        }

        binding.ivBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }

        binding.reciclerViewMedicalCard.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (!isLoading && !isLastPage && lastVisibleItem >= totalItemCount - 5) {


                    val request = MedicalCartonRequest(
                        dateFrom = formatDateFrom,
                        dateTo = formatDateTo,
                        institutionCode = institutionCode,
                        pin = getStoredPin(),
                        sortBy = if (isAscending) "ascending" else "descending",
                        pageNumber = page++
                    )

                    healthcareViewModel.getCasesData(request)
                    isLoading = true
                }
            }
        })


    }

    private fun setViewModels() {
        val healthcareRepository = HealthcareRepository()
        dataStoreRepository = DataStoreRepository(requireActivity())
        val healthcareViewModelFactory =
            HealthcareViewModelFactory(healthcareRepository, dataStoreRepository)
        healthcareViewModel = ViewModelProvider(
            requireActivity(), healthcareViewModelFactory
        ).get(HealthcareViewModel::class.java)
    }

    private fun observeToObservers() {
        healthcareViewModel.getMedicalCardLiveData.observe(viewLifecycleOwner) { response ->
            isLoading = false
            showHideShimmer(false)
            cartonArrData.clear()
            if (response != null && response.isSuccessful) {
                response.body()?.let { data ->
                    if (!data.isNullOrEmpty()) {
                        binding.clWrapperData.visibility= View.GONE

                        lastFetchedPageSize = data.size

                        if (data.isEmpty()) {
                            isLastPage = true
                            return@let
                        }

                        if (page == 1) {
                            cartonArrData = data.toMutableSet()
                            setAdapter(cartonArrData.toMutableList())
                        } else {
                            cartonArrData.addAll(data)
                            medicalCartonAdapter.updateData(cartonArrData.toMutableList())
                        }

                        isLastPage = data.size < 10
                    }else{
                        binding.clWrapperData.visibility= View.VISIBLE
                    }
                }
            } else {
                isLastPage = cartonArrData.size < 10
                //showToast("Greška: ${response?.message() ?: "Nepoznata greška"}")
            }
        }
    }

    private fun setAdapter(dataArray: List<MedicalCartonResponse>) {
        medicalCartonAdapter = MedicalCartonAdapter(dataArray, parentFragmentManager)
        val linearLayoutManager =
            object : LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false) {
                override fun canScrollVertically() = true
            }
        binding.reciclerViewMedicalCard.layoutManager = linearLayoutManager
        binding.reciclerViewMedicalCard.adapter = medicalCartonAdapter
    }

    override fun onShowSortDescendingDialog() {
        if (className == "MedicalCartonDateFragment") {
            cartonArrData?.let { sortData(it.toMutableList(), "descending") }
        } else {
            cartonArrData.clear()
            isAscending = true
            page = 1
            healthcareViewModel.getCasesData(
                MedicalCartonRequest(
                    dateFrom = formatDateFrom,
                    dateTo = formatDateTo,
                    institutionCode = institutionCode,
                    pin = getStoredPin(),
                    sortBy = "descending",
                    pageNumber = 1
                )
            )
        }
    }

    override fun onShowSortAscendingDialog() {
        if (className == "MedicalCartonDateFragment") {
            cartonArrData?.let { sortData(it.toMutableList(), "ascending") }
        } else {
            cartonArrData.clear()
            isAscending = true
            page = 1
            healthcareViewModel.getCasesData(
                MedicalCartonRequest(
                    dateFrom = formatDateFrom,
                    dateTo = formatDateTo,
                    institutionCode = institutionCode,
                    pin = getStoredPin(),
                    sortBy = "ascending",
                    pageNumber = 1
                )
            )
        }
    }

    private fun sortData(value: List<MedicalCartonResponse>, sortOrder: String) {
        val listMedicalCard = value.map { responseObject ->
            MedicalCartonResponse(
                caseDate = responseObject.caseDate ?: "N/A",
                caseInstitution = responseObject.caseInstitution ?: "N/A",
                institutionCode = responseObject.institutionCode ?: "N/A",
                detail = MedicalCartonDetailResponse(
                    admissionCabinetName = responseObject.detail?.admissionCabinetName ?: "N/A",
                    admissionDoctor = responseObject.detail?.admissionDoctor ?: "N/A",
                    referralType = responseObject.detail?.referralType ?: "N/A",
                    referralResponseTypeName = responseObject.detail?.referralResponseTypeName
                        ?: "N/A"
                )
            )
        }

        val sortedList = when (sortOrder.lowercase()) {
            "ascending" -> listMedicalCard.sortedBy { it.caseDate }
            "descending" -> listMedicalCard.sortedByDescending { it.caseDate }
            else -> listMedicalCard
        }

        cartonArrData = sortedList.toMutableSet()
        medicalCartonAdapter.sortUpdatedData(sortedList)
    }

    override fun onResume() {
        super.onResume()

        SharedCartonData.selectedCarton?.let {
            className = it

            if (className == "MedicalCarton" || className == "MedicalCartonDateFragment") {
                page = 1
                isLastPage = false
                isLoading = true
                cartonArrData.clear()

                if (!::medicalCartonAdapter.isInitialized) {
                    setAdapter(emptyList())
                }
                medicalCartonAdapter.clearData()

                getStoredPin()?.let { pin ->
                    healthcareViewModel.getCasesData(
                        MedicalCartonRequest(
                            dateFrom = formatDateFrom,
                            dateTo = formatDateTo,
                            institutionCode = institutionCode,
                            pin = pin,
                            sortBy = "descending",
                            pageNumber = page
                        )
                    )
                }
            }

            SharedCartonData.selectedCarton = null
        }
    }

    override fun onPinChange() {
        binding.wrapper.visibility = View.GONE

        healthcareViewModel.getCasesData(
            MedicalCartonRequest(
                dateFrom = formatDateFrom,
                dateTo = formatDateTo,
                institutionCode = institutionCode,
                pin = getStoredPin(),
                sortBy = "descending",
                pageNumber = 1
            )
        )
        isLoading = true
    }

    override fun onPinDismiss() {
        showHideShimmer(false)
    }

    private fun showHideShimmer(boolean: Boolean) {
        if (boolean == true) {
            binding.shimmer.visibility = View.VISIBLE
        } else {
            binding.shimmer.visibility = View.GONE
        }
    }
}