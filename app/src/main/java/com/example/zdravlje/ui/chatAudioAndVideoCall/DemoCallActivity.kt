package com.example.zdravlje.ui.chatAudioAndVideoCall

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.projection.MediaProjectionManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.democall.composesample.util.observers.network.ConnectivityObserverImpl
import com.example.zdravlje.services.avc.SampleApplication
import com.example.zdravlje.ui.base.BaseActivity
import com.xwray.groupie.GroupieAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.ActivityDemoCallBinding
import kotlin.time.Duration.Companion.seconds

class DemoCallActivity : BaseActivity() {

    private lateinit var binding: ActivityDemoCallBinding
    private lateinit var viewModel: CallViewModel
    private lateinit var screenCaptureIntentLauncher: ActivityResultLauncher<Intent>
    private var mpCallerRingtone: MediaPlayer? = null

    @androidx.camera.camera2.interop.ExperimentalCamera2Interop
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding = ActivityDemoCallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initRingtone()
        val sampleApp = this.application as SampleApplication

        viewModel = ViewModelProvider(
            this,
            CallViewModelFactory(
                intent.getStringExtra(DemoCallActivity.EXTRA_URL) ?: "",
                intent.getStringExtra(DemoCallActivity.EXTRA_TOKEN) ?: "",
                intent.getBooleanExtra(DemoCallActivity.EXTRA_IS_VIDEO, true),
                intent.getStringExtra(DemoCallActivity.EXTRA_CHAT_ID) ?: "",
                sampleApp.getSocketManager(),
                sampleApp.getCallManager(),
                ScreenStateManagerImpl(),
                ConnectivityObserverImpl(this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager),
                application
            )
        ).get(CallViewModel::class.java)

        screenCaptureIntentLauncher =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult(),
            ) { result ->
                val resultCode = result.resultCode
                val data = result.data
                if (resultCode != Activity.RESULT_OK || data == null) {
                    return@registerForActivityResult
                }
                //  viewModel.startScreenCapture(data)
            }

        // Audience row setup
        val audienceAdapter = GroupieAdapter()
        binding.audienceRow.apply {
            layoutManager =
                LinearLayoutManager(this@DemoCallActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = audienceAdapter
        }

        lifecycleScope.launchWhenCreated {
            viewModel.participants
                .collect { participants ->
                    val items = participants.map { participant ->
                        ParticipantItem(
                            room = viewModel.room,
                            participant = participant
                        )
                    }
                    audienceAdapter.update(items)
                }
        }

        // speaker view setup
        val speakerAdapter = GroupieAdapter()
        binding.speakerView.apply {
            layoutManager =
                LinearLayoutManager(this@DemoCallActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = speakerAdapter
        }
        lifecycleScope.launchWhenCreated {
            viewModel.primarySpeaker.collectLatest { speaker ->
                val items = listOfNotNull(speaker)
                    .map { participant ->
                        ParticipantItem(
                            viewModel.room,
                            participant,
                            speakerView = true
                        )
                    }
                speakerAdapter.update(items)
            }
        }

        // Controls setup
        viewModel.cameraEnabled.observe(this) { enabled ->
            binding.camera.setOnClickListener { viewModel.setCameraEnabled(!enabled) }
            binding.ivCamera.setImageResource(
                if (enabled) {
                    com.app.democall.R.drawable.camera_on
                } else {
                    com.app.democall.R.drawable.camera_off
                },
            )
            binding.flipCamera.isEnabled = enabled
        }

        viewModel.micEnabled.observe(this) { enabled ->
            binding.mic.setOnClickListener { viewModel.setMicEnabled(!enabled) }
            binding.ivMic.setImageResource(
                if (enabled) {
                    com.app.democall.R.drawable.mic
                } else {
                    com.app.democall.R.drawable.mic_off
                },
            )
        }

        binding.flipCamera.setOnClickListener { viewModel.flipCamera() }
        viewModel.screenshareEnabled.observe(this) { enabled ->
            binding.screenShare.setOnClickListener {
                if (enabled) {
                    // viewModel.stopScreenCapture()
                } else {
                    requestMediaProjection()
                }
            }
            binding.screenShare.setImageResource(
                if (enabled) {
                    com.app.democall.R.drawable.baseline_cast_connected_24
                } else {
                    com.app.democall.R.drawable.baseline_cast_24
                },
            )
        }

        binding.message.setOnClickListener {
            val editText = EditText(this)
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.fragment_audio_video_call_send_message))
                .setView(editText)
                .setPositiveButton(getString(R.string.fragment_audio_video_call_send)) { dialog, _ ->
                    viewModel.sendData(editText.text?.toString() ?: "")
                }
                .setNegativeButton(getString(R.string.fragment_audio_video_call_cancel)) { _, _ -> }
                .create()
                .show()
        }

//        viewModel.enhancedNsEnabled.observe(this) { enabled ->
//            binding.enhancedNs.visibility = if (enabled) {
//                android.view.View.VISIBLE
//            } else {
//                android.view.View.GONE
//            }
//        }
//
//        binding.enhancedNs.setOnClickListener {
//            showAudioProcessorSwitchDialog(viewModel)
//        }
//
        binding.exit.setOnClickListener {
            viewModel.leave()
            mpCallerRingtone?.stop()
            this.finish()

        }
//
//        // Controls row 2
//        binding.audioSelect.setOnClickListener {
//            showSelectAudioDeviceDialog(viewModel)
//        }
        lifecycleScope.launchWhenCreated {
            viewModel.permissionAllowed.collect { allowed ->

            }
        }

        lifecycleScope.launch {
            viewModel.screenState.observe(this@DemoCallActivity) {
                when (it) {
                    is Disconnecting -> {
                        if (!it.isSelfDeclined) {
                            Toast.makeText(
                                this@DemoCallActivity,
                                getString(com.app.democall.R.string.user_declined_call_text),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        viewModel.leave()
                        finish()
                    }

                    is Connecting -> {
                        binding.titleTextView.text =
                            "${getString(R.string.fragment_audio_video_call_connecting)}..."
                        mpCallerRingtone?.start()
                        mpCallerRingtone?.isLooping = true
                    }

                    is Connected -> {
                        mpCallerRingtone?.stop()
                        binding.titleTextView.text =
                            getString(R.string.fragment_audio_video_call_connected)
                        startCounting()
                    }

                    else -> {}
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launchWhenResumed {
            viewModel.error.collect {
                if (it != null) {
                    Toast.makeText(
                        this@DemoCallActivity,
                        "${getString(R.string.fragment_audio_video_call_connected)}: $it",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.i("errorHandler", it.toString())
                    viewModel.dismissError()
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.dataReceived.collect {
                Toast.makeText(this@DemoCallActivity, "${getString(R.string.fragment_audio_video_call_data_received)}: $it", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun requestMediaProjection() {
        val mediaProjectionManager =
            getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        screenCaptureIntentLauncher.launch(mediaProjectionManager.createScreenCaptureIntent())
    }

    override fun onDestroy() {
        binding.audienceRow.adapter = null
        binding.speakerView.adapter = null
        super.onDestroy()
    }

    companion object {
        private const val EXTRA_URL = "url"
        private const val EXTRA_TOKEN = "token"
        private const val EXTRA_IS_VIDEO = "isVideo"
        private const val EXTRA_CHAT_ID = "chatId"

        fun newIntent(
            context: Context,
            url: String,
            token: String,
            isVideo: Boolean,
            chatId: String,
        ): Intent {
            return Intent(context, DemoCallActivity::class.java).apply {
                putExtra(EXTRA_URL, url)
                putExtra(EXTRA_TOKEN, token)
                putExtra(EXTRA_IS_VIDEO, isVideo)
                putExtra(EXTRA_CHAT_ID, chatId)
            }
        }
    }

    private fun startCounting() {
        lifecycleScope.launch {
            var ticks = 0
            while (true) {
                delay(1.seconds)
                ticks++
                val minutes = (ticks / 60).toString().padStart(2, '0')
                val seconds = (ticks % 60).toString().padStart(2, '0')
                binding.timerTextView.text = "$minutes:$seconds"
                viewModel.duration = ticks
            }
        }
    }

    private fun initRingtone() {
        mpCallerRingtone = MediaPlayer.create(this, R.raw.ringback)
        mpCallerRingtone?.setOnCompletionListener { mp ->
            var mp = mp
            mp!!.reset()
            mp.release()
            mp = null
        }
    }
}
