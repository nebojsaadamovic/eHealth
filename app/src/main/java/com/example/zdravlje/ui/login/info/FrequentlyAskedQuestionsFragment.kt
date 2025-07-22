package com.example.zdravlje.ui.login.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentFrequentlyAskedQuestionsBinding

class FrequentlyAskedQuestionsFragment : Fragment() {

    lateinit var binding: FragmentFrequentlyAskedQuestionsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFrequentlyAskedQuestionsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.ivBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }

        binding.ivNext.setOnClickListener(View.OnClickListener {
            val answerFragment = AnswerFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.home_fragment_container_view, answerFragment)
                .addToBackStack(null)
                .commit()
        })
        binding.ivNext1.setOnClickListener(View.OnClickListener {
            val answerFragment = AnswerFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.home_fragment_container_view, answerFragment)
                .addToBackStack(null)
                .commit()
        })
    }
}