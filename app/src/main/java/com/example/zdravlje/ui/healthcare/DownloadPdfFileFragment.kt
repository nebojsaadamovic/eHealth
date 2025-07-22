package com.example.zdravlje.ui.healthcare

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentDownloadPdfFileBinding
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

class DownloadPdfFileFragment : Fragment() {

    private lateinit var binding: FragmentDownloadPdfFileBinding

    companion object {
        private const val ARG_URL = "arg_url"
        private const val ARG_FILE_NAME = "arg_file_name"

        fun newInstance(url: String, fileName: String): DownloadPdfFileFragment {
            val fragment = DownloadPdfFileFragment()
            val bundle = Bundle()
            bundle.putString(ARG_URL, url)
            bundle.putString(ARG_FILE_NAME, fileName)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDownloadPdfFileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val url = arguments?.getString(ARG_URL)
        val fileName = arguments?.getString(ARG_FILE_NAME)

        binding.mtvTitle.text = fileName
        if (url != null && fileName != null) {
            cashAndDisplayPdf(url)
        }

        binding.mtvDownload.setOnClickListener {
            val parseFileName = URL(url).path.split("/").last()
            val downloadsDirectory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val uniqueFileName = getUniqueFileName(downloadsDirectory, parseFileName)
            downloadPdfToDownloadsFolder(url!!, uniqueFileName)
        }

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    // Funkcija za SSL konfiguraciju
    private fun getSSLSocketFactory(): SSLSocketFactory {
        val trustAllCertificates = arrayOf(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCertificates, java.security.SecureRandom())
        return sslContext.socketFactory
    }

    private fun cashAndDisplayPdf(url: String) {
        Thread {
            try {
                val fileName = URL(url).path.split("/").last()
                val client = OkHttpClient.Builder()
                    .sslSocketFactory(getSSLSocketFactory(), object : X509TrustManager {
                        override fun checkClientTrusted(
                            chain: Array<X509Certificate>,
                            authType: String
                        ) {
                        }

                        override fun checkServerTrusted(
                            chain: Array<X509Certificate>,
                            authType: String
                        ) {
                        }

                        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                    })
                    .hostnameVerifier { hostname, session -> true }  // Prihvatanje svih hostova (samo za testiranje)
                    .build()

                val request = Request.Builder()
                    .url(url)
                    .build()

                val response: Response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val inputStream: InputStream = response.body?.byteStream()
                        ?: throw Exception("Greška pri otvaranju i kesiranju PDF-a")
                    val outputFile = File(requireContext().cacheDir, fileName)
                    val outputStream = FileOutputStream(outputFile)
                    inputStream.use { input ->
                        outputStream.use { output ->
                            input.copyTo(output)
                        }
                    }
                    requireActivity().runOnUiThread {
                        displayPdfFromFile(outputFile)
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.fragment_download_pdf_file_pdf_cached_successfully),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    throw Exception("Greška pri kesiranju i otvaranju PDF-a.")
                }
            } catch (e: Exception) {
                binding.mtvMsgEmptyFile.visibility= View.VISIBLE
                binding.mtvDownload.alpha = 0.2f
                binding.mtvDownload.isEnabled = false

                e.printStackTrace()
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.fragment_download_pdf_file_cache_open_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }.start()
    }

    private fun displayPdfFromFile(pdfFile: File) {
        try {
            val parcelFileDescriptor = ParcelFileDescriptor.open(
                pdfFile,
                ParcelFileDescriptor.MODE_READ_ONLY
            )

            val pdfRenderer = PdfRenderer(parcelFileDescriptor)
            val pageCount = pdfRenderer.pageCount

            for (i in 0 until pageCount) {
                val page = pdfRenderer.openPage(i)

                val scaleFactor = resources.displayMetrics.widthPixels.toFloat() / page.width
                val scaledHeight = (page.height * scaleFactor).toInt()

                val bitmap = Bitmap.createBitmap(
                    resources.displayMetrics.widthPixels,
                    scaledHeight,
                    Bitmap.Config.ARGB_8888
                )

                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                val imageView = ImageView(requireContext())
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.setMargins(16, 16, 16, 16)
                imageView.layoutParams = layoutParams
                imageView.setBackgroundColor(android.graphics.Color.WHITE)
                imageView.setImageBitmap(bitmap)
                binding.pdfContainer.addView(imageView)
                page.close()
            }
            pdfRenderer.close()
            parcelFileDescriptor.close()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                requireContext(),
                getString(R.string.fragment_download_pdf_file_display_error),
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    private fun downloadPdfToDownloadsFolder(urlString: String, fileName: String) {
        try {
            val downloadsDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)
            Thread {
                try {
                    val url = URL(urlString)
                    val sslSocketFactory = getSSLSocketFactory()
                    val connection = url.openConnection()
                    if (connection is javax.net.ssl.HttpsURLConnection) {
                        connection.sslSocketFactory = sslSocketFactory
                    }
                    val inputStream: InputStream = connection.getInputStream()
                    val outputStream: FileOutputStream = FileOutputStream(file)
                    inputStream.copyTo(outputStream)
                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.fragment_download_pdf_file_success_download),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.fragment_download_pdf_file_error_download),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    e.printStackTrace()
                }
            }.start()

        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                getString(R.string.fragment_download_pdf_file_error_start_download),
                Toast.LENGTH_SHORT
            ).show()
            e.printStackTrace()
        }
    }

    private fun getUniqueFileName(directory: File, baseName: String): String {
        var fileName = baseName
        var file = File(directory, fileName)
        var counter = 1

        while (file.exists()) {
            val extensionIndex = baseName.lastIndexOf(".")
            val nameWithoutExtension =
                if (extensionIndex != -1) baseName.substring(0, extensionIndex) else baseName
            val extension = if (extensionIndex != -1) baseName.substring(extensionIndex) else ""
            fileName = "${nameWithoutExtension}(${counter})${extension}"
            file = File(directory, fileName)
            counter++
        }
        return fileName
    }
}
