package com.example.zdravlje.data.model.response.chat

data class SearchPermisionResponse(
    val podosiguranici: List<UserContact>,
    val korisnici: List<UserContact>,
    val doktori: List<UserContact>,
    val farmaceuti: List<UserContact>
)

fun UserContact.toDisplayString(): String {
    return """
        UserContact(
            sgco_id=$sgco_id,
            user_id=$user_id,
            sgco_name=$sgco_name,
            email=$email,
            avt_encoded_value=HIDDEN,
            status=$status,
            last_activity=$last_activity,
            type_id=$type_id,
            can_see_profile=$can_see_profile,
            sharing=$sharing,
            isSelected=$isSelected
        )
    """.trimIndent()
}
