package com.example.zdravlje.data.model.notification

enum class NotificationTypeEnum(val id: Int, val description: String) {
    VACCINATION(1, "Vakcinacija"),
    MEDICATIONS(5, "Lekovi"),
    SHARING_MEDICAL_DATA(8, "Deljenje medicinskih podataka"),
    LIFESTYLE(11, "Životni stil"),
    SOCIAL_MEDIA_MESSAGE(14, "Poruka na socijalnim mrežama"),
    SOCIAL_MEDIA_COMMENT(17, "Komentar na socijalnim mrežama"),
    INBOX_MESSAGE(20, "Poruka u inboxu"),
    CONTACT_REQUEST(23, "Zahtev za kontakt"),
    NEW_CONTENT_MEDICAL_ATLAS(26, "Novi sadržaj u medicinskom atlasu"),
    NEW_TOPIC(29, "Nova tema"),
    MISSED_MEDICATION(32, "Nije popijen lek"),
    LIFESTYLE_NOTE(35, "Napomena za životni stil"),
    MEDICAL_DATA_ACCESS(38, "Uvid u medicinske podatke"),
    MISSED_VACCINATION(41, "Propuštena vakcinacija"),
    SCHEDULED_EXAMINATION(44, "Zakazan pregled"),
    CONSULTATIONS(47, "Konsultacije"),
    SCHEDULED_VIDEO_CALL(50, "Zakazan video poziv"),
    PERSONALIZED_REMINDER(56, "Personalizovani podsetnik"),
    REQUEST_SHARING_MEDICAL_DATA(59, "Zahtev za deljenje medicinskih podataka"),
    CONFIRMATION_RECEIPT_PRESRIPTIONS(62, "Potvrda preuzimanja recepta"),
    REQUEST_ACCEPTED(65, "Prijateljstvo prihvaćeno"),
    REQUEST_REJECTED(68, "Prijateljstvo odbijeno");

    companion object {
        fun fromId(id: Int): NotificationTypeEnum? {
            return values().find { it.id == id }
        }

        fun getDescriptionById(id: Int): String? {
            return fromId(id)?.description
        }
    }
}