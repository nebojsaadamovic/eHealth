package com.example.zdravlje.ui.healthcare.prevention

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.healthcare.prevention.commonsthemes.CommonsThemesFragment
import com.example.zdravlje.ui.healthcare.prevention.interactions.InteractionFragment
import com.example.zdravlje.ui.healthcare.prevention.medicaments.MedicamentsFragment
import com.example.zdravlje.ui.healthcare.prevention.mkb10.MKB10Fragment
import com.example.zdravlje.ui.healthcare.prevention.questionsanswers.QuestionsAnswersFragment
import com.example.zdravlje.ui.healthcare.prevention.recommendations.RecommendationsFragment
import com.example.zdravlje.ui.login.LoginActivity
import com.example.zdravlje.ui.login.PinBottomSheetFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentPreventionBinding

class PreventionFragment : BaseFragment(), PinBottomSheetFragment.OnPinListener {

    private lateinit var binding: FragmentPreventionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPreventionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        if (isPinSet()) {
            binding.mcvCommonThemes.setOnClickListener() {
                openFragment(CommonsThemesFragment())
            }

            binding.mcvQuestionsAnswers.setOnClickListener() {
                openFragment(QuestionsAnswersFragment())
            }

            binding.mcvRecommendations.setOnClickListener() {
                openFragment(RecommendationsFragment())
            }

            binding.mcvMedicaments.setOnClickListener() {
                openFragment(MedicamentsFragment())
            }

            binding.clInteractions.setOnClickListener() {
                openFragment(InteractionFragment())
            }

            binding.mcvMkb10.setOnClickListener() {
                openFragment(MKB10Fragment())
            }

            binding.ivBack.setOnClickListener() {
                requireActivity().onBackPressed()
            }
        } else {

            binding.wrapper.visibility = View.VISIBLE

            val mbpinCode =
                binding.wrapper.findViewById<MaterialButton>(R.id.mbEnterPinCode)
            mbpinCode.setOnClickListener {
                val pinBottomSheetFragment = PinBottomSheetFragment()
                pinBottomSheetFragment.setOnPinListener(this)
                pinBottomSheetFragment.show(parentFragmentManager, pinBottomSheetFragment.tag)
            }

            val mtvNetwork = binding.wrapper.findViewById<MaterialTextView>(R.id.mtvNetwork)
            mtvNetwork.text = getString(R.string.fragment_healthcare_prevention_title)

            val mtvDescription = binding.wrapper.findViewById<MaterialTextView>(R.id.mtvDescription)
            mtvDescription.text = getString(R.string.fragment_healthcare_prevention_description_txt)

            val imageMsg = binding.wrapper.findViewById<ImageView>(R.id.ivMsg)
            imageMsg.setImageResource(R.drawable.ic_prevention_wrapper)

            val mbLogInButton = binding.wrapper.findViewById<MaterialButton>(R.id.mbLogIn)
            mbLogInButton.setOnClickListener(View.OnClickListener {
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            })

            val ivBackButton = binding.wrapper.findViewById<ImageView>(R.id.ivBack)
            ivBackButton.setOnClickListener {
                requireActivity().onBackPressed()
            }
        }
    }

    private fun openFragment(nameFragment: Fragment) {
        val fragment = nameFragment
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.home_fragment_container_view, fragment)
            .addToBackStack(null).commit()
    }

    override fun onPinChange() {
        binding.wrapper.visibility = View.GONE
    }

    override fun onPinDismiss() {
        binding.wrapper.visibility = View.VISIBLE
    }
}