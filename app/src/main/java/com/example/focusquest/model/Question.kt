package com.example.focusquest.model

data class Question(
    val id: Int,
    val grade: Grade,
    val subject: Subject,
    val question: String,
    val answers: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String,
    val rewardTimeMillis: Long = 30_000L
)
