package com.example.zdravlje.data.model.response.chat

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserContact(
    val sgco_id: Int,
    val user_id: Int,
    val sgco_name: String,
    val email: String,
    val avt_encoded_value: String?,
    val avt_ext: String?,
    val status: Int,
    val last_activity: Long?,
    val type_id: Int,
    val can_see_profile: Int,
    val sharing: Sharing,
    var isSelected: Boolean=false
):Serializable

data class Sharing(
    val me2contact: Permissions,
    val contact2me: Permissions
):Serializable

data class Permissions(
    @SerializedName("permission_1")
    val permission1: List<String>?,  // parametri nacin zivota
    @SerializedName("permission_2")
    val permission2: List<String>?,  // pristup profilu
    @SerializedName("permission_3")
    val permission3: List<String>?,
    @SerializedName("permission_4")
    val permission4: List<String>?,
    @SerializedName("permission_5")  // lek
    val permission5: List<String>?,
    @SerializedName("permission_6")
    val permission6: List<String>?, //  eKarton
    @SerializedName("permission_999999")
    val permission999999: List<String>?
):Serializable
