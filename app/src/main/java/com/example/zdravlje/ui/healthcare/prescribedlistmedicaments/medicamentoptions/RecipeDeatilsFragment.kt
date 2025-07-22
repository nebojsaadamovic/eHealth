package com.example.zdravlje.ui.healthcare.prescribedlistmedicaments.medicamentoptions

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.zdravlje.data.model.response.healthcare.PatmedNotes
import com.example.zdravlje.data.model.response.healthcare.PrescribedListMedicamentResponse
import com.example.zdravlje.ui.healthcare.DownloadPdfFileFragment
import com.example.zdravlje.utilities.DateUtils
import com.google.gson.Gson
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentRecipeDeatilsBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class RecipeDeatilsFragment(
    private val prescribedObject: PrescribedListMedicamentResponse
) : Fragment() {

    private lateinit var binding: FragmentRecipeDeatilsBinding
    private var urlCharacteristicsMedicamente: String? = null
    private var urlInstructionManual: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecipeDeatilsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun setupUI() {
        if (prescribedObject != null) {
            val gson = Gson()
            val payload = gson.fromJson(prescribedObject.patmedNotes, PatmedNotes::class.java)

            binding.mtvProtectedNameValue.text = prescribedObject.medicamentProtectedName ?: "/"
            binding.mtvINNValue.text = prescribedObject.inn ?: "/"

            if (!payload.manufacturer.isNullOrEmpty()){
                binding.mtvMakerValue.text = payload.manufacturer
            }else{
                binding.mtvMakerValue.text = prescribedObject.companyMaker ?: "/"
            }

            binding.mtvValidityDateDecisionValue.text = prescribedObject.cbmedalimsDecisionValidityDate?.let {
                    DateUtils.formatDate_VerLineyyyyMMdd_DotddMMyyyy(it)
                } ?: "/"
            binding.mtvLicenseHolderValue.text = prescribedObject.licenseHolder ?: "/"
            binding.mtvATCValue.text = prescribedObject.atc ?: "/"
            binding.mtvJKLValue.text = prescribedObject.jkl ?: "/"
            binding.mtvTypeMedicamenteValue.text = prescribedObject.typeMedicamente ?: "/"

            val regimeDispensing = when(prescribedObject.regimeDispensing) {
                "R" -> getString(R.string.fragment_recipe_deatils_regime_dispensing_r)
                "BR" -> getString(R.string.fragment_recipe_deatils_regime_dispensing_br)
                null -> "/"
                else -> prescribedObject.regimeDispensing
            }

            binding.mtvRegimeDispensingValue.text = regimeDispensing
            binding.mtvListNameValue.text = prescribedObject.listName  ?: "/"
            binding.mtvPriceMendicamenteValue.text =
                prescribedObject.priceMedicamenete?.takeIf { it.isNotBlank() }?.toDoubleOrNull()?.let {
                    String.format("%.2f", it) + " RSD" } ?: "/"

            val participationValue = (prescribedObject.participation?.toDoubleOrNull() ?: 0.0) * 100
            val rounded = participationValue.roundToInt().toString()
            val firstTwoDigits = if (rounded.length > 2) rounded.take(2) else rounded
            binding.mtvParticipiantValue.text = "$firstTwoDigits%"
        }

        binding.ivBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }

        binding.mbCharacteristicsMedicamente.setOnClickListener() {
            urlCharacteristicsMedicamente = prescribedObject.characteristicsMedicamente
            val titleFromResources =
                getString(R.string.fragment_recipe_details_characteristics_medicamente)
            if (urlCharacteristicsMedicamente != null) {   // Show and download PDF with Url
                val downloadPdfFileFragment =
                    DownloadPdfFileFragment.newInstance(
                        urlCharacteristicsMedicamente!!,
                        titleFromResources
                    )

                parentFragmentManager.beginTransaction().apply {
                    replace(R.id.home_fragment_container_view, downloadPdfFileFragment)
                    addToBackStack(null)
                    commit()
                }
            } else {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.recipe_details_fragment_file_is_empty),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.mbInstructionManual.setOnClickListener() {
            urlInstructionManual = prescribedObject.instructionManual
            val titleFromResources = getString(R.string.fragment_recipe_details_instruction_manual)
            if (urlInstructionManual != null) {
                val downloadPdfFileFragment =
                    DownloadPdfFileFragment.newInstance(urlInstructionManual!!, titleFromResources)

                parentFragmentManager.beginTransaction().apply {
                    replace(R.id.home_fragment_container_view, downloadPdfFileFragment)
                    addToBackStack(null)
                    commit()
                }
            } else {
                Toast.makeText(requireActivity(),
                    getString(R.string.recipe_details_fragment_file_is_empty),
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
}