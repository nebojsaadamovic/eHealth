package com.example.zdravlje.ui.healthcare.prevention.interactions

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.zdravlje.data.model.request.healthcare.FeedbackRequest
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.healthcare.HealthcareRepository
import com.example.zdravlje.ui.base.BaseBottomSheetDialogFragment
import com.example.zdravlje.ui.healthcare.HealthcareViewModel
import com.example.zdravlje.ui.healthcare.HealthcareViewModelFactory
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentBottomSheetLikesInteractionsBinding

class BottomSheetLikesInteractionsFragment(
    private var responseId: String,
    private var content: String
) : BaseBottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomSheetLikesInteractionsBinding
    private lateinit var healthcareViewModel: HealthcareViewModel
    private lateinit var dataStoreRepository: DataStoreRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetLikesInteractionsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        setupUI()
    }

    private fun setupUI(){
        binding.mbClose.setOnClickListener(){
            dismiss()
        }

        binding.ivLike.setOnClickListener(){
            healthcareViewModel.saveFeedback(20, FeedbackRequest(response_id = responseId, like = true ))
            dismiss()
        }

        binding.ivNotLike.setOnClickListener(){
            healthcareViewModel.saveFeedback(20, FeedbackRequest(response_id = responseId, like = false ))
            dismiss()
        }

        binding.ivEdit.setOnClickListener(){
            parentFragmentManager.setFragmentResult(
                "requestKey",
                Bundle().apply { putString("className", "BottomSheetLikesInteractionsFragment") }
            )
            dismiss()
        }

        binding.ivCopy.setOnClickListener(){
            copyToClipboard(content)
            Toast.makeText(requireContext(), getString(R.string.fragment_bottom_sheet_likes_interactions_copy_txt_msg), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
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

    private fun copyToClipboard(text: String) {
        val clipboard = requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("copied_text", text)
        clipboard.setPrimaryClip(clip)
    }
}