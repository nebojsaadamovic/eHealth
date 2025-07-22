package com.example.zdravlje.ui.healthcare.prescribedlistmedicaments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.zdravlje.data.model.request.healthcare.DeleteMedicamenteRequest
import com.example.zdravlje.data.model.response.healthcare.PrescribedListMedicamentResponse
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.calendar.nextOnSchedule.BottomSheetConfirmDeleteReminderFragment
import com.example.zdravlje.ui.healthcare.HealthcareViewModel
import com.example.zdravlje.ui.healthcare.HealthcareViewModelFactory
import com.example.zdravlje.ui.healthcare.prescribedlistmedicaments.medicamentoptions.AddMedicmentPrescribedListFragment
import com.example.zdravlje.ui.healthcare.prescribedlistmedicaments.medicamentoptions.AddReminderFragment
import com.example.zdravlje.ui.healthcare.prescribedlistmedicaments.medicamentoptions.LimitationsFragment
import com.example.zdravlje.ui.healthcare.prescribedlistmedicaments.medicamentoptions.MedicamentDetailsFragment
import com.example.zdravlje.ui.healthcare.prescribedlistmedicaments.medicamentoptions.RecipeDeatilsFragment
import com.example.zdravlje.utilities.CommonUtils
import kotlinx.coroutines.launch
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentPrescribedMedicamentOptionsBinding

class PrescribedMedicamentOptionsFragment(
    private val prescribedObject: PrescribedListMedicamentResponse
) : BaseFragment(), BottomSheetConfirmDeleteReminderFragment.OnCheckedListener {

    lateinit var binding: FragmentPrescribedMedicamentOptionsBinding
    private lateinit var healthcareViewModel: HealthcareViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    var userPin: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPrescribedMedicamentOptionsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewModels()
        subscribeToObservers()
        setupUI()
    }

    private fun setupUI() {

        binding.blurBackground.setOnClickListener() {
            requireActivity().onBackPressed()
        }

        binding.clDetailsRecipe.setOnClickListener() {
            CommonUtils.openFragmentHome(requireActivity(), MedicamentDetailsFragment(prescribedObject)) // this is details about recipe
        }

        binding.clMedicamentDetails.setOnClickListener() {
            CommonUtils.openFragmentHome(requireActivity(), RecipeDeatilsFragment(prescribedObject))  // this is details about medicamente
        }

        binding.clLimitations.setOnClickListener() {
            CommonUtils.openFragmentHome(requireActivity(), LimitationsFragment(prescribedObject))
        }

        binding.clAddReminder.setOnClickListener() {
            CommonUtils.openFragmentHome(requireActivity(), AddReminderFragment(prescribedObject))
        }

        if (prescribedObject.prescriptionType?.toInt() == 3) {
            binding.clEdit.visibility = View.VISIBLE
            binding.clDelete.visibility = View.VISIBLE
        }

        binding.clEdit.setOnClickListener() {
            val fragment = AddMedicmentPrescribedListFragment(prescribedObject).apply {
                arguments = Bundle().apply {
                    putString("edit_medicamente", "edit_medicamente")
                }
            }

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.home_fragment_container_view, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.clDelete.setOnClickListener() {
            val bottomSheet = BottomSheetConfirmDeleteReminderFragment()
            bottomSheet.setCheckedListener(this)
            bottomSheet.setTextSource("DeleteMedicamente-PrescribedMedicamentOptionsFragment")
            bottomSheet.show(requireActivity().supportFragmentManager, bottomSheet.tag)
        }
    }

    override fun onChecked(isChecked: Int) {
        if (isChecked == 0) {
            if (isPinSet()){
                userPin=getStoredPin()
                healthcareViewModel.deleteManualMedicamente(
                    DeleteMedicamenteRequest(
                        prescribedObject.patmedId,
                        pin = userPin
                    )
                )
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
    }

    private fun subscribeToObservers() {
        healthcareViewModel.deleteMedicamenteManualLiveData.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.data_deleted_successfully),
                    Toast.LENGTH_SHORT
                ).show()
                CommonUtils.openFragmentHome(requireActivity(), PrescribedListMedicamentsFragment())
            }
        }

        healthcareViewModel.deleteMedicamenteManualErrorLiveData.observe(viewLifecycleOwner) { response ->
//            Toast.makeText(
//                requireContext(),
//                "${getString(R.string.error)} ${response}",
//                Toast.LENGTH_SHORT
//            ).show()
        }
    }
}