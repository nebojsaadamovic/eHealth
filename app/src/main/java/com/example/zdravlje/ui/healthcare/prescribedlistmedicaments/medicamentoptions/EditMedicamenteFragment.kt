package com.example.zdravlje.ui.healthcare.prescribedlistmedicaments.medicamentoptions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.zdravlje.data.model.response.healthcare.PrescribedListMedicamentResponse
import com.example.zdravlje.ui.calendar.BottomSheetInsertValue
import com.nites.ezdravljeapp.databinding.FragmentEditMedicamenteBinding

class EditMedicamenteFragment(
    private val prescribedObject: PrescribedListMedicamentResponse
) : Fragment(),BottomSheetInsertValue.OnValueListener {

    private var fieldTag: String? = null
    private var title: String? = null
    private var userPin: String? = null

    private lateinit var binding: FragmentEditMedicamenteBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditMedicamenteBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {

        binding.mtvNameMedicamenteValue.text=prescribedObject.medicamentName
        binding.mtvDosageValue.text=prescribedObject.dosage
        binding.mtvQuantityValue.text=prescribedObject.quantity
        binding.mtvDiagnosesValue.text=prescribedObject.descriptionDagnosis
        binding.mtvPrescribedDoctorValue.text=prescribedObject.prescribed
       // binding.mtvRealizedInPharmacyValue.text
        //binding.mtvPharmacistValue.text=prescribedObject.
//        binding.mtvPeriodFrom.text=prescribedObject.
//        binding.mtvPeriodTo.text=prescribedObject.

        binding.mtvDosageValue.setOnClickListener(){
            fieldTag = "dosage"
            title = "Unesite dozu"
            openEnterValueBottomSheet()
            }


    }

    override fun onValueChange(value: String) {

    }

    private fun openEnterValueBottomSheet() {
        val bSiV = BottomSheetInsertValue().apply {
            arguments = Bundle().apply {
                putString("title", title)
            }
        }
        bSiV.setOnValueListener(this, fieldTag = fieldTag ?: "")
        bSiV.show(requireActivity().supportFragmentManager, bSiV.tag)
    }
}