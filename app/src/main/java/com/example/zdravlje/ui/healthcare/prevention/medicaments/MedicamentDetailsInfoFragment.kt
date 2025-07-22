package com.example.zdravlje.ui.healthcare.prevention.medicaments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.zdravlje.data.model.response.healthcare.MedicamenteResponse
import com.nites.ezdravljeapp.databinding.FragmentMedicamentDetailsInfoBinding

class MedicamentDetailsInfoFragment(
    private var data: MedicamenteResponse
) : Fragment() {
    private lateinit var binding: FragmentMedicamentDetailsInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMedicamentDetailsInfoBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.mtvProtectedNameValue.text = data.cbmedalimsNazivLeka
        binding.mtvINNValue.text = data.cbmedInn
        binding.mtvMakerValue.text = data.cbmedalimsProizvodjac
        binding.mtvDateExpiriedDecisionValue.text = data.cbmedalimsDatumVazenjaResenja
        binding.mtvLicenseHolderValue.text = data.cbmedalimsNosilacDozvole
        binding.mtvATCValue.text = data.cbmedalimsAtc
        binding.mtvJKLValue.text = data.cbmedalimsJkl
        binding.mtvTypeMedicineValue.text = data.cbmedalimsVrstaLeka
        binding.mtvReleaseModeValue.text = data.cbmedalimsRezimIzdavanjaLeka
        binding.mtvListNameValue.text = data.cbmedrfzoNazivListe
        binding.mtvPriceMedicineValue.text = data.getRoundedPriceMedicamente()

        binding.mtvDiagnosisDescriptionValue.text = data.cbmedrfzoOpisDijagnoze
        binding.mtvParticipationValue.text = data.cbmeddetParticipation
        binding.mtvAgeFromValue.text = data.cbmedrfzoGodineOd
        binding.mtvAgeToValue.text = data.cbmedrfzoGodineDo
        binding.mtvMaxQuantityValue.text = data.cbmedrfzoKolicinaLeka

        binding.ivBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }
    }
}