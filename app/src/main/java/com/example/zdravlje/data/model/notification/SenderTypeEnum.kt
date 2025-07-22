package com.example.zdravlje.data.model.notification

enum class SenderTypeEnum(val id: Any, val description: String) {
    NO_EVENT(0, "Nema prateći event"),
    E_RECEPT(1, "eRecept"),
    E_KARTON(2, "eKarton"),
    E_DOSIJE(3, "eDosije"),
    E_ZDRAVLJE(4, "eZdravlje"),
    ALL(5, "Sve");

    companion object {
        fun fromId(id: Any): SenderTypeEnum? {
            return values().find { it.id == id }
        }

        fun getDescriptionById(id: Any): String? {
            return fromId(id)?.description
        }
    }
}
