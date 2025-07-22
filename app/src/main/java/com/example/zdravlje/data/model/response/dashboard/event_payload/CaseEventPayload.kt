package com.example.zdravlje.data.model.response.dashboard.event_payload

import java.io.Serializable

data class CaseEventPayload(
    val caseDate: String,
    val caseInstitution: String,
    val institutionCode: String,
    val detail: CaseDetail
): Serializable