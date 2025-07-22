package com.example.zdravlje.ui.profile

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.marginRight
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.ProfileRepository
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.calendar.apointment.BottomSheetDialogConfirmFragment
import com.example.zdravlje.ui.home.HomeActivity
import com.example.zdravlje.ui.login.LoginActivity
import com.example.zdravlje.ui.profile.settings.ProfileSettings
import com.example.zdravlje.utilities.CommonUtils
import com.example.zdravlje.utilities.loadImage
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentUserInfoBinding
import java.util.Locale

class UserInfoFragment : BaseFragment(), BottomSheetDialogConfirmFragment.OnDeleteListener{

    lateinit var binding: FragmentUserInfoBinding
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var sharedPreferences: SharedPreferences
    private var bundleListener: String ?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserInfoBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        arguments?.let {
            bundleListener = it.getString("listener")
        }
        profileViewModel.updateAvatarLiveData.value=null
        profileViewModel.updateUserLiveData.value=null
        setupUI()
        setupObservers()
        sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }

    fun setupUI() {
        binding.progressBar.visibility=View.VISIBLE

        binding.clAccessKeyWrapper.setOnClickListener(View.OnClickListener {
            val profileAccessKeyFragment = ProfileAccessKeyFragment()
            val activity = requireActivity() as ProfileActivity
            activity.startNewFragmentFromProfile(profileAccessKeyFragment)
        })

        binding.ivSettings.setOnClickListener(View.OnClickListener {
            val address = binding.tvAddressValue.text.toString().trim()
            val phoneNumber = binding.tvPhoneNumberValue.text.toString().trim()
            val email = binding.tvEmailValue.text.toString().trim()

            val profileSettings = ProfileSettings()
            val bundle = Bundle().apply {
                putString("address", address)
                putString("phoneNumber", phoneNumber)
                putString("email", email)
                putString("language", getSavedLanguage().uppercase(Locale.ROOT))
            }

            profileSettings.arguments = bundle
            val activity = requireActivity() as ProfileActivity
            activity.startNewFragmentFromProfile(profileSettings)
        })

        binding.mbDeleteAccount.setOnClickListener {
            val content="Da li ste sigurni da želite da obrišete nalog na portalu i sve podatke koje ste uneli prilikom rada sa portalom? \n" +
                    "Ova akcija trajno uklanja nalog i sve podatke koje ste uneli prilikom rada sa portalom bez mogućnosti povratka istih."

            val headerTitle = "Brisanje naloga"

            val bsDcf = BottomSheetDialogConfirmFragment().apply {
                arguments = Bundle().apply {
                    putString("title", "UserInfoFragment")
                    putString("header_title", headerTitle)
                    putString("content", content)
                }
            }
            bsDcf.setDeleteListener(this)
            bsDcf.show(parentFragmentManager, bsDcf.tag)
        }

        binding.mbLogout.setOnClickListener {
            val dialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.fragment_user_info_logout_btn))
                .setMessage(getString(R.string.fragment_user_info_logout_content_alert_dialog))
                .setPositiveButton(getString(R.string.fragment_user_info_logout_btn)) { dialogInterface, _ ->
                    clearPin()
                    clearToken()
                    val intent = Intent(requireActivity(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    dialogInterface.dismiss()
                }
                .setNegativeButton(getString(R.string.close)) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .create()

            dialog.window?.apply {
                setGravity(Gravity.BOTTOM)

                val displayMetrics = context.resources.displayMetrics
                val screenWidth = displayMetrics.widthPixels

                val marginInDp = 30
                val scale = displayMetrics.density
                val marginInPx = (marginInDp * scale + 0.5f).toInt()

                val params = attributes
                params.width = screenWidth - 2 * marginInPx
                params.height = WindowManager.LayoutParams.WRAP_CONTENT
                attributes = params

                val backgroundDrawable = GradientDrawable().apply {
                    setColor(Color.WHITE)
                    cornerRadius = 32f
                    setStroke(2, Color.LTGRAY)
                }
                decorView?.background = backgroundDrawable
                decorView?.setPadding(24, 24, 24, 24)
            }

            dialog.show()

            // 1. Automatski postavi sve TextView elemente na crno
            dialog.window?.decorView?.let { root ->
                val views = ArrayDeque<View>()
                views.add(root)
                while (views.isNotEmpty()) {
                    val view = views.removeFirst()
                    if (view is ViewGroup) {
                        for (i in 0 until view.childCount) {
                            views.add(view.getChildAt(i))
                        }
                    } else if (view is TextView) {
                        view.setTextColor(Color.BLACK)
                    }
                }
            }

            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)

            positiveButton?.setTextColor(Color.parseColor("#4CAF50"))
            negativeButton?.setTextColor(Color.parseColor("#F44336"))

            positiveButton?.textSize = 18f
            negativeButton?.textSize = 18f
        }

        binding.ivBack.setOnClickListener(View.OnClickListener {
            requireActivity().finish()
        })
    }

    private fun setViewModels() {
        val profileRepository = ProfileRepository()
        dataStoreRepository = DataStoreRepository(requireActivity())
        val profileViewModelFactory =
            ProfileViewModelFactory(profileRepository, dataStoreRepository)
        profileViewModel = ViewModelProvider(
            requireActivity(),
            profileViewModelFactory
        ).get(ProfileViewModel::class.java)
    }

    private fun getSavedLanguage(): String {
        return sharedPreferences.getString("selected_language", "sr") ?: "sr"
    }

    @SuppressLint("SetTextI18n")
    fun setupObservers() {
        profileViewModel.getUserInformation()

        profileViewModel.userInfoLiveData.observe(viewLifecycleOwner) { userInfo ->
            if (userInfo.isSuccessful) {
                binding.progressBar.visibility = View.GONE
                val userData = userInfo.body()
                binding.userFullName.text = "${userData?.pat_first_name} ${userData?.pat_last_name}"
                binding.tvJmbgValue.text = userData?.pat_umcn
                binding.tvSecNumberValue.text = userData?.pat_pin
                binding.tvBZKValue.text = userData?.pat_insurance_card_number
                binding.tvBZKValue.text = userData?.pat_insurance_card_number
                binding.tvGenderValue.text = setGender(userData?.pat_sex!!)
                binding.tvPhoneNumberValue.text = userData.pat_phone_mobile
                binding.tvAddressValue.text = userData.pat_address
                binding.tvEmailValue.text = userData.us_email

                val avatarValue = userData?.avt_encoded_value
                val fullName = userData?.sgco_name ?: ""

                if (!avatarValue.isNullOrEmpty()) {
                    binding.ivUserImage.visibility = View.VISIBLE
                    binding.tvAvatarInitials.visibility = View.GONE
                    binding.ivUserImage.loadImage(
                        avatarValue,
                        R.drawable.ic_contact_avatar
                    )
                } else {
                    if (userData?.pat_sex == 2) {
                        binding.tvAvatarInitials.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.screeningColor)
                        )
                    } else {
                        binding.tvAvatarInitials.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.base_blue_color)
                        )
                    }

                    binding.ivUserImage.visibility = View.GONE
                    binding.tvAvatarInitials.visibility = View.VISIBLE
                    binding.tvAvatarInitials.text = CommonUtils.getInitials(fullName)
                }
            }
        }

        profileViewModel.userInfoLiveDataErrorLiveData.observe(viewLifecycleOwner) { response ->
            binding.progressBar.visibility = View.GONE
            Toast.makeText(requireContext(), getString(R.string.error), Toast.LENGTH_SHORT).show()
        }
    }

    fun setGender(gender: Int): String {
        val gender = when (gender) {
            1 -> getString(R.string.fragment_user_info_gender_male)
            2 -> getString(R.string.fragment_user_info_gender_female)
            else -> " /"
        }
        return gender
    }

    override fun onDelete() {
        Toast.makeText(requireActivity(),"Nalog je trajno izbrisan.", Toast.LENGTH_SHORT).show()
        clearPin()
        clearToken()
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        profileViewModel.fullDeletePatient()
    }
}