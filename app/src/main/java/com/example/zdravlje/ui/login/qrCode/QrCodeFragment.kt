package com.example.zdravlje.ui.login.qrCode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentLoginQrCodeBinding


class QrCodeFragment : Fragment() {

    lateinit var binding: FragmentLoginQrCodeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginQrCodeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mbNext.setOnClickListener(View.OnClickListener {
            val fragmentQrCodeScannerFragment = QrCodeScannerFragment()
            requireFragmentManager().beginTransaction().apply {
                replace(R.id.qr_code_fragment_container_view, fragmentQrCodeScannerFragment)
                addToBackStack(null)
                commit()
            }
        })

    }
}