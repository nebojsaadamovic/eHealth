package com.example.zdravlje.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.nites.ezdravljeapp.databinding.DialogChatListFilterBinding

class ChatListFilterDialog(context: Context, private var parent: IChatFilterOptions) :
    Dialog(context) {

    private lateinit var binding: DialogChatListFilterBinding

    interface IChatFilterOptions {
        fun onShowDoctorsFilterDialog()
        fun onShowPharmacistsFilterDialog()
        fun onShowMyContactsFilterDialog()
        fun onShowChatGroupsFilterDialog()
        fun onShowRequestsReceivedFilterDialog()
        fun onShowAllFilterDialog()
    }

    init {
        setCancelable(true)
        binding = DialogChatListFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCanceledOnTouchOutside(true)

        // Define top and end margins
        val marginTop = 50 // Margin in dp
        val marginEnd = 20 // Margin in dp

        // Convert dp to pixels
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

        binding.tvShowDoctors.setOnClickListener(View.OnClickListener {
            parent.onShowDoctorsFilterDialog()
            dismiss()
        })
        binding.tvShowPharmacists.setOnClickListener(View.OnClickListener {
            parent.onShowPharmacistsFilterDialog()
            dismiss()
        })

        binding.tvShowMyContacts.setOnClickListener(View.OnClickListener {
            parent.onShowMyContactsFilterDialog()
            dismiss()
        })

        binding.tvShowChatGroups.setOnClickListener(View.OnClickListener {
            parent.onShowChatGroupsFilterDialog()
            dismiss()
        })

        binding.tvRequestsReceived.setOnClickListener(View.OnClickListener {
            parent.onShowRequestsReceivedFilterDialog()
            dismiss()
        })

        binding.tvRequestsReceived.setOnClickListener(View.OnClickListener {
            parent.onShowRequestsReceivedFilterDialog()
            dismiss()
        })

        binding.tvShowAll.setOnClickListener(View.OnClickListener {
            parent.onShowAllFilterDialog()
            dismiss()
        })

    }
}

