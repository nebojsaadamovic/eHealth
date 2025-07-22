package com.example.zdravlje.ui.healthcare.esickleave

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentDownloadPdfFileBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class OpenPdfFileUriFragment : Fragment() {

    private lateinit var binding: FragmentDownloadPdfFileBinding

    companion object {
        private const val ARG_FILE_URI = "arg_file_uri"
        private const val TITLE = "arg_file_title"

        fun newInstance(fileUri: Uri, title: String): OpenPdfFileUriFragment {
            val fragment = OpenPdfFileUriFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARG_FILE_URI, fileUri)
            bundle.putString(TITLE, title)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDownloadPdfFileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        val fileUri: Uri? = arguments?.getParcelable(ARG_FILE_URI)
        val title: String? = arguments?.getString(TITLE)

        if (fileUri != null) {
            val pdfFile = createTempFileFromUri(requireContext(), fileUri)
            if (pdfFile != null) {
                displayPdfFromFile(pdfFile)
            } else {
                binding.clWrapperData.visibility = View.VISIBLE
                showToast(R.string.fragment_open_pdf_file_uri_pdf_file_not_found)
            }
        } else {
            binding.clWrapperData.visibility = View.VISIBLE
            showToast(R.string.fragment_open_pdf_file_uri_pdf_file_not_found)
        }

        binding.mtvDownload.setOnClickListener {
            val fileUri: Uri? = arguments?.getParcelable(ARG_FILE_URI)
            val fileName = "${arguments?.getString(TITLE) ?: "SickLeave_Report"}.pdf"

            if (fileUri != null) {
                savePdfToDownloads(requireContext(), fileUri, fileName)
            } else {
                showToast(R.string.fragment_open_pdf_file_uri_pdf_file_not_found)
            }
        }

        binding.mtvTitle.text = title
        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun displayPdfFromFile(pdfFile: File) {
        if (!pdfFile.name.endsWith(".pdf", ignoreCase = true)) {
            binding.clWrapperData.visibility = View.VISIBLE
            showToast(R.string.fragment_open_pdf_file_uri2_invalid_pdf_file)
            return
        }

        binding.pdfView.visibility = View.VISIBLE
        binding.pdfView.fromFile(pdfFile)
            .enableSwipe(true)
            .swipeHorizontal(false)
            .enableDoubletap(true)
            .spacing(10)
            .pageFitPolicy(FitPolicy.WIDTH)
            .load()
    }

    private fun createTempFileFromUri(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val tempFile = File.createTempFile("temp_pdf", ".pdf", context.cacheDir)
            val outputStream = FileOutputStream(tempFile)
            inputStream.copyTo(outputStream)
            outputStream.close()
            inputStream.close()
            tempFile
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    @SuppressLint("StringFormatInvalid")
    private fun savePdfToDownloads(context: Context, pdfUri: Uri, fileName: String) {
        try {
            val inputStream = context.contentResolver.openInputStream(pdfUri)
            val downloadsDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)

            file.outputStream().use { outputStream ->
                inputStream?.use { it.copyTo(outputStream) }
            }

            Toast.makeText(
                context,
                getString(R.string.fragment_open_pdf_file_uri_pdf_saved_in_downloads, fileName),
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(
                context,
                "${getString(R.string.fragment_open_pdf_file_uri_error_saving_pdf)}: ${e.localizedMessage}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showToast(messageResId: Int) {
        Toast.makeText(requireContext(), getString(messageResId), Toast.LENGTH_SHORT).show()
    }
}
