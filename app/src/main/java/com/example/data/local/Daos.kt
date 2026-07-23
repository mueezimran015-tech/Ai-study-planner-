package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses WHERE userId = :userId ORDER BY title ASC")
    fun observeCourses(userId: String = "user_default"): Flow<List<CourseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCourse(course: CourseEntity)

    @Query("DELETE FROM courses WHERE courseId = :courseId")
    suspend fun deleteCourseById(courseId: String)
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE userId = :userId ORDER BY dueTimestamp ASC")
    fun observeTasks(userId: String = "user_default"): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE dueTimestamp >= :startOfDay AND dueTimestamp <= :endOfDay ORDER BY dueTimestamp ASC")
    fun observeTodayTasks(startOfDay: Long, endOfDay: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE courseId = :courseId ORDER BY dueTimestamp ASC")
    fun observeTasksByCourse(courseId: String): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTask(task: TaskEntity)

    @Query("UPDATE tasks SET status = :status, isSynced = 0 WHERE taskId = :taskId")
    suspend fun updateTaskStatus(taskId: String, status: String)

    @Query("DELETE FROM tasks WHERE taskId = :taskId")
    suspend fun deleteTaskById(taskId: String)
}

@Dao
interface StudySessionDao {
    @Query("SELECT * FROM study_sessions WHERE userId = :userId ORDER BY timestamp DESC")
    fun observeSessions(userId: String = "user_default"): Flow<List<StudySessionEntity>>

    @Query("SELECT SUM(durationSeconds) FROM study_sessions WHERE timestamp >= :startOfDay AND timestamp <= :endOfDay")
    fun observeTodayStudySeconds(startOfDay: Long, endOfDay: Long): Flow<Long?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: StudySessionEntity)

    @Query("DELETE FROM study_sessions WHERE sessionId = :sessionId")
    suspend fun deleteSessionById(sessionId: String)
}
