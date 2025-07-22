package com.example.zdravlje.data.model.response.profile

data class Permission(
    val permission_1: String? = null,
    val permission_2: String? = null,
    val permission_3: String? = null,
    val permission_4: String? = null,
    val permission_5: String? = null,
    val permission_6: String? = null,
    val permission_999999: String? = null
)
{
    // Funkcija koja vraća listu svih permisija
    fun givenPermissions(): List<String?> {
        return listOf(permission_1, permission_2, permission_3, permission_4, permission_5, permission_6, permission_999999)
    }
}