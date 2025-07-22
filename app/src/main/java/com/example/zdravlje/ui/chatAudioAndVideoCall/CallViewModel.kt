package com.example.zdravlje.ui.chatAudioAndVideoCall

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.media.AudioManager
import android.provider.CallLog
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.app.democall.composesample.data.call.AcceptedCall
import com.app.democall.composesample.data.call.CallResult
import com.app.democall.composesample.data.call.DeclinedCall
import com.app.democall.composesample.data.call.LeftCall
import com.app.democall.composesample.data.user.UserNameAvatar
import com.app.democall.composesample.util.observers.network.ConnectivityObserver
import com.app.democall.extensions.hide
import com.app.democall.sdk.call.api.AvcSdkCall
import com.app.democall.socket.SocketManager
import com.google.gson.Gson
import io.livekit.android.ConnectOptions
import io.livekit.android.LiveKit
import io.livekit.android.RoomOptions
import io.livekit.android.audio.AudioSwitchHandler
import io.livekit.android.events.RoomEvent
import io.livekit.android.events.collect
import io.livekit.android.room.Room
import io.livekit.android.room.participant.LocalParticipant
import io.livekit.android.room.participant.Participant
import io.livekit.android.room.participant.RemoteParticipant
import io.livekit.android.room.track.CameraPosition
import io.livekit.android.room.track.LocalVideoTrack
import io.livekit.android.room.track.LocalVideoTrackOptions
import io.livekit.android.room.track.Track
import io.livekit.android.room.track.VideoCaptureParameter
import io.livekit.android.util.flow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

internal class CallViewModel(
    private val url: String,
    private val token: String,
    private val isVideo: Boolean,
    private val chatId: String,
    private val socketManager: SocketManager,
    private val callManager: AvcSdkCall,
    private val screenStateManager: ScreenStateManager,
    private val connectivityObserver: ConnectivityObserver,
    application: Application,
) : AndroidViewModel(application) {

    val viewModelOutliveScope = CoroutineScope(SupervisorJob())

    val room = LiveKit.create(
        appContext = application,
        options = RoomOptions(
            adaptiveStream = true,
            dynacast = true,
        )
    )

    val audioHandler = (room.audioHandler as AudioSwitchHandler)

    val participants = room::remoteParticipants.flow
        .map { remoteParticipants ->
            listOf<Participant>(room.localParticipant) +
                    remoteParticipants
                        .keys
                        .sortedBy { it.value }
                        .mapNotNull { remoteParticipants[it] }
        }

    val isGroup = participants
        .map { it.size > 2 }

    val title = room::remoteParticipants.flow
        .map {
            val iterator = it.entries.iterator()
            if (iterator.hasNext()) {
                val participant = iterator.next().value

                val userInfo: UserNameAvatar? = Gson().fromJson(
                    participant.metadata,
                    UserNameAvatar::class.java
                )
                userInfo?.name ?: ""
            } else {
                ""
            }
        }

    private val mutableError = MutableStateFlow<Throwable?>(null)
    val error = mutableError.hide()

    private val mutablePrimarySpeaker = MutableStateFlow<Participant?>(null)
    val primarySpeaker: StateFlow<Participant?> = mutablePrimarySpeaker

    val activeSpeakers = room::activeSpeakers.flow

    // Controls
    private val mutableMicEnabled = MutableLiveData(true)
    val micEnabled = mutableMicEnabled.hide()

    private val mutableCameraEnabled = MutableLiveData(isVideo)
    val cameraEnabled = mutableCameraEnabled.hide()

    private val mutableCameraAllowed = MutableLiveData(true)
    val cameraAllowed = mutableCameraAllowed.hide()

    fun resolveCameraPermission(allowed: Boolean) {
        mutableCameraAllowed.postValue(allowed)
    }

    private val mutableAudioAllowed = MutableLiveData(true)
    val audioAllowed = mutableAudioAllowed.hide()

    fun resolveAudioPermission(allowed: Boolean) {
        mutableAudioAllowed.postValue(allowed)
    }

    private val mutableWriteLogAllowed = MutableLiveData(true)
    val writeLogAllowed = mutableWriteLogAllowed.hide()

    fun resolveWriteLogPermission(allowed: Boolean) {
        mutableWriteLogAllowed.postValue(allowed)
    }

    private val mutableReadLogAllowed = MutableLiveData(true)
    val readLogAllowed = mutableReadLogAllowed.hide()

    fun resolveReadLogPermission(allowed: Boolean) {
        mutableReadLogAllowed.postValue(allowed)
    }

    private val mutableScreenCallState = MutableLiveData<CallState>(Connecting)
    val screenState = mutableScreenCallState.hide().distinctUntilChanged()

    fun pushScreenState(callState: CallState) {
        mutableScreenCallState.postValue(callState)
    }

    private val mutableScreencastEnabled = MutableLiveData(false)
    val screenshareEnabled = mutableScreencastEnabled.hide()

    // Emits a string whenever a data message is received.
    private val mutableDataReceived = MutableSharedFlow<String>()
    val dataReceived = mutableDataReceived

    // Whether other participants are allowed to subscribe to this participant's tracks.
    private val mutablePermissionAllowed = MutableStateFlow(true)
    val permissionAllowed = mutablePermissionAllowed.hide()

    var messagesReceived = 0

    private var callLogsCalled = false

    var duration: Int = 0

    private val _showInternetConnectionError = MutableStateFlow(false)
    val showInternetConnectionError = _showInternetConnectionError.asStateFlow()

    fun hideInternetError() = viewModelScope.launch {
        _showInternetConnectionError.emit(false)
    }

    init {
        observeInternetConnection()

        screenStateManager
            .screenState
            .debounce(1000L)
            .filterIsInstance<Call>()
            .onEach {
                if (it.shouldBeClosed) {
                    onCancelCallClicked()
                }
            }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            // Collect any errors.
            launch {
                error.collect { Log.i("PRPRPR", "error$it") }
            }

            // Handle any changes in speakers.
            launch {
                combine(
                    participants,
                    activeSpeakers
                ) { participants, speakers -> participants to speakers }
                    .collect { (participantsList, speakers) ->
                        handlePrimarySpeaker(
                            participantsList,
                            speakers,
                            room
                        )
                    }
            }

            launch {
                participants
                    .filterNot {
                        val callState = mutableScreenCallState.value
                        callState == Connected || callState is Disconnecting
                    }
                    .map { it.size }
                    .collect {
                        if (it > 1) {
                            mutableScreenCallState.postValue(Connected)
                        }
                    }
            }

            // Handle room events.
            launch {
                room.events.collect {
                    when (it) {
                        is RoomEvent.FailedToConnect -> {
                            mutableError.value = it.error
                            pushScreenState(Disconnecting(true))
                        }

                        is RoomEvent.DataReceived -> {
                            val identity = it.participant?.identity ?: "server"
                            messagesReceived++
                            Log.i("PRPRPR", "message received from $identity, count $messagesReceived")
                        }

                        else -> {
                            Log.i("PRPRPR", "Room event: $it")
                        }
                    }
                }
            }

            connectToRoom()

            listenIsCallIsAlive()
            listenIsCallAccepted()
            listenIsCallDeclined()
        }
    }

    private fun observeInternetConnection() {
        connectivityObserver.networkStatusFlow()
            .onStart {
                emit(connectivityObserver.getStatus())
            }
            .map {
                it != ConnectivityObserver.Status.AVAILABLE
            }
            .onEach {
                _showInternetConnectionError.emit(it)
            }
            .launchIn(viewModelScope)
    }

    private suspend fun connectToRoom() {
        try {
            room.connect(
                url = url,
                token = token,
                options = ConnectOptions()
            )


            // video resolution settings
            val captureParams = VideoCaptureParameter(
                width = 640,
                height = 480,
                maxFps = 20,
            )
            val localVideoTrackOptions = LocalVideoTrackOptions(captureParams=captureParams)
            // end video resolution settings


            // Create and publish audio/video tracks
            val localParticipant = room.localParticipant

            localParticipant.videoTrackCaptureDefaults=localVideoTrackOptions

            localParticipant.setMicrophoneEnabled(true)
            mutableMicEnabled.postValue(localParticipant.isMicrophoneEnabled())

            localParticipant.setCameraEnabled(isVideo)
            mutableCameraEnabled.postValue(localParticipant.isCameraEnabled())

            // Update the speaker
            handlePrimarySpeaker(emptyList(), emptyList(), room)
        } catch (e: Throwable) {
            mutableError.value = e
        }
    }

    private fun handlePrimarySpeaker(
        participantsList: List<Participant>,
        speakers: List<Participant>,
        room: Room?
    ) {
        var speaker = mutablePrimarySpeaker.value

        // If speaker is local participant (due to defaults),
        // attempt to find another remote speaker to replace with.
        if (speaker is LocalParticipant) {
            val remoteSpeaker = participantsList
                .filterIsInstance<RemoteParticipant>() // Try not to display local participant as speaker.
                .firstOrNull()

            if (remoteSpeaker != null) {
                speaker = remoteSpeaker
            }
        }

        // If previous primary speaker leaves
        if (!participantsList.contains(speaker)) {
            // Default to another person in room, or local participant.
            speaker = participantsList.filterIsInstance<RemoteParticipant>()
                .firstOrNull()
                ?: room?.localParticipant
        }

        if (speakers.isNotEmpty() && !speakers.contains(speaker)) {
            val remoteSpeaker = speakers
                .filterIsInstance<RemoteParticipant>() // Try not to display local participant as speaker.
                .firstOrNull()

            if (remoteSpeaker != null) {
                speaker = remoteSpeaker
            }
        }

        mutablePrimarySpeaker.value = speaker
    }

    private fun listenIsCallIsAlive() {
        socketManager.listenEvent(LeftCall, CallResult::class.java) {
            pushScreenState(Disconnecting(false))
        }
    }

    private fun listenIsCallAccepted() {
        socketManager.listenEvent(AcceptedCall, CallResult::class.java) {
            if (chatId == it.chatId) {
                pushScreenState(Connected)
            }
        }
    }

    private fun listenIsCallDeclined() {
        socketManager.listenEvent(DeclinedCall, CallResult::class.java) {
            pushScreenState(Disconnecting(false))
        }
    }

    fun startChat() {
      //  chatManager.startChat(chatId)
    }

    fun onCancelCallClicked() {
        viewModelScope.launch {
            leave()
            pushScreenState(Disconnecting(true))
        }
    }

    fun leave() {
        viewModelScope.launch {
         //   val callType = if (CallLogInfoManager.callType != CallLog.Calls.OUTGOING_TYPE) CallLogInfoManager.callType else CallLog.Calls.OUTGOING_TYPE
            title.collect {
                if (!callLogsCalled) {
                    addCallLog(
                        getApplication(),
                        it,
                        duration.toLong(),
                        1
                    )
                    callLogsCalled = true
                }
            }

           // callManager.endCall("")
            Log.i("PRPRPR", "fun leave() callManager.leave()")
            disconnect()
            Log.i("PRPRPR", "fun leave() disconnect()")
        }
    }

    fun enableSpeakerphone(context: Context, enable: Boolean) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        if (enable) {
            // Postavljanje moda na komunikaciju
            audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
            // Omogućavanje zvučnika
            audioManager.isSpeakerphoneOn = true
        } else {
            // Vraćanje na prethodni način rada
            audioManager.isSpeakerphoneOn = false
            audioManager.mode = AudioManager.MODE_NORMAL
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("PRPRPR", "override fun onCleared()")
        disconnect()
    }

    private fun disconnect() {
        viewModelOutliveScope.launch {
            room.disconnect()
            room.release()
        }
    }

    fun setMicEnabled(enabled: Boolean) {
        viewModelScope.launch {
            if (enabled && room.localParticipant.isMicrophoneEnabled()) {
                /* no-op */
            } else {
                room.localParticipant.setMicrophoneEnabled(enabled)
            }

            mutableMicEnabled.postValue(enabled)
        }
    }

    fun setCameraEnabled(enabled: Boolean) {
        viewModelScope.launch {
            room.localParticipant.setCameraEnabled(enabled)
            mutableCameraEnabled.postValue(enabled)
        }
    }

    fun flipCamera() {
        val videoTrack = room.localParticipant.getTrackPublication(Track.Source.CAMERA)
            ?.track as? LocalVideoTrack
            ?: return

        val newPosition = when (videoTrack.options.position) {
            CameraPosition.FRONT -> CameraPosition.BACK
            CameraPosition.BACK -> CameraPosition.FRONT
            else -> null
        }

        videoTrack.switchCamera(position = newPosition)
    }

    fun dismissError() {
        mutableError.value = null
    }

    fun sendData(message: String) {
        viewModelScope.launch {
            room.localParticipant.publishData(message.toByteArray(Charsets.UTF_8))
        }
    }

    // Debug functions
    fun simulateMigration() {
        room.sendSimulateScenario(Room.SimulateScenario.MIGRATION)
    }

    fun simulateNodeFailure() {
        room.sendSimulateScenario(Room.SimulateScenario.NODE_FAILURE)
    }

    fun updateCurrentScreenState() {
        viewModelScope.launch {
            screenStateManager.pushState(Call(false))
        }
    }

    private fun addCallLog(context: Context, number: String, duration: Long, type: Int) {
        try {
            val values = ContentValues().apply {
                put(CallLog.Calls.NUMBER, number)
                put(CallLog.Calls.DURATION, duration)
                put(CallLog.Calls.TYPE, type)
                put(CallLog.Calls.DATE, System.currentTimeMillis())
                put(CallLog.Calls.NEW, 1)
                put(CallLog.Calls.CACHED_NAME, "")
                put(CallLog.Calls.CACHED_NUMBER_TYPE, 0)
                put(CallLog.Calls.CACHED_NUMBER_LABEL, "")
            }
            context.contentResolver.insert(CallLog.Calls.CONTENT_URI, values)
            Log.i("PRPRPR", "addCallLog -> Success $values")
        } catch (e: SecurityException) {
//            Timber.e(e)
            Log.i("PRPRPR", "addCallLog -> SecurityException $e")
        }
    }
}

interface ScreenStateManager {
    val screenState: SharedFlow<ScreenState>

    suspend fun pushState(state:ScreenState)
    fun tryPushState(state: ScreenState)
}


sealed class ScreenState(val shouldBeClosed: Boolean)
internal class Chat(shouldBeClosed: Boolean = false) : ScreenState(shouldBeClosed)
internal class Call(shouldBeClosed: Boolean = false) : ScreenState(shouldBeClosed)
internal class IncomingCall(shouldBeClosed: Boolean = false) : ScreenState(shouldBeClosed)


internal sealed class CallState

internal object Connected : CallState()
internal class Disconnecting(val isSelfDeclined: Boolean) : CallState()
internal object Connecting : CallState()


