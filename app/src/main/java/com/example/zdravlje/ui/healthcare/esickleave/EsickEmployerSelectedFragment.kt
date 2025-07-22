package com.example.zdravlje.ui.healthcare.esickleave

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.model.response.healthcare.Employer
import com.example.zdravlje.data.model.response.healthcare.SickLeaveNoteResponse
import com.example.zdravlje.data.model.response.healthcare.SickLeaveResponse
import com.example.zdravlje.ui.adaptersAndViewHolders.healthcare.EmployersAdapter
import com.example.zdravlje.utilities.CommonUtils
import com.nites.ezdravljeapp.databinding.FragmentEsickEmployerSelectedBinding

class EsickEmployerSelectedFragment(
    private var listEmployers: List<Employer>?=null,
    private val sickLeaveResponse: SickLeaveResponse?=null,
    private val userPin: String?=null
) : Fragment() ,EmployersAdapter.OnInstitutionListener {

    private lateinit var binding: FragmentEsickEmployerSelectedBinding
    private lateinit var employersAdapter: EmployersAdapter
    private var employerObject: Employer?=null
    private var sickLeaveNotes: List<SickLeaveNoteResponse> ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEsickEmployerSelectedBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.ivBack.setOnClickListener(){
            requireActivity().onBackPressed()
        }

        binding.mtvChooseBtn.setOnClickListener() {
            CommonUtils.openFragmentHome(
                requireActivity(),
                ESickDocumentsFragment(sickLeaveResponse, sickLeaveNotes, employerObject, userPin)
            )
        }

        employersAdapter = listEmployers?.let { EmployersAdapter(it, parentFragmentManager) }!!
        employersAdapter.setOnInstitutionListener(this)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvEmployersList.layoutManager = linearLayoutManager
        binding.rvEmployersList.adapter = employersAdapter
    }

    override fun onInstitutionSelected(employerObject: Employer, sickLeaveNotes: List<SickLeaveNoteResponse>) {
       this.employerObject=employerObject
        this.sickLeaveNotes = sickLeaveNotes
    }
}