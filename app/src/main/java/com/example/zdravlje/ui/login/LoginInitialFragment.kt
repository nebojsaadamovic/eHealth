package com.example.zdravlje.ui.login

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.zdravlje.data.api.RetrofitInstance
import com.example.zdravlje.data.model.request.EidTokenRequest
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.LoginRepository
import com.example.zdravlje.data.sharedPref.PreferenceHelper.saveToken
import com.example.zdravlje.ui.login.anonimus.AnonimusLoginActivity
import com.example.zdravlje.ui.login.info.HelpAndSupportBottomSheetFragment
import com.example.zdravlje.utilities.LocalWebView
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentLoginInitialBinding
import java.util.Base64

class LoginInitialFragment : Fragment() {

    lateinit var binding: FragmentLoginInitialBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var dataStoreRepository: DataStoreRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginInitialBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        subscribeToObservers()
        requireActivity().window.navigationBarColor =
            ContextCompat.getColor(requireContext(), R.color.gray_E6E6E6)
        loadLoginEmailPasswordFragment()
        loginEID()
        regEid()
        helpAndSupport()
        loadLoginRfzoFragment()
        anonimusPage()
    }

    private fun anonimusPage() {
        binding.ivLogo.setOnClickListener(View.OnClickListener {
            startActivity(Intent(requireActivity(), AnonimusLoginActivity::class.java))
            requireActivity().finish()
        })
    }


    private fun setViewModels() {
        val loginRepository = LoginRepository()
        dataStoreRepository = DataStoreRepository(requireActivity())

        val loginViewModelFactory = LoginViewModelFactory(loginRepository, dataStoreRepository)

        loginViewModel = ViewModelProvider(
            requireActivity(),
            loginViewModelFactory
        ).get(LoginViewModel::class.java)
    }

    private fun loadLoginEmailPasswordFragment() {
        binding.mbLoginEZdravlje.setOnClickListener(View.OnClickListener {
            val fragmentLoginEmailPassword = LoginEmailPasswordFragment()
            requireActivity().supportFragmentManager.beginTransaction().apply {
                replace(R.id.home_fragment_container_view, fragmentLoginEmailPassword)
                addToBackStack(null)
                commit()
            }
        })
    }

    private fun loadLoginRfzoFragment() {
        binding.mtvLoginRegister.setOnClickListener(View.OnClickListener {
            val loginRfzoFragment = LoginRfzoFragment()
            requireActivity().supportFragmentManager.beginTransaction().apply {
                replace(R.id.home_fragment_container_view, loginRfzoFragment)
                addToBackStack(null)
                commit()
            }
        })
    }

    private fun loginEID() {
        binding.mbEId.setOnClickListener(View.OnClickListener {
            loginViewModel.getEidToken(EidTokenRequest())

        })
    }

    private fun regEid() {
        binding.mtvGovEId.setOnClickListener(View.OnClickListener {
            redirekcija("https://eid.gov.rs/sr-Latn-RS/pocetna")
        })
    }

    private fun helpAndSupport() {
        binding.mcvHelpSupport.setOnClickListener(View.OnClickListener {
            val helpAndSupportBottomSheetFragment = HelpAndSupportBottomSheetFragment()
            helpAndSupportBottomSheetFragment.show(
                requireActivity().supportFragmentManager,
                helpAndSupportBottomSheetFragment.tag
            )
        })
    }

    fun redirekcija(link: String) {
        val alert = AlertDialog.Builder(requireActivity())
        alert.setTitle("")

        val progressDialog = ProgressDialog(requireActivity())
        progressDialog.setCancelable(false)
        progressDialog.show()

        val wv = LocalWebView(requireActivity())
        wv.requestFocus(View.FOCUS_DOWN)

        wv.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_UP -> {
                    if (!v.hasFocus()) {
                        v.requestFocus()
                    }
                }
            }
            false
        }

        wv.clearCache(true)
        wv.clearFormData()
        wv.clearHistory()
        wv.clearSslPreferences()
        wv.loadUrl(link)

        wv.settings.apply {
            allowContentAccess = true
            allowFileAccess = true
            databaseEnabled = true
            javaScriptEnabled = true
            domStorageEnabled = true
            loadsImagesAutomatically = true
            builtInZoomControls = true
            setSupportZoom(true)
            useWideViewPort = true
            loadWithOverviewMode = true
        }

        wv.isFocusable = true
        wv.isFocusableInTouchMode = true

        var alertDialog: AlertDialog? = null

        wv.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return when {
                    "token" in url -> {
                        wv.stopLoading()
                        val tokenStart = url.indexOf("token") + 6
                        val tokenEnd = url.indexOf("&sing")
                        //     val logoutStart = url.indexOf("&sing") + 45
                        //    val logoutEnd = url.indexOf("&post")

                        val token = url.substring(tokenStart, tokenEnd)
                        //     val logout = url.substring(logoutStart, logoutEnd)

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            testDecodeJWT(requireActivity(), token)
                            alertDialog?.dismiss()
                            progressDialog.dismiss()
                        }

                        //   eidLogout("$logout/")
                        //    returnQrToken(token)
                        false
                    }

                    "authenticationendpoint" in url -> {
                        progressDialog.dismiss()
                        view.loadUrl(url)
                        true
                    }

                    else -> {
                        view.loadUrl(url)
                        true
                    }
                }
            }
        }

        alert.setView(wv)
        alert.setNegativeButton("odustani") { dialog, _ -> dialog.dismiss() }
        alertDialog = alert.create() // ✳️ Kreiramo dijalog
        alertDialog.show()
    }


    fun testDecodeJWT(context: Context, jwtToken: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Log.w("JWT", "Decoding JWT requires API 26+")
            return
        }

        val decoder = Base64.getUrlDecoder()
        val parts = jwtToken.split(".")
        if (parts.size < 2) {
            Log.w("JWT", "Invalid JWT format")
            return
        }

        try {
            val payloadJson = String(decoder.decode(parts[1]), Charsets.UTF_8)

            var aal: Int? = null
            var ial: Int? = null

            // Regex za AAL i IAL
            val aalMatch = "\"AAL\"\\s*:\\s*(\\d)".toRegex().find(payloadJson)
            val ialMatch = "\"IAL\"\\s*:\\s*(\\d)".toRegex().find(payloadJson)

            aal = aalMatch?.groupValues?.getOrNull(1)?.toIntOrNull()
            ial = ialMatch?.groupValues?.getOrNull(1)?.toIntOrNull()

            val isFullAuth = (aal ?: 0) > 1 && (ial ?: 0) > 1

                RetrofitInstance.AUTH_TOKEN = jwtToken;
                saveToken(requireContext(), jwtToken)
                val pinFragment = PinFragment()
                requireFragmentManager().beginTransaction().apply {
                    replace(R.id.home_fragment_container_view, pinFragment)
                    addToBackStack(null)
                    commit()
                }


            // Snimi u SharedPreferences
            //  val prefs = context.getSharedPreferences("tokenPref", Context.MODE_PRIVATE)
            // prefs.edit().putBoolean("2fa", isFullAuth).apply()

            Log.d("JWT", "Decoded AAL: $aal, IAL: $ial, FullAuth: $isFullAuth")
        } catch (e: Exception) {
            Log.e("JWT", "Error decoding JWT", e)
        }
    }


    private fun subscribeToObservers() {
        loginViewModel.eidTokenLiveData.observe(requireActivity()) { response ->
            if (response.isSuccessful) {
                response.body()?.data?.let { redirekcija(it) }
            } else {
                Toast.makeText(requireContext(), getString(R.string.error), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


}