package com.example.zdravlje.ui.profile

import android.content.Intent
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.profile.settings.ProfileSettings
import com.example.zdravlje.ui.profile.sharedProfile.SharedProfileActivity
import com.nites.ezdravljeapp.databinding.FragmentBottomSheetProfileInitialBinding

class ProfileInitialFragment : BaseFragment() {

    lateinit var binding: FragmentBottomSheetProfileInitialBinding
    private val TAG = ProfileInitialFragment::class.java.simpleName

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetProfileInitialBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val blur = RenderEffect.createBlurEffect(30f, 30f, Shader.TileMode.CLAMP)
            binding.blurBackground.setRenderEffect(blur)
        }

        binding.clUserProfile.setOnClickListener(View.OnClickListener {
            val userInfoFragment = UserInfoFragment()
            val activity = requireActivity() as ProfileActivity
            activity.startNewFragmentFromProfile(userInfoFragment)
        })

        binding.clUserSettings.setOnClickListener(View.OnClickListener {
            val profileSettings = ProfileSettings()
            val activity = requireActivity() as ProfileActivity
            activity.startNewFragmentFromProfile(profileSettings)
        })

        binding.clUserSharedProfile.setOnClickListener(View.OnClickListener {
            startActivity(Intent(requireActivity(), SharedProfileActivity::class.java))
        })

        binding.clSink.setOnClickListener(View.OnClickListener {
            val profileSinkFragment = ProfileSinkFragment()
            val activity = requireActivity() as ProfileActivity
            activity.startNewFragmentFromProfile(profileSinkFragment)
        })

        binding.blurBackground.setOnClickListener() {
            requireActivity().finish()
        }

        if(!isPinSet()){
            binding.clSink.visibility = View.GONE
        }
    }
}