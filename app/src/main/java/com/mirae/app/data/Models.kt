package com.mirae.app.data

data class Subject(
    val id: String,
    val name: String,
    val slug: String?,
    val isShared: Boolean
)

data class Topic(
    val id: String,
    val subjectId: String,
    val name: String,
    val officialSlots: String?,
    val questionCount: Int
)

data class Option(
    val position: Int,
    val text: String,
    val isCorrect: Boolean
)

data class Question(
    val id: String,
    val subjectId: String,
    val topicId: String,
    val text: String,
    val xp: Int,
    val difficulty: String?,
    val origin: String,
    val verification: String,
    val options: List<Option>,
    val solution: String?
)

data class Catalog(
    val subjects: List<Subject>,
    val topics: List<Topic>,
    val questions: List<Question>
)

data class QuizQuestion(
    val question: Question,
    val selectedPosition: Int? = null,
    val answered: Boolean = false
)

data class QuizResult(
    val total: Int,
    val correct: Int,
    val xp: Int,
    val questions: List<QuizQuestion>
) {
    val percentage: Int get() = if (total == 0) 0 else (correct * 100 / total)
}
