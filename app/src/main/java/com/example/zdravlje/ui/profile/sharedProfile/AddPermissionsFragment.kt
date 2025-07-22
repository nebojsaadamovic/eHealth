package com.example.zdravlje.ui.profile.sharedProfile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.zdravlje.data.model.request.profile.SetPermission
import com.example.zdravlje.data.model.request.profile.SharedDataRequestModel
import com.example.zdravlje.data.model.response.chat.UserContact
import com.example.zdravlje.data.model.response.profile.UserInfo
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.ProfileRepository
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.profile.ProfileViewModel
import com.example.zdravlje.ui.profile.ProfileViewModelFactory
import com.example.zdravlje.utilities.DateUtils
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentAddPermissionsBinding
import java.text.SimpleDateFormat
import java.util.Locale

class AddPermissionsFragment : BaseFragment() {

    private lateinit var binding: FragmentAddPermissionsBinding
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var dataStoreRepository: DataStoreRepository

    private lateinit var userInfo: UserInfo
    private var contactName: String? = null
    private var contactId: String? = null
    private var userContact: UserContact? = null
    private var className: String? = null
    private var changeLifeStyleParam = 0
    private var changeAccessProfile = 0
    private var changeMedicalCard = 0
    private var changeMedicaments = 0
    private var changeEHR = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddPermissionsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        setupObservers()
        setupUI()
        checkEHRSelected()
//        checkAccessProfileSelected()

        className = arguments?.getString("class_name")

        if (className == "ProfileWhoCanSeeMyDataFragment") {
            println("aaaaa3333333333333")
            setupUIElements()
        } else {


            println("aaaaa333333333333333222222222225555555555555555")
            arguments?.let { bundle ->
                contactName = bundle.getString("contactName")
                contactId = bundle.getString("contactId")


                println(contactId)

                if (contactName != null) {
                    binding.clContent.visibility = View.VISIBLE
                    binding.mtvPermissionGivenNameValue.text = contactName
                    binding.mtvPermissionGivenName.text = getString(R.string.fragment_add_permissions_data_permission)
                    val color8A8A8D = ContextCompat.getColor(requireContext(), R.color.text_color_8A8A8D)
                    binding.mtvPermissionGivenNameValue.setTextColor(color8A8A8D)
                }
            }
        }
    }

    private fun setupObservers() {
        profileViewModel.getUserInformation()
        profileViewModel.userInfoLiveData.observe(viewLifecycleOwner) { userInfo1 ->
            userInfo = userInfo1.body()!!
        }

       profileViewModel.setPermissionLiveData.observe(viewLifecycleOwner) { response ->
           if(response.isSuccessful) {
               Toast.makeText(
                   requireActivity(),
                  "${getString(R.string.toast_data_save)} $response",
                   Toast.LENGTH_SHORT
               ).show()

                requireActivity().finish()
           }
       }

        profileViewModel.setPermissionErrorLiveData.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                Log.e("PermissionError", it)
            }
        }

    }

    private fun toggleVisibility(param: Int, layout: View) {
        if (param == 0) {
            layout.visibility = View.VISIBLE
        } else {
            layout.visibility = View.GONE
        }
    }

    private fun setupUI() {
        binding.clContent.visibility = View.GONE

        binding.clPermissionGiven.setOnClickListener() {
            val chooseContactBinding = ChooseContactFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.shared_profile_fragment_container_view, chooseContactBinding)
                .addToBackStack(null).commit()
        }

        binding.mbSave.setOnClickListener {
            val sharedData = mutableListOf<SharedDataRequestModel>()

            if (className == "ProfileWhoCanSeeMyDataFragment") {
                    if (binding.cbAccessProfile.isChecked) {
                    val dateFrom = DateUtilss.convertToDateFormat(
                        binding.mtvDateFromAccesProfile.text.toString(),
                        "yyyy-MM-dd"
                    )
                    val dateTo = DateUtilss.convertToDateFormat(
                        binding.mtvDateToAccesProfile.text.toString(),
                        "yyyy-MM-dd"
                    )

                    sharedData.add(
                        SharedDataRequestModel(
                            type = 1,
                            from = dateFrom,
                            to = dateTo
                        )
                    )
                    sharedData.add(
                        SharedDataRequestModel(
                            type = 2,
                            from = dateFrom,
                            to = dateTo
                        )
                    )

                    sharedData.add(
                        SharedDataRequestModel(
                            type = 5,
                            from = dateFrom,
                            to = dateTo
                        )
                    )

                    sharedData.add(
                        SharedDataRequestModel(
                            type = 6,
                            from = dateFrom,
                            to = dateTo
                        )
                    )

                    sharedData.add(
                        SharedDataRequestModel(
                            type = 999999,
                            from = dateFrom,
                            to = dateTo
                        )
                    )
                }
            }
            else {
                if (binding.cbAccessProfile.isChecked) {
                    val dateFromAccessProfile = DateUtilss.convertToDateFormat(
                        binding.mtvDateFromAccesProfile.text.toString(),
                        "yyyy-MM-dd"
                    )
                    val dateToAccessProfile = DateUtilss.convertToDateFormat(
                        binding.mtvDateToAccesProfile.text.toString(),
                        "yyyy-MM-dd"
                    )
                    sharedData.add(
                        SharedDataRequestModel(
                            type = 1,
                            from = dateFromAccessProfile,
                            to = dateToAccessProfile
                        )
                    )
                }

                if (binding.cbLifeStyleParam.isChecked) {
                    val dateFromLifeStyle = DateUtilss.convertToDateFormat(
                        binding.mtvDateFromLifeStyle.text.toString(),
                        "yyyy-MM-dd"
                    )
                    val dateToLifeStyle = DateUtilss.convertToDateFormat(
                        binding.mtvDateToLifeStyle.text.toString(),
                        "yyyy-MM-dd"
                    )

                    sharedData.add(
                        SharedDataRequestModel(
                            type = 2,
                            from = dateFromLifeStyle,
                            to = dateToLifeStyle
                        )
                    )
                }

                if (binding.cbMedicaments.isChecked) {
                    val dateFromMedicaments = DateUtilss.convertToDateFormat(
                        binding.mtvDateFromMedicaments.text.toString(),
                        "yyyy-MM-dd"
                    )
                    val dateToMedicaments = DateUtilss.convertToDateFormat(
                        binding.mtvDateToMedicaments.text.toString(),
                        "yyyy-MM-dd"
                    )
                    sharedData.add(
                        SharedDataRequestModel(
                            type = 3,
                            from = dateFromMedicaments,
                            to = dateToMedicaments
                        )
                    )
                }

                if (binding.cbMedicalCard.isChecked) {
                    val dateFromMedicalCard = DateUtilss.convertToDateFormat(
                        binding.mtvDateFromMedicalCard.text.toString(),
                        "yyyy-MM-dd"
                    )
                    val dateToMedicalCard = DateUtilss.convertToDateFormat(
                        binding.mtvDateToMedicalCard.text.toString(),
                        "yyyy-MM-dd"
                    )
                    sharedData.add(
                        SharedDataRequestModel(
                            type = 4,
                            from = dateFromMedicalCard,
                            to = dateToMedicalCard
                        )
                    )
                }

                if (binding.cbEHR.isChecked) {
                    val dateFromEHR = DateUtilss.convertToDateFormat(
                        binding.mtvDateFromEHR.text.toString(),
                        "yyyy-MM-dd"
                    )
                    val dateToEHR = DateUtilss.convertToDateFormat(
                        binding.mtvDateToEHR.text.toString(),
                        "yyyy-MM-dd"
                    )
                    sharedData.add(
                        SharedDataRequestModel(
                            type = 5,
                            from = dateFromEHR,
                            to = dateToEHR
                        )
                    )
                }

                if (binding.cbEHR.isChecked) {
                    val dateFromEHR = DateUtilss.convertToDateFormat(
                        binding.mtvDateFromEHR.text.toString(),
                        "yyyy-MM-dd"
                    )
                    val dateToEHR = DateUtilss.convertToDateFormat(
                        binding.mtvDateToEHR.text.toString(),
                        "yyyy-MM-dd"
                    )
                    sharedData.add(
                        SharedDataRequestModel(
                            type = 6,
                            from = dateFromEHR,
                            to = dateToEHR
                        )
                    )
                }
            }

            if (contactId.isNullOrEmpty()) {
                contactId = userContact?.sgco_id.toString()
            }
            profileViewModel.setPermission(
                SetPermission(
                    pin = getStoredPin(),
                    sgcoId = contactId,
                    sharedData = sharedData,
                    sharedNotes = binding.etNotes.text.toString()
                )
            )
        }


//        binding.mbSave.setOnClickListener(View.OnClickListener {
//            val addPermissionBottomFragment = AddPermissionBottomFragment()
//            addPermissionBottomFragment.show(parentFragmentManager, addPermissionBottomFragment.tag)
//
//        })

//        cbLifeStyleParam
        binding.cbLifeStyleParam.setOnClickListener {
            toggleVisibility(changeLifeStyleParam, binding.clDateFromToLifeStyleParam)
            changeLifeStyleParam = 1 - changeLifeStyleParam
        }

        binding.cbAccessProfile.setOnClickListener {
            Log.d("AddPermissionsFragment", "cbAccessProfile changed: $changeAccessProfile")
            toggleVisibility(changeAccessProfile, binding.clDateFromToAccesProfile)
            changeAccessProfile = 1 - changeAccessProfile
            binding.cbAccessProfile.setChecked(changeAccessProfile == 1)
            checkAccessProfileSelected()
        }

        binding.cbMedicalCard.setOnClickListener {
            toggleVisibility(changeMedicalCard, binding.clDateFromToMedicalCard)
            changeMedicalCard = 1 - changeMedicalCard
        }

        binding.cbMedicaments.setOnClickListener {
            toggleVisibility(changeMedicaments, binding.clDateFromToMedicaments)
            changeMedicaments = 1 - changeMedicaments
        }

        binding.cbEHR.setOnClickListener {
            toggleVisibility(changeEHR, binding.clDateFromToEHR)
            changeEHR = 1 - changeEHR
        }

        binding.mtvDateFromLifeStyle.setOnClickListener() {
            DateUtils.showDatePickerDialog(
                requireContext(),
                binding.mtvDateFromLifeStyle
            ) { selectedDate ->
                binding.mtvDateFromLifeStyle.text = selectedDate
            }
        }

        binding.mtvDateToLifeStyle.setOnClickListener() {
            DateUtils.showDatePickerDialog(
                requireContext(),
                binding.mtvDateToLifeStyle
            ) { selectedDate ->
                binding.mtvDateToLifeStyle.text = selectedDate
            }
        }

        binding.mtvDateFromAccesProfile.setOnClickListener {
            DateUtils.showDatePickerDialog(
                requireContext(),
                binding.mtvDateFromAccesProfile
            ) { selectedDate ->
                binding.mtvDateFromAccesProfile.text = selectedDate
                checkAccessProfileSelected()
            }
        }

        binding.mtvDateToAccesProfile.setOnClickListener {
            DateUtils.showDatePickerDialog(
                requireContext(),
                binding.mtvDateToAccesProfile
            ) { selectedDate ->
                binding.mtvDateToAccesProfile.text = selectedDate
                checkAccessProfileSelected()
            }
        }

        binding.mtvDateFromMedicalCard.setOnClickListener {
            DateUtils.showDatePickerDialog(
                requireContext(),
                binding.mtvDateFromMedicalCard
            ) { selectedDate ->
                binding.mtvDateFromMedicalCard.text = selectedDate
            }
        }

        binding.mtvDateToMedicalCard.setOnClickListener {
            DateUtils.showDatePickerDialog(
                requireContext(),
                binding.mtvDateToMedicalCard
            ) { selectedDate ->
                binding.mtvDateToMedicalCard.text = selectedDate
            }
        }

        binding.mtvDateFromMedicaments.setOnClickListener {
            DateUtils.showDatePickerDialog(
                requireContext(),
                binding.mtvDateFromMedicaments
            ) { selectedDate ->
                binding.mtvDateFromMedicaments.text = selectedDate
            }
        }

        binding.mtvDateToMedicaments.setOnClickListener {
            DateUtils.showDatePickerDialog(
                requireContext(),
                binding.mtvDateToMedicaments
            ) { selectedDate ->
                binding.mtvDateToMedicaments.text = selectedDate
            }
        }

        binding.mtvDateFromEHR.setOnClickListener {
            DateUtils.showDatePickerDialog(
                requireContext(),
                binding.mtvDateFromEHR
            ) { selectedDate ->
                binding.mtvDateFromEHR.text = selectedDate
            }
        }

        binding.mtvDateToEHR.setOnClickListener {
            DateUtils.showDatePickerDialog(
                requireContext(),
                binding.mtvDateToEHR
            ) { selectedDate ->
                binding.mtvDateToEHR.text = selectedDate
            }
        }

        binding.ivBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }

//        binding.cbAccessProfile.setOnCheckedChangeListener(null)
//        binding.cbAccessProfile.isChecked = true
        binding.cbAccessProfile.setOnCheckedChangeListener { _, isChecked ->
            val dateFromAccessProfile = binding.mtvDateFromAccesProfile.text
            val dateToAccessProfile = binding.mtvDateToAccesProfile.text

            if (isChecked) {
                if (areDatesValid(dateFromAccessProfile, dateToAccessProfile)) {
                    checkAccessProfileSelected()
                } else {
//                    binding.cbAccessProfile.isChecked = false
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.fragment_add_permissions_set_dates_in_profile_access),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                checkAccessProfileSelected()
            }
        }

        binding.cbEHR.setOnCheckedChangeListener { _, isChecked ->
            val dateFromEhr = binding.mtvDateFromEHR.text
            val dateToEhr = binding.mtvDateToEHR.text

            if (isChecked) {
                if (areDatesValid(dateFromEhr, dateToEhr)) {
                    checkEHRSelected()
                } else {
                    binding.cbEHR.isChecked = false
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.fragment_add_permissions_set_dates_in_ehr),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                checkEHRSelected()
            }

        }
    }

    private fun checkEHRSelected() {
        if (binding.cbEHR.isChecked) {
            val dateToEHR = binding.mtvDateToEHR.text
            val dateFromEHR = binding.mtvDateFromEHR.text

            binding.cbMedicaments.isChecked = true
            binding.cbMedicaments.isEnabled = false
            binding.mtvDateToMedicaments.text = dateToEHR
            binding.mtvDateFromMedicaments.text = dateFromEHR

            binding.cbMedicalCard.isChecked = true
            binding.cbMedicalCard.isEnabled = false
            binding.mtvDateToMedicalCard.text = dateToEHR
            binding.mtvDateFromMedicalCard.text = dateFromEHR
        } else {
            binding.cbMedicaments.isChecked = false
            binding.cbMedicaments.isEnabled = true
            binding.cbMedicalCard.isChecked = false
            binding.cbMedicalCard.isEnabled = true
        }
    }

    private fun checkAccessProfileSelected() {
        println("aaaaaaaaaa111111111111")
        if (changeAccessProfile == 1) {
            val dateToAccessProfile = binding.mtvDateToAccesProfile.text
            val dateFromAccessProfile = binding.mtvDateFromAccesProfile.text
            binding.cbAccessProfile.isChecked = true

            binding.cbMedicaments.isChecked = true
            binding.clMedicaments.visibility = View.VISIBLE
            binding.clMedicaments.disableTouchRecursively()
            binding.mtvDateFromMedicaments.text = dateFromAccessProfile
            binding.mtvDateToMedicaments.text = dateToAccessProfile



//            binding.cbMedicalCard.isChecked = true
//            binding.cbMedicalCard.isEnabled = false
//            binding.mtvDateToMedicalCard.text = dateToAccessProfile
//            binding.mtvDateFromMedicalCard.text = dateFromAccessProfile

//            binding.cbEHR.isChecked = true
//            binding.cbEHR.post {
//                binding.cbEHR.isEnabled = false
//                binding.mtvDateToEHR.text = dateToAccessProfile
//                binding.mtvDateFromEHR.text = dateFromAccessProfile
//            }
//
//            binding.cbLifeStyleParam.isChecked = true
//            binding.cbLifeStyleParam.isEnabled = false
//            binding.mtvDateToLifeStyle.text = dateToAccessProfile
//            binding.mtvDateFromLifeStyle.text = dateFromAccessProfile

        }
//        else {
//
//            println("aaaaaaaaaa2222222222222")
//            binding.cbMedicaments.isChecked = false
//            binding.cbMedicaments.isEnabled = true
//
//
//            binding.cbMedicalCard.isChecked = false
//            binding.cbMedicalCard.isEnabled = true
//
//            binding.cbEHR.isChecked = false
//            binding.cbEHR.isEnabled = true
//
//
//            binding.cbLifeStyleParam.isChecked = false
//            binding.cbLifeStyleParam.isEnabled = true
//        }
    }

    object DateUtilss {
        fun convertToDateFormat(inputDate: String, outputFormat: String): String {
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat(outputFormat, Locale.getDefault())
            val date = inputFormat.parse(inputDate)
            return outputFormat.format(date)
        }
    }

    private fun areDatesValid(dateFrom: CharSequence?, dateTo: CharSequence?): Boolean {
        return dateFrom != getString(R.string.fragment_add_permissions_add_date) && dateTo != getString(R.string.fragment_add_permissions_add_date)
    }

    private fun setupUIElements() {

        // lek- type 5  permisssion 5


//        binding.cbAccessProfile.setOnCheckedChangeListener(null)
//        binding.cbAccessProfile.isChecked = true

        userContact = arguments?.getSerializable("userId") as  UserContact
        binding.clContent.visibility = View.VISIBLE
        binding.clPermissionGiven.visibility = View.GONE

        println(userContact?.sharing?.me2contact)
        println("mmmmmmmmmmmmmmmmmmmm")

        if(userContact?.sharing?.me2contact?.permission2 != null){

            binding.cbAccessProfile.visibility = View.VISIBLE
            binding.cbAccessProfile.isChecked = true
//            binding.cbAccessProfile.isEnabled = false

            val dateFrom = userContact?.sharing?.me2contact?.permission2?.get(0)
            val dateTo = userContact?.sharing?.me2contact?.permission2?.get(1)
            binding.mtvDateFromAccesProfile.text =
                DateUtils.convertyyyyMMddHorLineToddMyyyyVerLine(dateFrom.toString())
            binding.mtvDateToAccesProfile.text =
                DateUtils.convertyyyyMMddHorLineToddMyyyyVerLine(dateTo.toString())
            if (!dateFrom.isNullOrEmpty() && !dateTo.isNullOrEmpty())
                binding.clDateFromToAccesProfile.visibility = View.VISIBLE

//            binding.clStyleParam.visibility = View.GONE
//            binding.clMedicalCard.visibility = View.GONE
//            binding.clMedicaments.visibility = View.GONE
//            binding.clEHR.visibility = View.GONE
//            binding.clNotes.visibility = View.GONE
        }

        if(userContact?.sharing?.me2contact?.permission5 != null){
            binding.clMedicaments.visibility = View.VISIBLE
            binding.cbMedicaments.isChecked = true

            val dateFrom = userContact?.sharing?.me2contact?.permission5?.get(0)
            val dateTo = userContact?.sharing?.me2contact?.permission5?.get(1)
            binding.mtvDateFromMedicaments.text =
                DateUtils.convertyyyyMMddHorLineToddMyyyyVerLine(dateFrom.toString())
            binding.mtvDateToMedicaments.text =
                DateUtils.convertyyyyMMddHorLineToddMyyyyVerLine(dateTo.toString())
            if (!dateFrom.isNullOrEmpty() && !dateTo.isNullOrEmpty())
                binding.clDateFromToMedicaments.visibility = View.VISIBLE
        }

//        binding.cbEHR.isEnabled = true
//        binding.cbLifeStyleParam.isEnabled = true
//        binding.cbMedicalCard.isEnabled = true
//        binding.cbMedicaments.isEnabled = true
//        binding.mtvChangeLifeStyleParam.text = getString(R.string.fragment_add_permissions_edit)
//        binding.mtvChangeAccesProfile.text = getString(R.string.fragment_add_permissions_edit)
//        binding.mtvChangeMedicaments.text = getString(R.string.fragment_add_permissions_edit)
//        binding.mtvChangeMedicalCard.text = getString(R.string.fragment_add_permissions_edit)
//        binding.mtvChangeEHR.text = getString(R.string.fragment_add_permissions_edit)
//        binding.clPermissionGiven.visibility = View.GONE
    }

    private fun setViewModels() {
        val profileRepository = ProfileRepository()
        dataStoreRepository = DataStoreRepository(requireActivity())
        val profileViewModelFactory =
            ProfileViewModelFactory(profileRepository, dataStoreRepository)
        profileViewModel =
            ViewModelProvider(this, profileViewModelFactory).get(ProfileViewModel::class.java)
    }

     fun View.disableTouchRecursively() {
        isClickable = false
        isFocusable = false
        isFocusableInTouchMode = false
        isLongClickable = false
        if (this is ViewGroup) {
            for (i in 0 until childCount) {
                getChildAt(i).disableTouchRecursively()
            }
        }
    }




}