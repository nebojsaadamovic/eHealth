package com.example.zdravlje.data.model.response.login

data class TicketResponse(
    val id: Int,
    val queue: Int,
    val title: String,
    val description: String?,
    val resolution: String?,
    val submitter_email: String,
    val assigned_to: Int?,
    val status: Int,
    val on_hold: Boolean,
    val priority: Int,
    val due_date: String?,
    val merged_to: Int?,
    val followup_set: List<Followup>
)

data class Followup(
    val id: Int,
    val ticket: Int,
    val date: String,
    val title: String,
    val comment: String?,
    val public: Boolean,
    val user: Int,
    val new_status: Int?,
    val message_id: String?,
    val time_spent: String?,
    val followupattachment_set: List<FollowupAttachment>
)

data class FollowupAttachment(
    val id: Int,
    val followup: Int,
    val file: String,
    val filename: String,
    val mime_type: String,
    val size: Int
)

