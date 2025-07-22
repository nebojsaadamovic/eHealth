package com.example.zdravlje.ui.healthcare.prevention.commonsthemes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.zdravlje.data.model.response.healthcare.SocialGroupCategoryResponse
import com.nites.ezdravljeapp.databinding.FragmentPreventionInfoDetailsBinding

class PreventionInfoDetailsFragment(
    private val data: List<SocialGroupCategoryResponse>? = null,
    private val commonThemes: String? = null,
    private val content: String? = null,
) : Fragment() {

    private lateinit var binding: FragmentPreventionInfoDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPreventionInfoDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        if (commonThemes == "Commons Themes") {
            binding.ivMenu.visibility = View.VISIBLE
            binding.ivMenu.setOnClickListener() {
                val bottomSheetActionsFragment =
                    BottomSheetActionsFragment(data!!, parentFragmentManager)
                bottomSheetActionsFragment.show(
                    requireActivity().supportFragmentManager, bottomSheetActionsFragment.tag
                )
            }
        }

        binding.mtvContent.text = content

        binding.ivBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }
    }
}