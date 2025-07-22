package com.example.zdravlje.ui.chatAudioAndVideoCall

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.app.democall.R
import com.app.democall.composesample.util.observers.network.ConnectivityObserverImpl
import com.app.democall.composesample.util.requestNeededPermissions
import com.example.zdravlje.services.avc.SampleApplication
import com.example.zdravlje.ui.base.BaseActivity
import io.livekit.android.events.RoomEvent
import io.livekit.android.events.collect
import io.livekit.android.room.track.LocalVideoTrack
import io.livekit.android.room.track.Track
import io.livekit.android.room.track.VideoTrack
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.nites.ezdravljeapp.databinding.ActivityCallBinding
import kotlin.time.Duration.Companion.seconds

class CallActivity : BaseActivity() {

    private lateinit var viewModel: CallViewModel
    private lateinit var binding: ActivityCallBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val sampleApp = this.application as SampleApplication

        viewModel = ViewModelProvider(
            this,
            CallViewModelFactory(
                intent.getStringExtra(EXTRA_URL) ?: "",
                intent.getStringExtra(EXTRA_TOKEN) ?: "",
                intent.getBooleanExtra(EXTRA_IS_VIDEO, true),
                intent.getStringExtra(EXTRA_CHAT_ID) ?: "",
                sampleApp.getSocketManager(),
                sampleApp.getCallManager(),
                ScreenStateManagerImpl(),
                ConnectivityObserverImpl(this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager),
                application
            )
        ).get(CallViewModel::class.java)

        requestNeededPermissions(
            onPermissionsGranted = {
                viewModel.resolveCameraPermission(true)
                viewModel.resolveAudioPermission(true)
                viewModel.resolveWriteLogPermission(true)
                viewModel.resolveReadLogPermission(true)
            },
            onAudioDenied = {
                viewModel.resolveAudioPermission(false)
            },
            onCameraDenied = {
                viewModel.resolveCameraPermission(false)
            },
            onWriteCallLogDenied = {
                viewModel.resolveWriteLogPermission(false)
            },
            onReadCallLogDenied = {
                viewModel.resolveReadLogPermission(false)
            }
        )

        // Inflate the XML layout
        binding = ActivityCallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
    }


    private fun setupUI() {
        viewModel.room.initVideoRenderer(binding.renderer)
        viewModel.room.initVideoRenderer(binding.localCamera)

        binding.cameraButton.setOnClickListener(View.OnClickListener {
            viewModel.setCameraEnabled(true)

//            val localTrack =
//                viewModel.room.localParticipant.getTrackPublication(Track.Source.CAMERA)?.track as? LocalVideoTrack
//
//            if (localTrack != null) {
//                attachLocalVideo(localTrack)
//            }
        })

        binding.endCallButton.setOnClickListener(View.OnClickListener {
            viewModel.flipCamera()
        })

        lifecycleScope.launch {
            viewModel.room.events.collect { event ->
                when (event) {
                    is RoomEvent.TrackSubscribed -> onTrackSubscribed(event)
                    else -> {}
                }
            }
        }

        // Start the timer
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

        lifecycleScope.launch {
            viewModel.screenState.observe(this@CallActivity) {
                when (it) {
                    is Disconnecting -> {
                        if (!it.isSelfDeclined) {
                            Toast.makeText(
                                this@CallActivity,
                                getString(R.string.user_declined_call_text),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        viewModel.leave()
                        finish()
                    }

                    is Connecting -> {
                        binding.titleTextView.text =
                            "${getString( com.nites.ezdravljeapp.R.string.fragment_audio_video_call_connecting)}..."
                    }

                    is Connected -> {
                        binding.titleTextView.text =
                            "${getString(com.nites.ezdravljeapp.R.string.fragment_audio_video_call_connected)}"

                    }

                    else -> {}
                }
            }

            // Observe participants
            viewModel.participants
                .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .collect { participants ->
                    val remoteParticipant = participants.firstOrNull()
                    val remoteTrack =
                        remoteParticipant?.getTrackPublication(Track.Source.CAMERA)?.track as? LocalVideoTrack
                    if (remoteTrack != null) {
                        attachRemoteVideo(remoteTrack)
                    }
                }

            // Observe title
            viewModel.title
                .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .collect { title ->
                    // Update UI with title
                }

            // Observe isGroup
            viewModel.isGroup
                .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .collect { isGroup ->
                    // Update UI based on isGroup
                }

            viewModel.cameraAllowed.observe(this@CallActivity) {

            }

            viewModel.audioAllowed.observe(this@CallActivity) {

            }

            viewModel.writeLogAllowed.observe(this@CallActivity) {

            }

            viewModel.readLogAllowed.observe(this@CallActivity) {

            }

            viewModel.primarySpeaker
                .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .collect { primarySpeaker ->
                    // Update UI with primary speaker
                }

            viewModel.activeSpeakers
                .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .collect { activeSpeakers ->
                    // Update UI with active speakers
                }

            viewModel.micEnabled.observe(this@CallActivity) {
            }

            viewModel.cameraEnabled.observe(this@CallActivity) { enabled ->

                binding.cameraButton.setOnClickListener { viewModel.setCameraEnabled(!enabled) }
                binding.ivCamera.setImageResource(
                    if (enabled) {
                        R.drawable.outline_videocam_24
                    } else {
                        R.drawable.outline_videocam_off_24
                    },
                )
                binding.flipCamera.isEnabled = enabled
            }

            viewModel.screenshareEnabled.observe(this@CallActivity) {

            }

            viewModel.permissionAllowed
                .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .collect { permissionAllowed ->
                    // Update UI based on permissionAllowed
                }

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

    private fun onTrackSubscribed(event: RoomEvent.TrackSubscribed) {
        val track = event.track
        if (track is VideoTrack) {
            attachRemoteVideo(track)
        }
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
            chatId: String
        ): Intent {
            return Intent(context, CallActivity::class.java).apply {
                putExtra(EXTRA_URL, url)
                putExtra(EXTRA_TOKEN, token)
                putExtra(EXTRA_IS_VIDEO, isVideo)
                putExtra(EXTRA_CHAT_ID, chatId)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.updateCurrentScreenState()
    }

    override fun onResume() {
        super.onResume()
        observeErrors()
        observeData()
    }

    private fun observeErrors() {
        lifecycleScope.launch {
            viewModel.error
                .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .collect {
                    if (it != null) {
                        Toast.makeText(
                            this@CallActivity,
                            "${getString(com.nites.ezdravljeapp.R.string.error)}: $it",
                            Toast.LENGTH_LONG
                        ).show()
                        viewModel.dismissError()
                    }
                }
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.dataReceived
                .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .collect {
                    Toast.makeText(
                        this@CallActivity,
                        "${getString(com.nites.ezdravljeapp.R.string.fragment_audio_video_call_data_received)}: $it",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    viewModel.dismissError()
                }
        }
    }

    private fun attachLocalVideo(videoTrack: VideoTrack) {
        videoTrack.addRenderer(binding.localCamera)
    }

    private fun attachRemoteVideo(videoTrack: VideoTrack) {
        videoTrack.addRenderer(binding.renderer)
        // binding.progress.visibility = View.GONE
    }
}
