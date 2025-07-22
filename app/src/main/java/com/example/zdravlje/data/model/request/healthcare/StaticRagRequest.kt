package com.example.zdravlje.data.model.request.healthcare

data class StaticRagRequest(
    val query: List<QueryItem>,
    val properties: Map<String, Any> = emptyMap()
)

data class QueryItem(
    val name: String,
    val type: String
)