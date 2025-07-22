package com.example.zdravlje.data.model.response.chat

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchContactResponse(
    val sgco_id: String ?= "",
    val sgco_name: String? = null,
    val municipality: String? = null,
    val birth_year: Int? = null,
    val avt_encoded_value: String? = null,
    val avt_ext: String? = null,
    val email: String? = "",
    val creator: String? = null
) : Parcelable


@Parcelize
data class Participant(
    val sgco_id: Int? = 0,
    val sgco_name: String? = null,
    val municipality: String? = null,
    val birth_year: Int? = null,
    val avt_encoded_value: String? = null,
    val avt_ext: String? = null,
    val email: String? = "",
    val creator: String? = null
) : Parcelable