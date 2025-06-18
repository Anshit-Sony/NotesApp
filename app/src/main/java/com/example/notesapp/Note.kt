package com.example.notesapp

data class Note(
    var id: String = "",
    var title: String = "",
    var content: String = "",
    var timestamp: Long = 0
)
