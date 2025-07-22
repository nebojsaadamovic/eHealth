package com.example.zdravlje.ui.profile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentProfileAccessKeyBinding


class ProfileAccessKeyFragment : Fragment() {

    lateinit var binding: FragmentProfileAccessKeyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileAccessKeyBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
    }

    fun setupUi() {
        binding.copy.setOnClickListener(View.OnClickListener {
            val textToCopy =
                "" + binding.tvUserNameValue.text + " " + binding.tvPasswordValue.text + " " + binding.tvPinValue.text
            copyText(textToCopy)
        })

        binding.ivBack.setOnClickListener(View.OnClickListener {
            requireActivity().onBackPressed()
        })
    }

    private fun copyText(text: String) {
        val myClipboard =
            requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val myClip: ClipData = ClipData.newPlainText("Label", text)
        myClipboard.setPrimaryClip(myClip)
        Toast.makeText(
            requireContext(),
            getString(R.string.fragment_profile_access_key_copied),
            Toast.LENGTH_SHORT
        ).show()
    }
}