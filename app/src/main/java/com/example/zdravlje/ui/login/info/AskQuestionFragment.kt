package com.example.zdravlje.ui.login.info

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import android.app.Activity
import android.content.ContentResolver
import android.provider.OpenableColumns
import com.example.zdravlje.data.model.request.login.TicketRequest
import com.example.zdravlje.data.repository.DataStoreRepository
import com.example.zdravlje.data.repository.LoginRepository
import com.example.zdravlje.ui.login.LoginInitialFragment
import com.example.zdravlje.ui.login.LoginViewModel
import com.example.zdravlje.ui.login.LoginViewModelFactory
import com.example.zdravlje.ui.login.info.EmailAskQuestBottomSheetFragment.OnEmailListener
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentAskQuestionBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class AskQuestionFragment : Fragment(),
    ChooseCategoryQuestBottomSheetFragment.OnChooseCategoryListener,
    TitleBottomSheetFragment.OnTitleListener, OnEmailListener {

    lateinit var binding: FragmentAskQuestionBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var dataStoreRepository: DataStoreRepository
    private val PDF_REQUEST_CODE = 1001
    private var selectedPdfUri: Uri? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAskQuestionBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModels()
        setupUI()
        subscribeToObservers()
    }

    private fun setViewModels() {
        val loginRepository = LoginRepository()
        dataStoreRepository = DataStoreRepository(requireContext())
        val loginViewModelFactory = LoginViewModelFactory(loginRepository, dataStoreRepository)
        loginViewModel = ViewModelProvider(
            this,
            loginViewModelFactory
        ).get(LoginViewModel::class.java)
    }

    private fun setupUI() {
        binding.mtvChoose.setOnClickListener(View.OnClickListener {
            val chooseCategoryQuestBottomSheetFragment = ChooseCategoryQuestBottomSheetFragment()
            chooseCategoryQuestBottomSheetFragment.setOnChooseCategoryListener(this)
            chooseCategoryQuestBottomSheetFragment.show(
                requireActivity().supportFragmentManager,
                chooseCategoryQuestBottomSheetFragment.tag
            )
        })

        binding.mtvAddTitle.setOnClickListener(View.OnClickListener {
            val titleBottomSheetFragment = TitleBottomSheetFragment()
            titleBottomSheetFragment.setOnTitleListener(this)
            titleBottomSheetFragment.show(
                requireActivity().supportFragmentManager,
                titleBottomSheetFragment.tag
            )
        })

        binding.mtvAddEmail.setOnClickListener(View.OnClickListener {
            val emailAskQuestBottomSheetFragment = EmailAskQuestBottomSheetFragment()
            emailAskQuestBottomSheetFragment.setOnEmailListener(this)
            emailAskQuestBottomSheetFragment.show(
                requireActivity().supportFragmentManager,
                emailAskQuestBottomSheetFragment.tag
            )
        })

        binding.mbSend.setOnClickListener {
            val queue = when (binding.mtvChoose.text.toString()) {
                getString(R.string.fragment_choose_category_quest_bottom_sheet_helpdesk) -> 4
                getString(R.string.fragment_choose_category_quest_bottom_sheet_gdpr) -> 3
                getString(R.string.fragment_choose_category_quest_bottom_sheet_registration) -> 1
                else -> null
            }

            val pdfPart = selectedPdfUri?.let { preparePdfPart(requireContext(), it) }

            val ticketRequest = TicketRequest(
                queue = queue.toString(),
                title = binding.mtvChoose.text.toString(),
                submitterEmail = binding.mtvAddEmail.text.toString(),
                description = binding.etAskQuest.text.toString()
            )

            loginViewModel.createTicket(ticketRequest, pdfPart)

            // Navigacija i potvrda
            val loginInitialFragment = LoginInitialFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.home_fragment_container_view, loginInitialFragment)
                .addToBackStack(null)
                .commit()

            Toast.makeText(requireActivity(), "Zahtev poslat", Toast.LENGTH_SHORT).show()
        }

        binding.mbAttachment.setOnClickListener(){
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf"
            startActivityForResult(intent, PDF_REQUEST_CODE)

        }

        binding.ivBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }
    }

    fun preparePdfPart(context: Context, uri: Uri): MultipartBody.Part? {
        val contentResolver = context.contentResolver

        // Izvlačenje imena fajla iz Uri
        val fileName = queryFileName(contentResolver, uri) ?: "attachment.pdf"

        val inputStream = contentResolver.openInputStream(uri) ?: return null
        val file = File(context.cacheDir, fileName)
        file.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }

        val requestBody = file.asRequestBody("application/pdf".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("file", file.name, requestBody)
    }

    // Helper funkcija za dobijanje imena fajla iz Uri-ja
    private fun queryFileName(contentResolver: ContentResolver, uri: Uri): String? {
        val returnCursor = contentResolver.query(uri, null, null, null, null)
        returnCursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    return it.getString(nameIndex)
                }
            }
        }
        return null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PDF_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            selectedPdfUri = data?.data
            Toast.makeText(requireContext(), "PDF izabran", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onChoseCategoryChange(categoryText: String) {
        binding.mtvChoose.text = categoryText
        Toast.makeText(
            requireActivity(),
            "${getString(R.string.fragment_ask_question_selected_category)}: $categoryText",
            Toast.LENGTH_SHORT
        )
            .show()
    }

    override fun onTitleChange(titleText: String) {
        binding.mtvAddTitle.text = titleText
        Toast.makeText(
            requireActivity(),
            "${getString(R.string.fragment_ask_question_titleOne)}: $titleText",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onEmailChange(email: String) {
        binding.mtvAddEmail.text = email
        Toast.makeText(
            requireActivity(),
            "${getString(R.string.fragment_ask_question_email)}: $email",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun subscribeToObservers() {
        loginViewModel.getTicketLiveData.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                val loginInitialFragment = LoginInitialFragment()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.home_fragment_container_view, loginInitialFragment)
                    .addToBackStack(null)
                    .commit()
            Toast.makeText(
                requireActivity(),
                getString(R.string.fragment_ask_question_success_message),
                Toast.LENGTH_SHORT
            )
                .show()
            }
        }
    }
}