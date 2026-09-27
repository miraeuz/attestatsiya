package com.mirae.app.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class MiraeRepository(private val context: Context) {

    suspend fun loadCatalog(): Catalog = withContext(Dispatchers.IO) {
        val db = openBundledDatabase()
        try {
            val subjects = mutableListOf<Subject>()
            db.rawQuery(
                "SELECT id, name, slug, is_shared FROM subjects ORDER BY CASE id WHEN 'informatika' THEN 1 WHEN 'pedagogika' THEN 2 ELSE 3 END",
                null
            ).use { c ->
                while (c.moveToNext()) {
                    subjects += Subject(
                        id = c.getString(0),
                        name = c.getString(1),
                        slug = c.getString(2),
                        isShared = c.getInt(3) == 1
                    )
                }
            }

            val topics = mutableListOf<Topic>()
            db.rawQuery(
                """
                SELECT t.id, t.subject_id, t.name, t.official_slots, COUNT(q.id)
                FROM topics t
                LEFT JOIN questions q ON q.topic_id = t.id AND q.active = 1
                GROUP BY t.id
                ORDER BY t.subject_id, t.name
                """.trimIndent(),
                null
            ).use { c ->
                while (c.moveToNext()) {
                    topics += Topic(
                        id = c.getString(0),
                        subjectId = c.getString(1),
                        name = c.getString(2),
                        officialSlots = c.getString(3),
                        questionCount = c.getInt(4)
                    )
                }
            }

            val questions = mutableListOf<Question>()
            db.rawQuery(
                """
                SELECT id, subject_id, topic_id, COALESCE(text,''), xp,
                       difficulty, origin, verification, correct_json
                FROM questions
                WHERE active = 1
                ORDER BY created_at, id
                """.trimIndent(),
                null
            ).use { c ->
                while (c.moveToNext()) {
                    val id = c.getString(0)
                    val correctJson = c.getString(8)
                    val correctPosition = Regex("""\d+""").find(correctJson)?.value?.toIntOrNull()

                    val options = mutableListOf<Option>()
                    db.rawQuery(
                        "SELECT position, COALESCE(text,''), is_correct FROM options WHERE question_id = ? ORDER BY position",
                        arrayOf(id)
                    ).use { o ->
                        while (o.moveToNext()) {
                            options += Option(
                                position = o.getInt(0),
                                text = o.getString(1),
                                isCorrect = o.getInt(2) == 1 || o.getInt(0) == correctPosition
                            )
                        }
                    }

                    val solution = db.rawQuery(
                        "SELECT text FROM solutions WHERE question_id = ? ORDER BY position LIMIT 1",
                        arrayOf(id)
                    ).use { s -> if (s.moveToFirst()) s.getString(0) else null }

                    questions += Question(
                        id = id,
                        subjectId = c.getString(1),
                        topicId = c.getString(2),
                        text = c.getString(3),
                        xp = c.getInt(4),
                        difficulty = c.getString(5),
                        origin = c.getString(6),
                        verification = c.getString(7),
                        options = options,
                        solution = solution
                    )
                }
            }

            Catalog(subjects, topics, questions)
        } finally {
            db.close()
        }
    }

    private fun openBundledDatabase(): SQLiteDatabase {
        val dbFile = File(context.filesDir, "mirae_questions.sqlite3")
        if (!dbFile.exists() || dbFile.length() < 1000L) {
            context.assets.open("mirae_questions.sqlite3").use { input ->
                dbFile.outputStream().use { output -> input.copyTo(output) }
            }
        }
        return SQLiteDatabase.openDatabase(
            dbFile.absolutePath,
            null,
            SQLiteDatabase.OPEN_READONLY
        )
    }
}
