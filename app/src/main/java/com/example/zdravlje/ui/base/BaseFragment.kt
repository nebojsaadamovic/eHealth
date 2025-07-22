package com.example.zdravlje.ui.base

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.zdravlje.data.sharedPref.PreferenceHelper
import com.example.zdravlje.ui.base.bottomsheet.BottomSheetSessionFragment
import com.example.zdravlje.ui.login.LoginActivity
import com.google.firebase.messaging.FirebaseMessaging


open class BaseFragment : Fragment(), BottomSheetSessionFragment.OnValueChangeListener {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    private fun logout() {
        clearPin()
        clearToken()
        //   profileViewModel.logout()
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }


    fun getStoredPin(): String {
        return context?.let { PreferenceHelper.getPin(it) } ?: ""
    }

    fun savePin(pin: String) {
        context?.let { PreferenceHelper.savePin(it, pin) }
    }

    fun clearPin() {
        context?.let { PreferenceHelper.clearPin(it) }
    }

    fun isPinSet(): Boolean {
        val pin = getStoredPin()
        return !pin.isNullOrBlank()
    }


    fun getStoredToken(): String {
        return context?.let { PreferenceHelper.getToken(it) } ?: ""
    }

    fun saveToken(pin: String) {
        context?.let { PreferenceHelper.saveToken(it, pin) }
    }

    fun clearToken() {
        context?.let { PreferenceHelper.clearToken(it) }
    }

    fun isTokenSet(): Boolean {
        val pin = getStoredToken()
        return !pin.isNullOrBlank()
    }


    fun getStoredFcmToken(): String {
        return context?.let { PreferenceHelper.getFcmToken(it) } ?: ""
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

    override fun onValueChange() {
        logout()
    }


    fun showErrorDialog() {
        val tag = "BottomSheetSession"

        val existingFragment = requireActivity().supportFragmentManager.findFragmentByTag(tag)
        if (existingFragment == null || !existingFragment.isVisible) {
            val bottomSheet = BottomSheetSessionFragment()
            bottomSheet.isCancelable = false
            bottomSheet.setValueChange(this)
            bottomSheet.show(requireActivity().supportFragmentManager, tag)
        }
    }
}