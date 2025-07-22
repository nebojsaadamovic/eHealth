package com.example.zdravlje.ui.base

import com.example.zdravlje.data.sharedPref.PreferenceHelper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.messaging.FirebaseMessaging


open class BaseBottomSheetDialogFragment : BottomSheetDialogFragment() {

    fun getStoredPin(): String {
        return context?.let { PreferenceHelper.getPin(it) }?: ""
    }

    fun savePin(pin: String) {
        context?.let { PreferenceHelper.savePin(it, pin) }
    }

    fun getStoredFcmToken(): String? {
        return context?.let { PreferenceHelper.getFcmToken(it) }?: ""
    }

    fun getToken(): String? {
        return context?.let { PreferenceHelper.getToken(it) }?: ""
    }

    fun isPinSet(): Boolean {
        val pin = getStoredPin()
        return !pin.isNullOrBlank()
    }

    fun getFcmTokenFromFirebase() {
        context?.let { ctx ->
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task ->
                    try {
                        val token = task.result
                        PreferenceHelper.saveFcmToken(ctx, token)
                    } catch (e: Exception) {
                        // todo: log or handle error
                    }
                }
        }
    }
}