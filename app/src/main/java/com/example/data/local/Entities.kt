package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey val courseId: String,
    val userId: String = "user_default",
    val title: String,
    val courseCode: String = "",
    val colorHex: String = "#6366F1",
    val examDateEpochMs: Long = 0L,
    val creditHours: Int = 3,
    val isSynced: Boolean = false
)

@Entity(tableName = "tasks")
@TypeConverters(StringListConverter::class)
data class TaskEntity(
    @PrimaryKey val taskId: String,
    val courseId: String = "",
    val userId: String = "user_default",
    val title: String,
    val priority: String = "MEDIUM", // LOW, MEDIUM, HIGH, CRITICAL
    val status: String = "TODO",    // TODO, COMPLETED
    val dueTimestamp: Long = System.currentTimeMillis(),
    val subtasks: List<String> = emptyList(),
    val resourceUrls: List<String> = emptyList(),
    val scratchpadNotes: String = "",
    val isSynced: Boolean = false
)

@Entity(tableName = "study_sessions")
data class StudySessionEntity(
    @PrimaryKey val sessionId: String,
    val courseId: String? = null,
    val courseTitle: String? = null,
    val userId: String = "user_default",
    val durationSeconds: Long,
    val sessionType: String = "POMODORO", // POMODORO, STOPWATCH
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = "",
    val isSynced: Boolean = false
)

class StringListConverter {
    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        if (list == null || list.isEmpty()) return ""
        return list.joinToString("|||")
    }

    @TypeConverter
    fun toStringList(data: String?): List<String> {
        if (data.isNullOrBlank()) return emptyList()
        return data.split("|||")
    }
}
