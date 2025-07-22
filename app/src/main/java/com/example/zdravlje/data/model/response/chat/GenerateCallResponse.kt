package com.example.zdravlje.data.model.response.chat

data class GenerateCallResponse(
    val eve_start_time: String,
    val eve_end_time: String,
    val eve_title: String,
    val eve_payload: EvePayload
)

data class EvePayload(
    val participants: List<ParticipantData>,
    val vcc_id: String
)

data class ParticipantData(
    val sgco_id: Int,
    val sgco_name: String,
    val email: String,
    val creator: Int
)