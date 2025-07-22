package com.example.zdravlje.ui.profile.sharedProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.zdravlje.ui.adaptersAndViewHolders.profile.SharedProfileAdapter
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentSharedProfileInitialBinding

class SharedProfileInitialFragment : Fragment() {

    lateinit var binding: FragmentSharedProfileInitialBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSharedProfileInitialBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewPager.adapter =
            SharedProfileAdapter(requireActivity().supportFragmentManager, requireContext())
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        tabPageListener()
        setupUI()
    }

    private fun setupUI() {
        binding.mtvAddPermissions.setOnClickListener() {
            val addPermissionsFragment = AddPermissionsFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(
                    R.id.shared_profile_fragment_container_view,
                    addPermissionsFragment
                ).addToBackStack(null).commit()

        }

        binding.ivBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }
    }

    private fun tabPageListener() {
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                binding.mtvAddPermissions.visibility =
                    if (position == 1) View.VISIBLE else View.GONE
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }
    override fun onResume() {
        super.onResume()
        binding.viewPager.adapter =
            SharedProfileAdapter(requireActivity().supportFragmentManager, requireContext())
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        tabPageListener()
        setupUI()
    }

}