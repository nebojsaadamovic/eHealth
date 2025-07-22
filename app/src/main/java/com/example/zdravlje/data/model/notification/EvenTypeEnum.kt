package com.example.zdravlje.data.model.notification

enum class EventTypeEnum(val id: Int, val description: String) {
    ZERO_PARAM(0, "Sve"),
    MEDICATION_CONSUMPTION(1, "Konzumiranje leka"),
    SCHEDULED_CONSULTATION(2, "Zakazani pregled / Konsultacije"),
    AVAILABLE_THERAPY(3, "Dostupna terapija u apoteci"),
    FREE_ACTIVITY(4, "Slobodna aktivnost"),
    SCREENING_EXAM(5, "Skrining pregled"),
    REALIZED_EXAMS(6, "Realizovani pregledi");

    companion object {
        fun fromCode(code: Int): EventTypeEnum? {
            return values().find { it.id == code }
        }

        fun getDescriptionByCode(code: Int): String? {
            return fromCode(code)?.description
        }
    }
}
