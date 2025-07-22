package com.example.zdravlje.ui.healthcare.prevention.questionsanswers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.zdravlje.data.model.response.healthcare.QAResponse
import com.nites.ezdravljeapp.databinding.FragmentQuestionsDetailsBinding

class QuestionsDetailsFragment(
    private var allQAArray: Array<QAResponse>,
) : Fragment() {

    private lateinit var binding: FragmentQuestionsDetailsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding=FragmentQuestionsDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       setupUI()
    }

    private fun setupUI(){
        binding.mtvInstitutionDescription.text = allQAArray.firstOrNull()?.citengInstitutionName
        binding.mtvCategoryDescription.text = allQAArray.firstOrNull()?.category
        binding.mtvQuestionDescription.text = allQAArray.firstOrNull()?.citengQuestion
        binding.mtvAnswerDescription.text = allQAArray.firstOrNull()?.citengAnswer

        binding.ivBack.setOnClickListener(){
            requireActivity().onBackPressed()
        }
    }
}