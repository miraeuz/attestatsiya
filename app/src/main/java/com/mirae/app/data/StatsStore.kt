package com.mirae.app.data

import android.content.Context

data class UserStats(
    val answered: Int = 0,
    val correct: Int = 0,
    val xp: Int = 0,
    val quizzes: Int = 0,
    val bestScore: Int = 0
) {
    val accuracy: Int get() = if (answered == 0) 0 else (correct * 100 / answered)
}

class StatsStore(context: Context) {
    private val prefs = context.getSharedPreferences("mirae_stats", Context.MODE_PRIVATE)

    fun load(): UserStats = UserStats(
        answered = prefs.getInt("answered", 0),
        correct = prefs.getInt("correct", 0),
        xp = prefs.getInt("xp", 0),
        quizzes = prefs.getInt("quizzes", 0),
        bestScore = prefs.getInt("bestScore", 0)
    )

    fun saveQuiz(result: QuizResult): UserStats {
        val old = load()
        val next = old.copy(
            answered = old.answered + result.total,
            correct = old.correct + result.correct,
            xp = old.xp + result.xp,
            quizzes = old.quizzes + 1,
            bestScore = maxOf(old.bestScore, result.percentage)
        )
        prefs.edit()
            .putInt("answered", next.answered)
            .putInt("correct", next.correct)
            .putInt("xp", next.xp)
            .putInt("quizzes", next.quizzes)
            .putInt("bestScore", next.bestScore)
            .apply()
        return next
    }
}
