package com.example.zdravlje.data.model.request.calendar

data class ScreeningProgramsRequest(
    val code: String,
    val description: String,
    val validTo: String,
    val validFrom: String,
    val genderDescription: String,
    val programId: Int,
    val availableResources: List<AvailableResource>?
)

data class AvailableResource(
    val resourceName: String,
    val resourceId: String
)