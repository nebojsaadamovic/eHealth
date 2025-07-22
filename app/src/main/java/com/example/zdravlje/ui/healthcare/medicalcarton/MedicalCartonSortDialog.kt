package com.example.zdravlje.ui.healthcare.medicalcarton

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.nites.ezdravljeapp.databinding.DialogMedicalCartonSortBinding

class MedicalCartonSortDialog(context: Context, private var parent: IMedicalCartonSortOptions) :
    Dialog(context) {

    private lateinit var binding: DialogMedicalCartonSortBinding

    interface IMedicalCartonSortOptions {
        fun onShowSortDescendingDialog()
        fun onShowSortAscendingDialog()
    }

    init {
        setCancelable(true)
        binding = DialogMedicalCartonSortBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCanceledOnTouchOutside(true)

        val marginTop = 50
        val marginEnd = 20

        val scale = context.resources.displayMetrics.density
        val marginTopPx = (marginTop * scale + 0.5f).toInt()
        val marginEndPx = (marginEnd * scale + 0.5f).toInt()

        val layoutParams = window!!.attributes
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        layoutParams.gravity = Gravity.TOP or Gravity.END
        layoutParams.x = marginEndPx
        layoutParams.y = marginTopPx
        layoutParams.dimAmount = 0f

        window!!.attributes = layoutParams

        onInit()
    }

    fun onInit() {

        binding.tvShowDescending.setOnClickListener(View.OnClickListener {
            parent.onShowSortDescendingDialog()
            dismiss()
        })
        binding.tvShowAscending.setOnClickListener(View.OnClickListener {
            parent.onShowSortAscendingDialog()
            dismiss()
        })

    }
}

