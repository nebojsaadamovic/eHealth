package com.example.zdravlje.ui.healthcare.screeningexaminations

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zdravlje.data.model.healthcare.ScreeningExaminationsData
import com.example.zdravlje.ui.adaptersAndViewHolders.healthcare.ScreeningExaminationsAdapter
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentScreeningExaminationsBinding

class ScreeningExaminationsFragment(
    private var screeningExaminationsArr: Array<ScreeningExaminationsData>
) : Fragment() {

    lateinit var binding: FragmentScreeningExaminationsBinding
    lateinit var screeningExaminationsAdapter: ScreeningExaminationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScreeningExaminationsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showHideShimmer(true)
        if (!screeningExaminationsArr.isEmpty()){
            showHideShimmer(false)
            setAdapter()
        }else{
            binding.wrapperData.visibility=View.VISIBLE
            binding.reciclerViewScreeningExaminations.visibility=View.GONE
        }
        setupUI()
    }

    private fun setupUI() {
        binding.ivBack.setOnClickListener(){
            requireActivity().onBackPressed()
        }

        binding.ivPlus.setOnClickListener(){
            val screeningProgramFragment = ScreeningProgramFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.home_fragment_container_view,screeningProgramFragment)
                .addToBackStack(null).commit()
        }
    }

    private fun setAdapter() {
        screeningExaminationsAdapter = ScreeningExaminationsAdapter(screeningExaminationsArr, parentFragmentManager)
        val linearLayoutManager =
            object : LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false) {
                override fun canScrollVertically() = true
            }
        binding.reciclerViewScreeningExaminations.layoutManager = linearLayoutManager
        binding.reciclerViewScreeningExaminations.adapter = screeningExaminationsAdapter
    }

    private fun showHideShimmer(boolean: Boolean){
        if(boolean==true){
            binding.shimmer.visibility = View.VISIBLE
        }else{
            binding.shimmer.visibility = View.GONE
        }
    }
}