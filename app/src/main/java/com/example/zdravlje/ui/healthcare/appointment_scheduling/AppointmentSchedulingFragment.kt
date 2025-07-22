package com.example.zdravlje.ui.healthcare.appointment_scheduling

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.ViewPager
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.calendar.apointment.CalendarAppointmentExaminationFragment
import com.example.zdravlje.ui.chat.PinChatBottomSheetFragment
import com.example.zdravlje.ui.login.LoginActivity
import com.example.zdravlje.utilities.CommonUtils
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textview.MaterialTextView
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentAppointmentSchedulingBinding

class AppointmentSchedulingFragment(
    private val className: String? = null
) : BaseFragment(), PinChatBottomSheetFragment.OnPinChatListener{

    private lateinit var binding: FragmentAppointmentSchedulingBinding
    private lateinit var myFragmentPagerAdapter: MyFragmentPagerAdapter
    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAppointmentSchedulingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val className = arguments?.getString("class_name")
        if (className == "CalendarOptionsFragment") {
            childFragmentManager.beginTransaction()
                .replace(R.id.history_appointment_fragment_container, ScheduledAppointmentsFragment())
                .commit()
        } else {
            viewPager = binding.viewPager
            tabLayout = binding.tabLayout
            binding.tabLayout.setupWithViewPager(binding.viewPager)
            setPagerAdapter()
            setTabLayout();
        }

        setupUI()
    }

    private fun setTabLayout() {
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.getTabAt(0)!!.setText(getString(R.string.fragment_appointments_scheduling_tab_scheduled_appointments))
        tabLayout.getTabAt(1)!!.setText(getString(R.string.fragment_appointments_scheduling_tab_appointment_history))
    }

    private fun setPagerAdapter() {
        myFragmentPagerAdapter = MyFragmentPagerAdapter(childFragmentManager) // ← OVA LINIJA
        viewPager.adapter = myFragmentPagerAdapter
    }

    private fun setupUI() {
        if (isPinSet()) {
            binding.wrapper.visibility = View.GONE
            binding.ivAddAppointmentScheduling.setOnClickListener() {
                if (className == "HealthcareFragment") {
                    CommonUtils.openFragmentHome(requireActivity(), CalendarAppointmentExaminationFragment(className))
                } else {
                    CommonUtils.openFragmentCalendar(requireActivity(), CalendarAppointmentExaminationFragment())
                }
            }

        } else {
            binding.wrapper.visibility = View.VISIBLE

            val mbpinCode =
                binding.wrapper.findViewById<MaterialButton>(R.id.mbEnterPinCode)
            mbpinCode.setOnClickListener {
                val pinChatBottomSheetFragment = PinChatBottomSheetFragment()
                pinChatBottomSheetFragment.setOnPinChatListener(this)
                pinChatBottomSheetFragment.show(parentFragmentManager, pinChatBottomSheetFragment.tag)
            }

            val mtvNetwork = binding.wrapper.findViewById<MaterialTextView>(R.id.mtvNetwork)
            mtvNetwork.text = getString(R.string.fragment_appointment_schedulimg_an_appointments)

            val mtvDescription = binding.wrapper.findViewById<MaterialTextView>(R.id.mtvDescription)
            mtvDescription.text = getString(R.string.fragment_appointment_description)
            val imageMsg = binding.wrapper.findViewById<ImageView>(R.id.ivMsg)
            imageMsg.setImageResource(R.drawable.ic_scheduled_appoinment_wrapper)

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

        binding.ivBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }
    }

    override fun onPinChatChange() {
        setupUI()
        val fragments = childFragmentManager.fragments
        val scheduledFragment = fragments.find { it is ScheduledAppointmentsFragment } as? ScheduledAppointmentsFragment
        if (scheduledFragment != null) {
            scheduledFragment.reloadFutureExams()
        } else {
            Log.d("DEBUG_TAG", "ScheduledAppointmentsFragment NOT found")
        }
    }
}