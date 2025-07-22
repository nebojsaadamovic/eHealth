package com.example.zdravlje.ui.login.anonimus

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nites.ezdravljeapp.R


class EnterEmailPasswordBottomSheetFragment : Fragment() {



    interface OnEnterUserNameListener {
        fun onEnterUserName(email: String, password:String)
    }

    private var listener: OnEnterUserNameListener? = null


    fun setOnUserNameListener(listener: OnEnterUserNameListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            R.layout.fragment_enter_email_password_bottom_sheet,
            container,
            false
        )
    }


}