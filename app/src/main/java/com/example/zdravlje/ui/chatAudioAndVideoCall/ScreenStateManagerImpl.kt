package com.example.zdravlje.ui.chatAudioAndVideoCall


import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

internal class ScreenStateManagerImpl:
    com.example.zdravlje.ui.chatAudioAndVideoCall.ScreenStateManager {

    private val _screenState = MutableSharedFlow<ScreenState>(
        replay = 1,
        extraBufferCapacity = 1
    )
    override val screenState = _screenState.asSharedFlow()

    override suspend fun pushState(state: ScreenState) {
        _screenState.emit(state)
    }

    override fun tryPushState(state: ScreenState) {
        _screenState.tryEmit(state)
    }
}