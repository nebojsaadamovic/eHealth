package com.example.zdravlje.ui.login.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nites.ezdravljeapp.databinding.FragmentAnswerBinding


class AnswerFragment : Fragment() {

    lateinit var binding: FragmentAnswerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAnswerBinding.inflate(layoutInflater, container, false)
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
    }
}