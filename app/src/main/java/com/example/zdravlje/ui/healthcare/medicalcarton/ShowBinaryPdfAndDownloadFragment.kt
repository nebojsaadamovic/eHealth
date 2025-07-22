package com.example.zdravlje.ui.healthcare.medicalcarton

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentShowBinaryPdfAndDownloadBinding
import java.io.File

class ShowBinaryPdfAndDownloadFragment : Fragment() {

    private lateinit var binding: FragmentShowBinaryPdfAndDownloadBinding

    companion object {
        private const val ARG_FILE_PATH = "arg_file_path"
        private const val ARG_FILE_NAME = "arg_file_name"

        fun newInstance(filePath: String, fileName: String): ShowBinaryPdfAndDownloadFragment {
            val fragment = ShowBinaryPdfAndDownloadFragment()
            val args = Bundle().apply {
                putString(ARG_FILE_PATH, filePath)
                putString(ARG_FILE_NAME, fileName)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShowBinaryPdfAndDownloadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val filePath = arguments?.getString(ARG_FILE_PATH)
        val fileName = arguments?.getString(ARG_FILE_NAME)

        binding.mtvTitle.text = fileName ?: ""

        filePath?.let {
            val pdfFile = File(it)
            if (pdfFile.exists()) {
                displayPdfFromFile(pdfFile)
            } else {
                binding.mtvContent.visibility = View.VISIBLE
            }
        }

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.mtvDownload.setOnClickListener {
            filePath?.let { downloadPdf(File(it)) }
        }
    }

    private fun displayPdfFromFile(pdfFile: File) {
        try {
            val parcelFileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
            val pdfRenderer = PdfRenderer(parcelFileDescriptor)

            for (i in 0 until pdfRenderer.pageCount) {
                val page = pdfRenderer.openPage(i)

                val displayMetrics = resources.displayMetrics
                val bitmap = Bitmap.createBitmap(
                    page.width * displayMetrics.density.toInt(),
                    page.height * displayMetrics.density.toInt(),
                    Bitmap.Config.ARGB_8888
                )

                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                page.close()

                val imageView = ImageView(requireContext())
                imageView.setImageBitmap(bitmap)
                imageView.setPadding(16, 16, 16, 16)
                binding.pdfContainer.addView(imageView)
            }

            pdfRenderer.close()
            parcelFileDescriptor.close()

        } catch (e: Exception) {
            e.printStackTrace()
            binding.mtvContent.visibility = View.VISIBLE
        }
    }

    private fun downloadPdf(file: File) {
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val newFile = File(downloadsDir, file.name)

        try {
            file.copyTo(newFile, overwrite = true)
            Toast.makeText(
                requireContext(),
                "${getString(R.string.fragment_download_pdf_file_downloaded_successfully)}.",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                requireContext(),
                "${getString(R.string.fragment_download_pdf_file_error_while_downloading)}.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}