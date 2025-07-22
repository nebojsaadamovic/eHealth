package com.example.zdravlje.data.model.response.healthcare

data class InteractionHistoryResponse(
    val total_count: Int,
    val page: Int,
    val size: Int,
    val histories: List<InteractionHistoryItem>
)