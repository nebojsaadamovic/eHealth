package com.example.zdravlje.ui.chatAudioAndVideoCall.democall2

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.media.projection.MediaProjectionManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.dynamicanimation.animation.FloatPropertyCompat
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.democall.composesample.util.observers.network.ConnectivityObserverImpl
import com.app.democall.composesample.util.requestNeededPermissions
import com.example.zdravlje.services.avc.SampleApplication
import com.example.zdravlje.ui.chatAudioAndVideoCall.CallViewModel
import com.example.zdravlje.ui.chatAudioAndVideoCall.CallViewModelFactory
import com.example.zdravlje.ui.chatAudioAndVideoCall.Connected
import com.example.zdravlje.ui.chatAudioAndVideoCall.Connecting
import com.example.zdravlje.ui.chatAudioAndVideoCall.Disconnecting
import com.example.zdravlje.ui.chatAudioAndVideoCall.ParticipantItem
import com.example.zdravlje.ui.chatAudioAndVideoCall.ScreenStateManagerImpl
import com.xwray.groupie.GroupieAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentAudioVideoCallBinding
import kotlin.time.Duration.Companion.seconds

class AudioVideoCallFragment : Fragment() {

    private lateinit var binding: FragmentAudioVideoCallBinding
    private lateinit var viewModel: CallViewModel
    private lateinit var screenCaptureIntentLauncher: ActivityResultLauncher<Intent>
    private var mpCallerRingtone: MediaPlayer? = null

    companion object {
        private const val ARG_URL = "url"
        private const val ARG_TOKEN = "token"
        private const val ARG_IS_VIDEO = "isVideo"
        private const val ARG_CHAT_ID = "chatId"

        fun newInstance(
            url: String,
            token: String,
            isVideo: Boolean,
            chatId: String
        ): AudioVideoCallFragment {
            return AudioVideoCallFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_URL, url)
                    putString(ARG_TOKEN, token)
                    putBoolean(ARG_IS_VIDEO, isVideo)
                    putString(ARG_CHAT_ID, chatId)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAudioVideoCallBinding.inflate(inflater, container, false)
        return binding.root
    }

    @androidx.camera.camera2.interop.ExperimentalCamera2Interop
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sampleApp = requireActivity().application as SampleApplication
        val url = arguments?.getString(AudioVideoCallFragment.ARG_URL)
        val token = arguments?.getString(AudioVideoCallFragment.ARG_TOKEN)
        val isVideo = arguments?.getBoolean(AudioVideoCallFragment.ARG_IS_VIDEO)
        val chatId = arguments?.getString(AudioVideoCallFragment.ARG_CHAT_ID)

        binding.clBack.setOnClickListener {
            // hideElementsForSmallScreen()
            (requireActivity() as DemoCallActivity2).hideVideo()
        }

        viewModel = ViewModelProvider(
            this,
            CallViewModelFactory(
                url ?: "",
                token ?: "",
                isVideo ?: false,
                chatId ?: "",
                sampleApp.getSocketManager(),
                sampleApp.getCallManager(),
                ScreenStateManagerImpl(),
                ConnectivityObserverImpl(requireActivity().getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager),
                requireActivity().application
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

        requireActivity().requestNeededPermissions(
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

        val audienceAdapter = GroupieAdapter()
        binding.audienceRow.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
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
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
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
        viewModel.cameraEnabled.observe(requireActivity()) { enabled ->

            Log.i("ttassss", "usaou AudioVideoCallFramgent")

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

        viewModel.micEnabled.observe(requireActivity()) { enabled ->
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
        viewModel.screenshareEnabled.observe(requireActivity()) { enabled ->
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
            val editText = EditText(requireActivity())
            AlertDialog.Builder(requireActivity())
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
            requireActivity().finish()

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
            viewModel.screenState.observe(requireActivity()) {
                when (it) {
                    is Disconnecting -> {
                        if (!it.isSelfDeclined) {
                            Toast.makeText(
                                requireActivity(),
                                getString(com.app.democall.R.string.user_declined_call_text),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        viewModel.leave()
                        requireActivity().finish()
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
                    Toast.makeText(requireActivity(), "${R.string.error}: $it", Toast.LENGTH_LONG)
                        .show()
                    Log.i("errorHandler", it.toString())
                    viewModel.dismissError()
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.dataReceived.collect {
                Toast.makeText(
                    requireActivity(),
                    "${getString(R.string.fragment_audio_video_call_data_received)}: $it",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }
    }

    private fun requestMediaProjection() {
        val mediaProjectionManager =
            requireActivity().getSystemService(AppCompatActivity.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        screenCaptureIntentLauncher.launch(mediaProjectionManager.createScreenCaptureIntent())
    }

    override fun onDestroy() {
        binding.audienceRow.adapter = null
        binding.speakerView.adapter = null
        super.onDestroy()
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

    fun getSpringAnimation(
        view: View,
        springAnimationType: FloatPropertyCompat<View>,
        finalPosition: Float
    ): SpringAnimation {
        val animation = SpringAnimation(view, springAnimationType)
        // create a spring with desired parameters
        val spring = SpringForce()
        spring.finalPosition = finalPosition
        spring.stiffness = SpringForce.STIFFNESS_VERY_LOW // optional
        spring.dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY // optional
        // set your animation's spring
        animation.spring = spring
        return animation
    }

    fun hideElementsForSmallScreen() {
        binding.titleBox.visibility = View.GONE
        binding.clBack.visibility = View.GONE
        binding.audienceRow.visibility = View.GONE
        binding.linearLayout.visibility = View.GONE
    }

    fun showElementsForSmallScreen() {
        binding.titleBox.visibility = View.VISIBLE
        binding.clBack.visibility = View.VISIBLE
        binding.audienceRow.visibility = View.VISIBLE
        binding.linearLayout.visibility = View.VISIBLE
    }
}