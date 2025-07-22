package com.example.zdravlje.ui.base.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.zdravlje.ui.base.BaseBottomSheetDialogFragment
import com.example.zdravlje.ui.calendar.nextOnSchedule.BottomSheetRealizedUnrealizedActivityFragment.OnValueChangeListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentBottomSheetSessionBinding

class BottomSheetSessionFragment : BaseBottomSheetDialogFragment() {

    private  lateinit var binding : FragmentBottomSheetSessionBinding
    private var listener: OnValueChangeListener? = null

    interface OnValueChangeListener {
        fun onValueChange()
    }

    fun setValueChange(listener: OnValueChangeListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentBottomSheetSessionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }


    private fun setupUI(){
        binding.mbLogin.setOnClickListener(){
            listener?.onValueChange()
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
    }

}