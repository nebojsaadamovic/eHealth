package com.example.zdravlje.ui.login.anonimus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.zdravlje.data.model.request.AnonimusLoginRequest
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.LoginRepository
import com.example.zdravlje.services.di.MainViewModel
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.login.LoginViewModel
import com.example.zdravlje.ui.login.LoginViewModelFactory
import com.nites.ezdravljeapp.databinding.FragmentAnonimusIntroBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class AnonimusIntroFragment : BaseFragment(), EnterUserNameBottomSheet.OnEnterUserNameListener {


    lateinit var binding: FragmentAnonimusIntroBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var mainViewModel: MainViewModel
    private lateinit var anonimusActivity: AnonimusLoginActivity;


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAnonimusIntroBinding.inflate(layoutInflater)
        return binding.root
        // Inflate the layout for this fragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        anonimusActivity = requireActivity() as AnonimusLoginActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        subscribeToObservers()
        setupUI()
    }

    private fun setupUI() {

        binding.mbGuest.setOnClickListener(View.OnClickListener {
            val enterUserNameBottomSheet = EnterUserNameBottomSheet()
            enterUserNameBottomSheet.setOnUserNameListener(this)
            enterUserNameBottomSheet.show(parentFragmentManager, enterUserNameBottomSheet.tag)
        })

        binding.mbLoginEZdravlje.setOnClickListener(View.OnClickListener {

        })
    }

    private fun setViewModels() {
        val loginRepository = LoginRepository()
        dataStoreRepository = DataStoreRepository(requireActivity())

        val loginViewModelFactory = LoginViewModelFactory(loginRepository, dataStoreRepository)
        mainViewModel = anonimusActivity.getMainViewModel()


        loginViewModel = ViewModelProvider(
            requireActivity(),
            loginViewModelFactory
        ).get(LoginViewModel::class.java)
    }

    private fun subscribeToObservers() {

        binding.mbLoginEZdravlje.setOnClickListener(View.OnClickListener {
            if (isTokenSet()){
                showProgressBar()
                val activity = requireActivity() as AnonimusLoginActivity
                activity.startAVCSdk(getStoredToken(), "sr", getStoredFcmToken(), "2")
                lifecycleScope.launch {
                    delay(4000)
                    mainViewModel.acceptCall(anonimusActivity.getChatId(), false)
                }
            }
        })

        loginViewModel.anonimusLoginLiveData.observe(requireActivity()) { response ->
            if (response != "") {
                Toast.makeText(requireContext(), response, Toast.LENGTH_SHORT).show()
            }
            val activity = requireActivity() as AnonimusLoginActivity
            activity.startAVCSdk(response, "sr", "1", "2")

            mainViewModel.acceptCall(anonimusActivity.getChatId(), false)
         //   mainViewModel.acceptCall("28adab54-1905-4e91-b03b-319a69719052", false)
            hideProgressBar()
        }
    }

    override fun onEnterUserName(address: String) {
      //  Toast.makeText(requireContext(), address, Toast.LENGTH_SHORT).show()
        showProgressBar()
        loginViewModel.anonimusLogin(
            AnonimusLoginRequest(
                anonimusActivity.getChatId(),
               // "28adab54-1905-4e91-b03b-319a69719052",
                address
            )
        )
    }

  //  https://hepor.nites.rs/avc/4d1f9d5f-7015-426f-b257-274817fbeabb
    //https://hepor.nites.rs/avc/28adab54-1905-4e91-b03b-319a69719052

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
        binding.clContent.visibility=View.GONE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
        binding.clContent.visibility=View.VISIBLE


    }

}