package com.example.zdravlje.ui.healthcare.esickleave

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.model.request.healthcare.ESickRequestDTO
import com.example.zdravlje.data.model.response.healthcare.Employer
import com.example.zdravlje.data.model.response.healthcare.SickLeaveNoteResponse
import com.example.zdravlje.data.model.response.healthcare.SickLeaveResponse
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.ui.adaptersAndViewHolders.healthcare.EsickReportsAdapter
import com.example.zdravlje.ui.healthcare.HealthcareViewModel
import com.example.zdravlje.ui.healthcare.HealthcareViewModelFactory
import com.example.zdravlje.utilities.CommonUtils
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentESickDocumentsBinding

class ESickDocumentsFragment(
    private val sickLeaveResponse: SickLeaveResponse? = null,
    private var sickLeaveNotes: List<SickLeaveNoteResponse>? = null,
    private var employerObject: Employer? = null,
    private val userPin: String? = null
) : Fragment() {

    private lateinit var binding: FragmentESickDocumentsBinding
    private lateinit var esickReportsAdapter: EsickReportsAdapter
    private lateinit var healthcareViewModel: HealthcareViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    lateinit var listEmployers: List<Employer>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentESickDocumentsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        setupUI()
        subscribeToObservers()
    }

    private fun setupUI() {
        sickLeaveResponse?.employers?.forEach { data ->
            listEmployers = listOf(data)
        }

        val employerValue = sickLeaveResponse?.employers?.let { it }

        if (employerValue?.size!! <= 1) {
            binding.mtvChooseEmployerValue.text = employerValue.firstOrNull()?.employerName
        }

        binding.clChooseEmployer.setOnClickListener() {
            CommonUtils.openFragmentHome(
                requireActivity(),
                EsickEmployerSelectedFragment(
                    sickLeaveResponse?.employers,
                    sickLeaveResponse,
                    userPin
                )
            )
        }

        if (employerObject != null) {
            binding.mtvChooseEmployerValue.text = employerObject?.employerName
        }

        binding.clConfirmation.setOnClickListener() {
            binding.progressBar.visibility = View.VISIBLE

            if (employerObject != null) {
                healthcareViewModel.getInabilityReceipt(
                    ESickRequestDTO(
                        urlForTempInabilityReceipt = employerObject?.urlForTempInabilityReceipt,
                        pin = userPin
                    )
                )

            } else if (employerValue != null) {
                healthcareViewModel.getInabilityReceipt(
                    ESickRequestDTO(
                        urlForTempInabilityReceipt = employerValue.firstOrNull()?.urlForTempInabilityReceipt,
                        pin = userPin
                    )
                )
            }
        }

        if (employerObject != null || employerValue?.size!! <= 1) {
            binding.clConfirmation.visibility = View.VISIBLE
            binding.mtvSubtitleOne.visibility = View.VISIBLE
        }

        if (sickLeaveNotes?.isNotEmpty() == true) {
            esickReportsAdapter = EsickReportsAdapter(
                sickLeaveNotes,
                parentFragmentManager,
                userPin,
                viewLifecycleOwner, requireContext()
            )

            if (sickLeaveNotes?.firstOrNull()?.urlForTempInabilityReport != null) {
                binding.mtvSubtitleTwo.visibility = View.VISIBLE
                binding.rvReports.visibility = View.VISIBLE
                setAdapter()
            } else {
                binding.mtvSubtitleTwo.visibility = View.GONE
                binding.mtvContentEmpty.visibility = View.VISIBLE
                binding.rvReports.visibility = View.GONE
            }


        } else if (employerValue?.size!! <= 1) {
            esickReportsAdapter = EsickReportsAdapter(
                employerValue.firstOrNull()?.sickLeaveNotes,
                parentFragmentManager,
                userPin,
                viewLifecycleOwner,
                requireContext()
            )

            if (employerValue.firstOrNull()?.sickLeaveNotes?.firstOrNull()?.urlForTempInabilityReport != null) {
                binding.mtvSubtitleTwo.visibility = View.VISIBLE
                binding.rvReports.visibility = View.VISIBLE
                setAdapter()
            } else {
                binding.mtvSubtitleTwo.visibility = View.GONE
                binding.mtvContentEmpty.visibility = View.VISIBLE
                binding.rvReports.visibility = View.GONE
            }
        }

        binding.ivBack.setOnClickListener() {
           requireActivity().onBackPressed()
        }
    }

    private fun setViewModels() {
        val healthcareRepository = HealthcareRepository()
        dataStoreRepository = DataStoreRepository(requireContext())
        val healthcareViewModelFactory =
            HealthcareViewModelFactory(healthcareRepository, dataStoreRepository)
        healthcareViewModel =
            ViewModelProvider(context as ViewModelStoreOwner, healthcareViewModelFactory)
                .get(HealthcareViewModel::class.java)
    }

    private fun subscribeToObservers() {
        healthcareViewModel.inabilityReceiptLiveData.observe(
            viewLifecycleOwner,
            Observer { response ->
                if (response.isSuccessful) {
                    binding.progressBar.visibility = View.GONE
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val fileName =
                            getString(R.string.esick_leave_adapter_certificate_temporary_incapacity_for_work)
                        val pdfBody = responseBody.bytes()
                        CommonUtils.openFragmentHome(requireActivity(), OpenPdfFileUri2Fragment.newInstance(pdfBody, fileName))
                    }
                }
            })

        healthcareViewModel.inabilityReceiptErrorLiveData.observe(
            viewLifecycleOwner,
            Observer { error ->
                binding.progressBar.visibility = View.GONE
                Toast.makeText(
                    context,
                    "${getString(R.string.error)}: $error",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    private fun setAdapter() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvReports.layoutManager = linearLayoutManager
        binding.rvReports.adapter = esickReportsAdapter
    }
}