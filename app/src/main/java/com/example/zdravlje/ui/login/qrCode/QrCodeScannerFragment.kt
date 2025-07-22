package com.example.zdravlje.ui.login.qrCode

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.example.zdravlje.data.api.RetrofitInstance
import com.example.zdravlje.data.model.request.UserDeviceInfo
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.LoginRepository
import com.example.zdravlje.ui.home.HomeActivity
import com.example.zdravlje.ui.login.LoginViewModel
import com.example.zdravlje.ui.login.LoginViewModelFactory
import com.nites.ezdravljeapp.R

class QrCodeScannerFragment : Fragment() {

    private val RECORD_REQUEST_CODE = 101
    private lateinit var codeScanner: CodeScanner
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var activity: QrCodeActivity
    private var token = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_qr_code_scaner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity = requireActivity() as QrCodeActivity
        setViewModels()
        subscribeToObservers()

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            initScanner()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                RECORD_REQUEST_CODE
            )
        }
    }

    private fun initScanner() {
        val scannerView = requireView().findViewById<CodeScannerView>(R.id.scanner_view)
        codeScanner = CodeScanner(requireActivity(), scannerView)

        codeScanner.decodeCallback = DecodeCallback {
            requireActivity().runOnUiThread {
                token = it.text
                Log.d("QRCode", "Scanned token: $token")

                RetrofitInstance.AUTH_TOKEN = token
                loginViewModel.checkToken(
                    UserDeviceInfo("sr", activity.getStoredFcmToken(), 2)
                )
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }

        codeScanner.startPreview()
    }

    override fun onResume() {
        super.onResume()
        if (::codeScanner.isInitialized) {
            codeScanner.startPreview()
        }
    }

    override fun onPause() {
        if (::codeScanner.isInitialized) {
            codeScanner.releaseResources()
        }
        super.onPause()
    }

    private fun setViewModels() {
        val loginRepository = LoginRepository()
        dataStoreRepository = DataStoreRepository(requireActivity())
        val loginViewModelFactory = LoginViewModelFactory(loginRepository, dataStoreRepository)

        loginViewModel = ViewModelProvider(
            requireActivity(),
            loginViewModelFactory
        )[LoginViewModel::class.java]
    }

    private fun subscribeToObservers() {
        loginViewModel.generate6MonthTokenResponseLiveData.observe(requireActivity()) { response ->
            if (response.isNotEmpty()) {
                RetrofitInstance.AUTH_TOKEN = response
                startActivity(Intent(requireActivity(), HomeActivity::class.java))
                requireActivity().finish()
            }
        }

        loginViewModel.checkTokenLiveData.observe(requireActivity()) { response ->
            if (response) {
                activity.saveToken(token)
                startActivity(Intent(requireActivity(), HomeActivity::class.java))
                requireActivity().finish()
            } else {
                Toast.makeText(requireContext(), "Pogrešan token", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RECORD_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("QRCode", "Camera permission granted.")
                initScanner()
            }
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA).not()) {
                // User checked "Don't ask again"
                startActivity(Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = android.net.Uri.fromParts("package", requireContext().packageName, null)
                })
            }
            else {
                Log.d("QRCode", "Camera permission denied.")
                Toast.makeText(requireContext(), "Potrebna je dozvola za kameru.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
