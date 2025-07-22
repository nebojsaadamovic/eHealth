package com.example.zdravlje.ui.profile.settings

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.LocaleList
import android.provider.MediaStore
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.zdravlje.data.model.request.UpdateMyProfileRequest
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.ProfileRepository
import com.example.zdravlje.ui.base.BaseFragment
import com.example.zdravlje.ui.login.parameters.BloodGroupFragment
import com.example.zdravlje.ui.profile.BottomSheetPasswordSettings
import com.example.zdravlje.ui.profile.ProfileViewModel
import com.example.zdravlje.ui.profile.ProfileViewModelFactory
import com.example.zdravlje.ui.profile.UserInfoFragment
import com.example.zdravlje.utilities.CommonUtils
import com.example.zdravlje.utilities.loadImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentProfileSettingsBinding
import java.util.Locale

class ProfileSettings : BaseFragment(), BottomSheetChooseLanguage.OnLanguageSelectedListener,
    AdrressFragmentBottomSheet.OnAddressListener,
    PhoneNumberFragmentBottomSheet.OnPhoneNumListener,
    EmailFragmentBottomSheet.OnEmailListener, BloodGroupFragment.OnBloodGroupSelectedListener {

    lateinit var binding: FragmentProfileSettingsBinding
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var sharedPreferences: SharedPreferences
    private val TAG = ProfileSettings::class.java.simpleName
    var userPin: String = ""
    var languageSelected = ""
    private var isImageLocallyChanged = false

    private var selectedImageUri: Uri? = null

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                val bitmap = rotateImageIfRequired(requireContext(), selectedImageUri!!)
                binding.ivUserPlaceholder.setImageBitmap(bitmap)
                binding.ivUserPlaceholder.visibility = View.VISIBLE
                binding.tvAvatarInitials.visibility = View.GONE
                isImageLocallyChanged=true
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileSettingsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        setupObservers()
        setupUI()
        setProfileData()
        loadDataFromArguments()
        sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        setLocale(requireContext(), getSavedLanguage())
    }

    private fun loadDataFromArguments() {
        if (arguments == null || requireArguments().isEmpty) {
            profileViewModel.getUserInformation()
            profileViewModel.userInfoLiveData.observe(viewLifecycleOwner) { userInfo ->
                val userData = userInfo.body()

                binding.tvPhoneNumberValue.text =
                    if (profileViewModel.phoneNumberValue?.isNotEmpty() == true) {
                        profileViewModel.phoneNumberValue
                    } else {
                        userData?.pat_phone_mobile ?: "N/A"
                    }

                binding.tvAddressValue.text =
                    if (profileViewModel.adressValue?.isNotEmpty() == true) {
                        profileViewModel.adressValue
                    } else {
                        userData?.pat_address ?: "N/A"
                    }

                binding.tvEmailValue.text =
                    if (profileViewModel.eMailValue?.isNotEmpty() == true) {
                        profileViewModel.eMailValue
                    } else {
                        userData?.us_email ?: "N/A"
                    }

                binding.mtvBloodGroupValue.text =
                    if (profileViewModel.bloodGroupValue?.isNotEmpty() == true) {
                        profileViewModel.bloodGroupValue
                    } else {
                        userData?.pat_blood_type ?: "N/A"
                    }

                binding.tvLanguageValue.text = when (getSavedLanguage().uppercase(Locale.ROOT)) {
                    "SR" -> "СР-ћирилица"
                    "SR-RRS" -> "Srpski-latinica"
                    "EN" -> "Engleski"
                    else -> "N/A"
                }
            }
        } else {
            arguments?.let { bundle ->
                val address = bundle.getString("address")?.takeIf { it.isNotBlank() && it != "N/A" }
                val phoneNumber =
                    bundle.getString("phoneNumber")?.takeIf { it.isNotBlank() && it != "N/A" }
                val email = bundle.getString("email")?.takeIf { it.isNotBlank() && it != "N/A" }
                val language =
                    bundle.getString("language")?.takeIf { it.isNotBlank() && it != "N/A" }

                profileViewModel.getUserInformation()
                profileViewModel.userInfoLiveData.observe(viewLifecycleOwner) { userInfo ->
                    val userData = userInfo.body()
                    binding.mtvBloodGroupValue.text =userData?.pat_blood_type ?: "N/A"
                }

                if (address == null && phoneNumber == null && email == null) {
                    profileViewModel.userInfoLiveData.observe(viewLifecycleOwner) { userInfo ->
                        val userData = userInfo.body()

                        binding.tvPhoneNumberValue.text =
                            if (profileViewModel.phoneNumberValue?.isNotEmpty() == true) {
                                profileViewModel.phoneNumberValue
                            } else {
                                userData?.pat_phone_mobile ?: "N/A"
                            }

                        binding.tvAddressValue.text =
                            if (profileViewModel.adressValue?.isNotEmpty() == true) {
                                profileViewModel.adressValue
                            } else {
                                userData?.pat_address ?: "N/A"
                            }

                        binding.tvEmailValue.text =
                            if (profileViewModel.eMailValue?.isNotEmpty() == true) {
                                profileViewModel.eMailValue
                            } else {
                                userData?.us_email ?: "N/A"
                            }
                        binding.tvLanguageValue.text = language ?: "N/A"
                        binding.tvLanguageValue.text =
                            when (getSavedLanguage().uppercase(Locale.ROOT)) {
                                "SR" -> "СР-ћирилица"
                                "SR-RRS" -> "Srpski-latinica"
                                "EN" -> "Engleski"
                                else -> "N/A"
                            }
                    }
                } else {
                    binding.tvPhoneNumberValue.text =
                        profileViewModel.phoneNumberValue?.ifEmpty { phoneNumber ?: "N/A" } ?: ""
                    binding.tvAddressValue.text =
                        profileViewModel.adressValue?.ifEmpty { address ?: "N/A" } ?: ""
                    binding.tvEmailValue.text =
                        profileViewModel.eMailValue?.ifEmpty { email ?: "N/A" } ?: ""
                    languageSelected =
                        profileViewModel.languageValue?.ifEmpty { language ?: "N/A" } ?: ""
                    if (languageSelected.uppercase() == "SR") {
                        binding.tvLanguageValue.text = "СР-ћирилица"
                    }
                    if (languageSelected.uppercase() == "SR-RRS") {
                        binding.tvLanguageValue.text = "Srpski-latinica"
                    }
                    if (languageSelected.uppercase() == "EN") {
                        binding.tvLanguageValue.text = "Engleski"
                    }
                }
            }
        }
    }

    private fun setupUI() {
        binding.progressBar.visibility = View.VISIBLE

        binding.tvLanguageValue.setOnClickListener {
            val bottomSheetChooseLanguage = BottomSheetChooseLanguage()
            bottomSheetChooseLanguage.setOnLanguageListener(this)
            bottomSheetChooseLanguage.show(childFragmentManager, TAG)
        }

        binding.mtvBloodGroupValue.setOnClickListener(View.OnClickListener {
            val bloodGroupFragment = BloodGroupFragment()
            bloodGroupFragment.setTargetFragment(this, 0)
            bloodGroupFragment.show(
                requireActivity().supportFragmentManager,
                bloodGroupFragment.tag
            )
        })

        binding.mbSave.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            val adrressValue= binding.tvAddressValue.text.toString()
            val emailValue= binding.tvEmailValue.text.toString()
            val phoneNumValue= binding.tvPhoneNumberValue.text.toString()
            val bloodGroupValue= binding.mtvBloodGroupValue.text.toString()
            val languageId = profileViewModel.languageTag

            profileViewModel.getUserInformation()
            profileViewModel.userInfoLiveData.observe(viewLifecycleOwner) { userInfo ->
                if (userInfo.isSuccessful) {
                    binding.progressBar.visibility = View.GONE
                    val userData = userInfo.body()
                    if (adrressValue != userData?.pat_address ||
                        emailValue != userData?.pat_email ||
                        phoneNumValue != userData?.pat_phone_mobile ||
                        bloodGroupValue != userData?.pat_blood_type
                    ) {

                        var updateMyProfileRequest = UpdateMyProfileRequest()
                        updateMyProfileRequest.pin = userPin
                        updateMyProfileRequest.pat_address = binding.tvAddressValue.text.toString()
                        updateMyProfileRequest.pat_email = binding.tvEmailValue.text.toString()
                        updateMyProfileRequest.pat_phone_mobile = binding.tvPhoneNumberValue.text.toString()
                        updateMyProfileRequest.pat_blood_type = binding.mtvBloodGroupValue.text.toString()

                        if (languageId != null) {
                            updateMyProfileRequest.us_language_id = languageId
                        }
                        profileViewModel.updateMyProfileRequest(updateMyProfileRequest)
                    }
                }
            }

            if (selectedImageUri != null) {
                selectedImageUri?.let { uri ->
                    val inputStream = requireContext().contentResolver.openInputStream(uri)
                    val fileSize = inputStream?.available() ?: 0
                    val maxFileSize = 50 * 1024 * 1024

                    if (fileSize > maxFileSize) {
                        Toast.makeText(
                            requireActivity(),
                            getString(R.string.fragment_profile_settings_image_size_too_large),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@let
                    }

                    val rotatedBitmap = rotateImageIfRequired(requireContext(), uri)
                    val resizedBitmap = rotatedBitmap.resize(Size(110, 110), ContentMode.CONTENT_FILL)
                    val multipartBody = bitmapToRequestBody(resizedBitmap)

                    if (isPinSet()){
                        profileViewModel.updateAvatarProfile(getStoredPin()!!, multipartBody)
                        binding.progressBar.visibility = View.VISIBLE
                    }else{
                        Toast.makeText(
                            requireActivity(),
                            getString(R.string.error_upload_image),
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }else{
                profileViewModel.updateUserLiveData.observe(viewLifecycleOwner) { response ->
                    if (response!=null && response.isSuccessful) {
                        binding.progressBar.visibility = View.GONE
                        val userInfoFragment = UserInfoFragment()
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.profile_fragment_container_view, userInfoFragment)
                            .addToBackStack(null).commit()
                    }
                }
            }
        }

        binding.mbClose.setOnClickListener {
            val userInfoFragment = UserInfoFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.profile_fragment_container_view, userInfoFragment)
                .addToBackStack(null).commit()
            profileViewModel.adressValue = ""
            profileViewModel.phoneNumberValue = ""
            profileViewModel.eMailValue = ""
            profileViewModel.languageValue = ""
        }

        binding.profilePasswordDots.setOnClickListener {
            val bottomSheetPasswordSettings = BottomSheetPasswordSettings()
            bottomSheetPasswordSettings.setTargetFragment(this, 0)
            bottomSheetPasswordSettings.show(
                requireActivity().supportFragmentManager,
                bottomSheetPasswordSettings.tag
            )
        }
    }

    private fun setProfileData() {
        binding.tvAddressValue.setOnClickListener {
            val fragmentAdrressBottomSheet = AdrressFragmentBottomSheet()
            fragmentAdrressBottomSheet.setOnAddressListener(this)
            fragmentAdrressBottomSheet.show(parentFragmentManager, fragmentAdrressBottomSheet.tag)
        }

        binding.tvPhoneNumberValue.setOnClickListener {
            val fragmentPhoneNumberBottomSheet = PhoneNumberFragmentBottomSheet()
            fragmentPhoneNumberBottomSheet.setOnPhoneNumListener(this)
            fragmentPhoneNumberBottomSheet.show(
                parentFragmentManager,
                fragmentPhoneNumberBottomSheet.tag
            )
        }

        binding.tvEmailValue.setOnClickListener {
            val fragmentEmailBottomSheet = EmailFragmentBottomSheet()
            fragmentEmailBottomSheet.setOnPhoneNumListener(this)
            fragmentEmailBottomSheet.show(parentFragmentManager, fragmentEmailBottomSheet.tag)
        }

        val imgFromGallery = getString(R.string.fragment_profile_settings_image_from_gallery)
        val takePicture = getString(R.string.fragment_profile_settings_make_picture)
        binding.ivUserPlaceholder.setOnClickListener {
            val options = arrayOf(imgFromGallery, takePicture)
            val builder = AlertDialog.Builder(requireContext())
            builder.setItems(options) { _, which ->
                when (which) {
                    0 -> pickImageFromGallery()
                    1 -> takePictureFromCamera()
                }
            }

            builder.show()
        }

        binding.tvAvatarInitials.setOnClickListener {
            val options = arrayOf(imgFromGallery, takePicture)
            val builder = AlertDialog.Builder(requireContext())
            builder.setItems(options) { _, which ->
                when (which) {
                    0 -> pickImageFromGallery()
                    1 -> takePictureFromCamera()
                }
            }

            builder.show()
        }
    }

    private fun pickImageFromGallery() {
        pickImageLauncher.launch("image/*")
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

    @SuppressLint("SetTextI18n")
    fun setupObservers() {
        profileViewModel.getUserInformation()
        profileViewModel.userInfoLiveData.observe(viewLifecycleOwner) { userInfo ->
            binding.progressBar.visibility = View.GONE
            val userData = userInfo.body()
            if (!isImageLocallyChanged){
                binding.ivUserPlaceholder.loadImage(
                    userData?.avt_encoded_value.toString(),
                    R.drawable.ic_contact_avatar
                )
            }
            lifecycleScope.launch {
                dataStoreRepository.saveAvatar(userInfo.body()?.avt_encoded_value.toString())
            }

            val avatarValue = userData?.avt_encoded_value
            val fullName = userData?.sgco_name ?: ""

            if (!avatarValue.isNullOrEmpty()) {
                binding.ivUserPlaceholder.visibility = View.VISIBLE
                binding.tvAvatarInitials.visibility = View.GONE
                binding.ivUserPlaceholder.loadImage(
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

                binding.ivUserPlaceholder.visibility = View.GONE
                binding.tvAvatarInitials.visibility = View.VISIBLE
                binding.tvAvatarInitials.text = CommonUtils.getInitials(fullName)
            }
        }

        userPin=getStoredPin()


        profileViewModel.updateAvatarLiveData.observe(viewLifecycleOwner) { response ->
            binding.progressBar.visibility = View.VISIBLE
            if (response!=null && response.isSuccessful) {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.fragment_profile_settings_succesfully_update_profile_img),
                    Toast.LENGTH_SHORT
                ).show()

                profileViewModel.getUserInformation()
                binding.progressBar.visibility = View.GONE
                val userInfoFragment = UserInfoFragment()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.profile_fragment_container_view, userInfoFragment)
                    .addToBackStack(null).commit()
            }
        }

        profileViewModel.updateUserLiveData.observe(viewLifecycleOwner) { response ->
            if (response!=null &&response.isSuccessful) {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.success_change_data),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        profileViewModel.updateUserLiveDataErrorLiveData.observe(viewLifecycleOwner) { response ->
            binding.progressBar.visibility = View.GONE
            Toast.makeText(
                requireActivity(),
                getString(R.string.unsuccessful_save_data) + " " + response,
                Toast.LENGTH_SHORT
            ).show()
        }

        profileViewModel.updateAvatarErrorLiveData.observe(viewLifecycleOwner) { errorMessage ->
            binding.progressBar.visibility = View.GONE
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onAddressChange(address: String) {
        binding.tvAddressValue.text = address
        profileViewModel.adressValue = address
    }

    override fun onPhoneNumChange(phoneNumber: String) {
        binding.tvPhoneNumberValue.text = phoneNumber
        profileViewModel.phoneNumberValue = phoneNumber
    }

    override fun onEmailChange(email: String) {
        binding.tvEmailValue.text = email
        profileViewModel.eMailValue = email
    }

    override fun onBloodGroupSelected(value: String) {
        binding.mtvBloodGroupValue.text = value
        profileViewModel.bloodGroupValue = value
    }

    private fun getSavedLanguage(): String {
        return sharedPreferences.getString("selected_language", "sr") ?: "sr"
    }

    private fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            config.setLocales(LocaleList(locale))
        }

        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    fun updateLanguage(languageCode: String) {
        setLocale(requireActivity(), languageCode)
        saveLanguage(languageCode)
        requireActivity().recreate()
        refreshCurrentFragment()
    }

    private fun saveLanguage(languageCode: String) {
        with(sharedPreferences.edit()) {
            putString("selected_language", languageCode)
            apply()
        }
    }

    override fun onLanguageSelected(languageCode: String, languageTag: Int) {
        updateLanguage(languageCode)
        profileViewModel.languageValue = languageCode
        profileViewModel.languageTag = languageTag
    }

    fun refreshCurrentFragment() {
        val fragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.profile_fragment_container_view)
        if (fragment != null) {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.detach(fragment) // Uklanja fragment
            fragmentTransaction.attach(fragment) // Ponovo ga dodaje
            fragmentTransaction.commit() // Primena promena
        }
    }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess && selectedImageUri != null) {
                val bitmap = rotateImageIfRequired(requireContext(), selectedImageUri!!)
                binding.ivUserPlaceholder.setImageBitmap(bitmap)
            }
        }

    private fun createImageUri(): Uri {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "profile_image.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }
        return requireActivity().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )!!
    }

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                takePictureFromCameraInternal()
            } else {
                Toast.makeText(requireContext(), getString(R.string.fragment_profile_settings_camera_permission_denied), Toast.LENGTH_SHORT).show()
            }
        }

    private fun takePictureFromCamera() {
        val context = requireContext()

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {

            takePictureFromCameraInternal()

        } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            AlertDialog.Builder(context)
                .setTitle(getString(R.string.fragment_profile_settings_camera_permission_needed))
                .setMessage(getString(R.string.fragment_profile_settings_camera_permission_notifiction))
                .setPositiveButton(getString(R.string.fragment_profile_settings_allow)) { _, _ ->
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun takePictureFromCameraInternal() {
        selectedImageUri = createImageUri()
        takePictureLauncher.launch(selectedImageUri)
    }

    fun rotateImageIfRequired(context: Context, uri: Uri): Bitmap {
        val inputStream1 = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream1)
        inputStream1?.close()

        val inputStream2 = context.contentResolver.openInputStream(uri)
        val exif = inputStream2?.use { ExifInterface(it) }
        val orientation = exif?.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        val rotationAngle = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }

        return if (rotationAngle != 0f) {
            val matrix = Matrix().apply { postRotate(rotationAngle) }
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } else {
            bitmap
        }
    }

    enum class ContentMode {
        CONTENT_FILL,
        CONTENT_ASPECT_FILL,
        CONTENT_ASPECT_FIT
    }

    fun Bitmap.resize(targetSize: Size, contentMode: ContentMode = ContentMode.CONTENT_ASPECT_FILL): Bitmap {
        val widthRatio = targetSize.width.toFloat() / this.width
        val heightRatio = targetSize.height.toFloat() / this.height

        val scale = when (contentMode) {
            ContentMode.CONTENT_FILL -> {
                // Stretch to fill target size (može izobličiti sliku)
                return Bitmap.createScaledBitmap(this, targetSize.width, targetSize.height, true)
            }
            ContentMode.CONTENT_ASPECT_FIT -> minOf(widthRatio, heightRatio)  // skalira da stane unutar targeta, čuva proporcije
            ContentMode.CONTENT_ASPECT_FILL -> maxOf(widthRatio, heightRatio) // skalira da popuni ceo target, moguće seće
        }

        val scaledWidth = (this.width * scale).toInt()
        val scaledHeight = (this.height * scale).toInt()

        return Bitmap.createScaledBitmap(this, scaledWidth, scaledHeight, true)
    }

    fun bitmapToRequestBody(bitmap: Bitmap): MultipartBody.Part {
        val stream = java.io.ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()
        val requestBody = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("avatar", "avatar.jpg", requestBody)
    }
}