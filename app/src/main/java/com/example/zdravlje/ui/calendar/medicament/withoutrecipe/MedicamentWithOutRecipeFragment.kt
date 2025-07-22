package com.example.zdravlje.ui.calendar.medicament.withoutrecipe

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.model.request.calendar.MedicineRequest
import com.example.zdravlje.data.model.response.calendar.MedicineResponse
import com.example.zdravlje.data.repository.CalendarRepository
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.ui.adaptersAndViewHolders.calendar.MedicineSearchAdapter
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.calendar.CalendarViewModel
import com.example.zdravlje.ui.calendar.CalendarViewModelFactory
import com.example.zdravlje.ui.healthcare.prescribedlistmedicaments.medicamentoptions.AddMedicmentPrescribedListFragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentMedicamentWithOutReceptBinding

class MedicamentWithOutRecipeFragment : BaseFragment() {

    lateinit var binding: FragmentMedicamentWithOutReceptBinding
    private var searchRunnable: Runnable? = null
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var calendarViewModel: CalendarViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var medicineAdapter: MedicineSearchAdapter
    private var headerTitle: String? = null
    private var prescribedListValue: String? = null
    private var medicineList: List<MedicineResponse>? = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMedicamentWithOutReceptBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        subscribeToObservers()
       setAdapter(medicineList!!)
        prescribedListValue =
            getString(R.string.fragment_prescribed_list_medicaments_header_adapter)
        setupUI()
    }

    private fun setAdapter(medicineList: List<MedicineResponse>) {
        binding.rvMedicament.layoutManager = LinearLayoutManager(requireContext())
        medicineAdapter = MedicineSearchAdapter(requireContext(), medicineList, { })
        binding.rvMedicament.adapter = medicineAdapter
    }

    private fun setupUI() {
        calendarViewModel.getMedicamentSearch(
            MedicineRequest(
                pin = getStoredPin(),
                cbmed_status = 1,
                limit_in = 200,
                offset_in = 0,
            )
        )

        headerTitle = arguments?.getString("header_title") ?: ""
        if (headerTitle!!.isNotEmpty()) {
            binding.tvAddMedicine.text = headerTitle
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchRunnable?.let { handler.removeCallbacks(it) }
            }

            override fun afterTextChanged(p0: Editable?) {
                val searchText = p0?.toString() ?: ""

                if (searchText.length < 3) {
                    hideProgressBar()
                    medicineAdapter = MedicineSearchAdapter(requireContext(), emptyList(), ::onMedicineItemClick)
                    binding.rvMedicament.adapter = medicineAdapter
                    if (searchText.isEmpty()) {
                        hideProgressBar()
                        setupUI()
                        medicineAdapter = MedicineSearchAdapter(requireContext(), medicineList!!, ::onMedicineItemClick)
                        binding.rvMedicament.adapter = medicineAdapter
                    }
                    return
                }

                showProgressBar()
                searchRunnable = Runnable {
                    calendarViewModel.getMedicamentSearch(
                        MedicineRequest(
                            pin = getStoredPin(),
                            limit_in = 15,
                            offset_in = 0,
                            cbmed_status = 1,
                            cbmed_protected_name = searchText
                        )
                    )
                }
                handler.postDelayed(searchRunnable!!, 500)
            }
        })

        binding.ivBack.setOnClickListener() {
            requireActivity().onBackPressed()
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
        calendarViewModel.getMedicineSearchLiveData.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                hideProgressBar()
                medicineList = response.body()!!
                if(medicineList!!.isNotEmpty()) {
                    medicineAdapter =
                        MedicineSearchAdapter(requireContext(), medicineList!!, ::onMedicineItemClick)
                    binding.rvMedicament.adapter = medicineAdapter
                    binding.clWrapperData.visibility=View.GONE
                    hideProgressBar()
                }
                else{
                    binding.clWrapperData.visibility=View.VISIBLE
                    hideProgressBar()
                }

            } else {
                Toast.makeText(requireContext(), getString(R.string.unsuccessfully), Toast.LENGTH_SHORT).show()
                hideProgressBar()
            }
        }

        calendarViewModel.getMedicineErrorLiveData.observe(viewLifecycleOwner) {
            hideProgressBar()
            Toast.makeText(requireActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show()
            hideProgressBar()
        }
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private fun onMedicineItemClick(medicine: MedicineResponse) {
        if (headerTitle == prescribedListValue) {
            val fragment = AddMedicmentPrescribedListFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("medicine_response", medicine)
                }
            }

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.home_fragment_container_view, fragment)
                .addToBackStack(null)
                .commit()
        } else {
            val bundle = Bundle().apply {
                putParcelable("medicine_item", medicine)
                putString("title", "medicamenteWithOutRecipe")
            }

            val periodFromToFragment = PeriodFromToFragment()
            periodFromToFragment.arguments = bundle

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.calendar_options_container_view, periodFromToFragment)
                .addToBackStack(null)
                .commit()
        }
    }
}