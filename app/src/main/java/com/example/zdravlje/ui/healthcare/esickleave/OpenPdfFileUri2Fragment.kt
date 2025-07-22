package com.example.zdravlje.ui.healthcare.esickleave

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentDownloadPdfFileBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class OpenPdfFileUri2Fragment : Fragment() {

    private lateinit var binding: FragmentDownloadPdfFileBinding

    companion object {
        private const val ARG_PDF_BODY = "arg_pdf_body"
        private const val TITLE = "arg_file_title"

        fun newInstance(pdfBody: ByteArray, title: String): OpenPdfFileUri2Fragment {
            return OpenPdfFileUri2Fragment().apply {
                arguments = Bundle().apply {
                    putByteArray(ARG_PDF_BODY, pdfBody)
                    putString(TITLE, title)
                }
            }
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

        val pdfBody = arguments?.getByteArray(ARG_PDF_BODY)
        val title = arguments?.getString(TITLE) ?: "DownloadedFile"

        binding.mtvTitle.text = title

        pdfBody?.let {
            if (it.isEmpty()) {
                showToast(R.string.fragment_open_pdf_file_uri2_invalid_pdf_file)
                binding.clWrapperData.visibility = View.VISIBLE
                binding.mtvDownload.alpha = 0.2f
                binding.mtvDownload.isEnabled = false
                return
            }

            savePdfToCache(it)?.let { file ->
                displayPdfFromFile(file)
            } ?: showToast(R.string.fragment_open_pdf_file_uri2_error_downloading_pdf)
        }

        binding.mtvDownload.setOnClickListener {
            pdfBody?.let {
                val fileName = "$title.pdf"
                savePdfToDownloads(requireContext(), it, fileName)
            } ?: showToast(R.string.fragment_open_pdf_file_uri2_pdf_content_not_available)
        }

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun showToast(messageRes: Int) {
        Toast.makeText(requireContext(), getString(messageRes), Toast.LENGTH_SHORT).show()
    }

    private fun savePdfToCache(pdfBody: ByteArray): File? {
        return try {
            val file = File(requireContext().cacheDir, "temp_pdf.pdf")
            FileOutputStream(file).use { it.write(pdfBody) }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun displayPdfFromFile(pdfFile: File) {
        if (!isPdfFile(pdfFile)) {
            binding.clWrapperData.visibility = View.VISIBLE
            showToast(R.string.fragment_open_pdf_file_uri2_invalid_pdf_file)
            return
        }

        binding.pdfView.fromFile(pdfFile)
            .enableSwipe(true)
            .swipeHorizontal(false)
            .enableDoubletap(true)
            .enableAnnotationRendering(true)
            .defaultPage(0)
            .spacing(20)
            .load()
    }

    private fun isPdfFile(file: File): Boolean = true // PDFView already handles invalid files

    private fun savePdfToDownloads(context: Context, pdfBody: ByteArray, fileName: String) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

                // Avoid filename conflicts
                val nameWithoutExt = fileName.substringBeforeLast(".")
                val extension = fileName.substringAfterLast(".", "")
                var newFileName = fileName
                var index = 1
                while (fileExists(resolver, collection, newFileName)) {
                    newFileName = "$nameWithoutExt ($index).$extension"
                    index++
                }

                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, newFileName)
                    put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                    put(MediaStore.Downloads.IS_PENDING, 1)
                }

                val itemUri = resolver.insert(collection, contentValues)
                itemUri?.let {
                    resolver.openOutputStream(it)?.use { outputStream ->
                        outputStream.write(pdfBody)
                    }

                    contentValues.clear()
                    contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
                    resolver.update(it, contentValues, null, null)

                    Toast.makeText(context,
                       "${getString(R.string.fragment_open_pdf_file_uri2_pdf_saved_in_downloads)} $newFileName",
                        Toast.LENGTH_LONG).show()
                } ?: Toast.makeText(
                    context,
                    getString(R.string.report_disability_for_work_adapter_error_save_pdf),
                    Toast.LENGTH_SHORT).show()

            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) downloadsDir.mkdirs()

                val file = getUniqueFile(downloadsDir, fileName)
                FileOutputStream(file).use { it.write(pdfBody) }

                Toast.makeText(context,
                    "${getString(R.string.fragment_open_pdf_file_uri2_pdf_saved_in_downloads)}  ${file.name}",
                    Toast.LENGTH_LONG).show()
            }

        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(
                context,
                "${getString(R.string.report_disability_for_work_adapter_error_save_pdf)} ${e.localizedMessage}",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun fileExists(resolver: ContentResolver, collection: android.net.Uri, fileName: String): Boolean {
        val projection = arrayOf(MediaStore.Downloads._ID)
        val selection = "${MediaStore.Downloads.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(fileName)

        resolver.query(collection, projection, selection, selectionArgs, null).use { cursor ->
            return cursor?.moveToFirst() == true
        }
    }

    private fun getUniqueFile(directory: File, baseFileName: String): File {
        var file = File(directory, baseFileName)
        var counter = 1
        val nameWithoutExtension = baseFileName.substringBeforeLast(".")
        val extension = baseFileName.substringAfterLast(".", "")

        while (file.exists()) {
            val newName = if (extension.isNotEmpty()) {
                "$nameWithoutExtension ($counter).$extension"
            } else {
                "$nameWithoutExtension ($counter)"
            }
            file = File(directory, newName)
            counter++
        }
        return file
    }
}
