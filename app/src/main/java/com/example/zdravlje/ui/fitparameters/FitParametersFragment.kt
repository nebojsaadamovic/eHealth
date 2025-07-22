package com.example.zdravlje.ui.fitparameters

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.login.LoginActivity
import com.example.zdravlje.ui.login.PinBottomSheetFragment
import com.google.android.material.button.MaterialButton
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentFitParametersBinding

class FitParametersFragment : BaseFragment(), PinBottomSheetFragment.OnPinListener {

    private lateinit var dataStoreRepository: DataStoreRepository
    lateinit var binding: FragmentFitParametersBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFitParametersBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        subscribeToObservers()
        setupUI()
    }

    private fun setViewModels() {
        dataStoreRepository = DataStoreRepository(requireActivity())
    }

    fun subscribeToObservers() {
        if (isPinSet()) {
            binding.wrapper.visibility = View.GONE
            binding.wrapperTwo.visibility = View.VISIBLE
        } else {
            binding.wrapper.visibility = View.VISIBLE
        }
    }

    private fun setupUI() {
        val mbpinCode =
            binding.wrapper.findViewById<com.google.android.material.button.MaterialButton>(R.id.mbEnterPinCode)
        mbpinCode.setOnClickListener {
            val pinBottomSheetFragment = PinBottomSheetFragment()
            pinBottomSheetFragment.setOnPinListener(this)
            pinBottomSheetFragment.show(parentFragmentManager, pinBottomSheetFragment.tag)
        }
        val ivBackButton = binding.wrapper.findViewById<ImageView>(R.id.ivBack)
        ivBackButton.setOnClickListener {
            requireActivity().onBackPressed()
        }


        val mbLogInButton = binding.wrapper.findViewById<MaterialButton>(R.id.mbLogIn)
        mbLogInButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        })

        val ivUnderConstruction =
            binding.wrapperTwo.findViewById<ImageView>(R.id.iv_underConstruction)
        ivUnderConstruction.setOnClickListener() {
            binding.wrapperTwo.visibility = View.GONE
            val wayOfLifeFragment = WayOfLifeFragment()
            requireActivity().supportFragmentManager.beginTransaction().replace(
                R.id.home_fragment_container_view, wayOfLifeFragment
            ).addToBackStack(null).commit()
        }
    }

    override fun onPinChange() {
        binding.wrapper.visibility = View.GONE
        binding.wrapperTwo.visibility = View.VISIBLE
    }

    override fun onPinDismiss() {
    }
}
