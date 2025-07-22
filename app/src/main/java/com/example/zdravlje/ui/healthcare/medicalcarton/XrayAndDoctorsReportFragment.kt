package com.example.zdravlje.ui.healthcare.medicalcarton

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.zdravlje.data.model.request.healthcare.MedicalCartonPdfRequest
import com.example.zdravlje.data.model.response.healthcare.MedicalCartonResponse
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.healthcare.HealthcareViewModel
import com.example.zdravlje.ui.healthcare.HealthcareViewModelFactory
import kotlinx.coroutines.launch
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentXrayAndDoctorsReportBinding
import java.io.File
import java.io.FileOutputStream

class XrayAndDoctorsReportFragment(
    private val medicalCardObject: MedicalCartonResponse
) : BaseFragment() {

    private lateinit var binding: FragmentXrayAndDoctorsReportBinding
    private lateinit var healthcareViewModel: HealthcareViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private var userPin: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentXrayAndDoctorsReportBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        observeToObservers()
        setupUI()
    }

    private fun setViewModels() {
        val healthcareRepository = HealthcareRepository()
        dataStoreRepository = DataStoreRepository(requireActivity())
        val healthcareViewModelFactory =
            HealthcareViewModelFactory(healthcareRepository, dataStoreRepository)
        healthcareViewModel = ViewModelProvider(
            requireActivity(),
            healthcareViewModelFactory
        ).get(HealthcareViewModel::class.java)
    }

    private fun observeToObservers() {
        userPin=getStoredPin()
    }

    private fun setupUI() {
        val xRayDiagnostics = "Rendgen dijagnostika"
        if (medicalCardObject.detail?.referralType?.equals(xRayDiagnostics) == true) {
            binding.clXRay.visibility = View.VISIBLE
        }

        val url = medicalCardObject.detail?.src

        binding.clXRay.setOnClickListener() {
            val webView: WebView = binding.webView
            webView.settings.javaScriptEnabled = true  //open like web page
            if (url != null) {
                webView.loadUrl(url)
            }
        }

        binding.clDownladPdf.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE

            healthcareViewModel.downloadReportMedicalCard(
                MedicalCartonPdfRequest(
                    pin = userPin,
                    reportId = medicalCardObject.detail?.id
                )
            )

            healthcareViewModel.reportMedicalCardLiveData.observe(
                viewLifecycleOwner,
                Observer { response ->
                    if (response.isSuccessful) {
                        binding.progressBar.visibility = View.VISIBLE

                        val inputStream = response.body()?.byteStream()  //Response in PDF format
                        val file = File(
                            requireContext().cacheDir,
                            "${getString(R.string.fragment_bottom_sheet_show_pdf_review_doctors_report)}.pdf"
                        )

                        inputStream?.use { input ->
                            FileOutputStream(file).use { output ->
                                input.copyTo(output)
                            }
                        }

                        val fragment = ShowBinaryPdfAndDownloadFragment.newInstance(
                            file.absolutePath,
                            getString(R.string.fragment_bottom_sheet_show_pdf_review_doctors_report)
                        )
                        parentFragmentManager.beginTransaction().apply {
                            replace(R.id.home_fragment_container_view, fragment)
                            addToBackStack(null)
                            commit()
                        }

                        binding.progressBar.visibility = View.GONE
                    }
                })
        }

        binding.blurBackground.setOnClickListener(){
            MedicalCartonFragment.SharedCartonData.selectedCarton = "MedicalCarton"
            requireActivity().onBackPressed()
        }
    }
}