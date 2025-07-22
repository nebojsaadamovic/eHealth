package com.example.zdravlje.data.model.response.healthcare

data class StaticRagResponse(
    val id: String,
    val answer: String,
    val score: Int,
    val note: NoteStaticRag
)

data class NoteStaticRag(
    val warnings: List<String>,
    val info: List<String>
)