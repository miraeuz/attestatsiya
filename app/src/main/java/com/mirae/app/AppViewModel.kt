package com.mirae.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mirae.app.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AppState(
    val loading: Boolean = true,
    val catalog: Catalog? = null,
    val stats: UserStats = UserStats(),
    val error: String? = null,
    val quizQuestions: List<QuizQuestion> = emptyList(),
    val quizIndex: Int = 0,
    val quizActive: Boolean = false,
    val quizResult: QuizResult? = null,
    val answeredCurrent: Boolean = false
) {
    val currentQuizQuestion: QuizQuestion?
        get() = quizQuestions.getOrNull(quizIndex)
}

class AppViewModel(app: Application) : AndroidViewModel(app) {
    private val repository = MiraeRepository(app)
    private val statsStore = StatsStore(app)

    private val _state = MutableStateFlow(AppState(stats = statsStore.load()))
    val state: StateFlow<AppState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            runCatching { repository.loadCatalog() }
                .onSuccess { catalog ->
                    _state.value = _state.value.copy(loading = false, catalog = catalog)
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(
                        loading = false,
                        error = e.message ?: "Maʼlumotlar bazasini ochib bo‘lmadi."
                    )
                }
        }
    }

    fun startQuiz(subjectId: String? = null, topicId: String? = null, difficulty: String? = null, count: Int = 10) {
        val catalog = _state.value.catalog ?: return
        val pool = catalog.questions.filter { q ->
            (subjectId == null || q.subjectId == subjectId) &&
            (topicId == null || q.topicId == topicId) &&
            (difficulty == null || q.difficulty.equals(difficulty, true))
        }
        val selected = pool.shuffled().take(count.coerceIn(1, 50)).map { QuizQuestion(it) }
        _state.value = _state.value.copy(
            quizQuestions = selected,
            quizIndex = 0,
            quizActive = selected.isNotEmpty(),
            quizResult = null,
            answeredCurrent = false
        )
    }

    fun answer(position: Int) {
        val s = _state.value
        val current = s.currentQuizQuestion ?: return
        if (current.answered) return
        val updated = current.copy(
            selectedPosition = position,
            answered = true
        )
        val list = s.quizQuestions.toMutableList()
        list[s.quizIndex] = updated
        _state.value = s.copy(quizQuestions = list, answeredCurrent = true)
    }

    fun nextQuestion() {
        val s = _state.value
        if (!s.quizActive) return
        if (s.quizIndex >= s.quizQuestions.lastIndex) {
            val correct = s.quizQuestions.count { it.selectedPosition != null &&
                it.question.options.firstOrNull { option -> option.position == it.selectedPosition }?.isCorrect == true }
            val xp = s.quizQuestions.sumOf { qq ->
                if (qq.selectedPosition != null &&
                    qq.question.options.firstOrNull { option -> option.position == qq.selectedPosition }?.isCorrect == true
                ) qq.question.xp else 0
            }
            val result = QuizResult(s.quizQuestions.size, correct, xp, s.quizQuestions)
            val stats = statsStore.saveQuiz(result)
            _state.value = s.copy(
                quizActive = false,
                quizResult = result,
                stats = stats
            )
        } else {
            _state.value = s.copy(
                quizIndex = s.quizIndex + 1,
                answeredCurrent = false
            )
        }
    }

    fun clearResult() {
        _state.value = _state.value.copy(quizResult = null)
    }
}
