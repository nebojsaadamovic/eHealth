package com.example.zdravlje.utilities

object ValidationUtils {

    const val EMAIL_REGEX = "^[A-Za-z](.*)([@])(.+)(\\.)(.+)";

    fun isEmailValid(email: String): Boolean {
        return EMAIL_REGEX.toRegex().matches(email);
    }

    fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val regex = Regex("^(\\d{3})-(\\d{3})-(\\d{4})$")
        return regex.matches(phoneNumber)
    }
}