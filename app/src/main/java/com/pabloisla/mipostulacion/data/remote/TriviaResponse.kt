package com.pabloisla.mipostulacion.data.remote

data class TriviaResponse(
    val response_code: Int,
    val results: List<TriviaQuestion>
)

data class TriviaQuestion(
    val category: String,
    val question: String,
    val correct_answer: String,
    val incorrect_answers: List<String>
)