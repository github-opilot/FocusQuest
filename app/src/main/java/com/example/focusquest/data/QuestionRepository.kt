package com.example.focusquest.data

import com.example.focusquest.data.questions.fifthGradeQuestions
import com.example.focusquest.data.questions.fourthGradeQuestions
import com.example.focusquest.data.questions.thirdGradeQuestions
import com.example.focusquest.model.Grade
import com.example.focusquest.model.Question
import com.example.focusquest.model.Subject

object QuestionRepository {
    internal val allQuestions: List<Question> =
        fifthGradeQuestions + fourthGradeQuestions + thirdGradeQuestions

    fun getQuestions(grade: Grade, subject: Subject): List<Question> {
        return allQuestions
            .filter { question ->
                question.grade == grade && question.subject == subject
            }
            .shuffled()
    }
}
