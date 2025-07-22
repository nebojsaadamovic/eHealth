package com.example.zdravlje.data.model.healthcare

data class ScreeningExaminationsData(
    val date: String,
    val time: String,
    val location: String,
    val status: String,
    val anamnesticQuestionnaire: String, //Object  ovo mora biti objekat
    val invitation: String, //Object
    val report: String, //Object
    val action:String
)
