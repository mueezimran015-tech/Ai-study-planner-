package com.example.data.repository

import com.example.data.local.CourseDao
import com.example.data.local.CourseEntity
import com.example.data.local.StudySessionDao
import com.example.data.local.StudySessionEntity
import com.example.data.local.TaskDao
import com.example.data.local.TaskEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.UUID

class StudyPlannerRepositoryImpl(
    private val courseDao: CourseDao,
    private val taskDao: TaskDao,
    private val studySessionDao: StudySessionDao
) {
    fun getCoursesStream(userId: String = "user_default"): Flow<List<CourseEntity>> =
        courseDao.observeCourses(userId)

    fun getTasksStream(userId: String = "user_default"): Flow<List<TaskEntity>> =
        taskDao.observeTasks(userId)

    fun getTodayTasksStream(startOfDay: Long, endOfDay: Long): Flow<List<TaskEntity>> =
        taskDao.observeTodayTasks(startOfDay, endOfDay)

    fun getStudySessionsStream(userId: String = "user_default"): Flow<List<StudySessionEntity>> =
        studySessionDao.observeSessions(userId)

    fun getTodayStudySecondsStream(startOfDay: Long, endOfDay: Long): Flow<Long?> =
        studySessionDao.observeTodayStudySeconds(startOfDay, endOfDay)

    suspend fun createCourse(course: CourseEntity) = withContext(Dispatchers.IO) {
        courseDao.upsertCourse(course)
    }

    suspend fun deleteCourse(courseId: String) = withContext(Dispatchers.IO) {
        courseDao.deleteCourseById(courseId)
    }

    suspend fun createTask(task: TaskEntity) = withContext(Dispatchers.IO) {
        taskDao.upsertTask(task)
    }

    suspend fun toggleTaskStatus(taskId: String, isCompleted: Boolean) = withContext(Dispatchers.IO) {
        val status = if (isCompleted) "COMPLETED" else "TODO"
        taskDao.updateTaskStatus(taskId, status)
    }

    suspend fun deleteTask(taskId: String) = withContext(Dispatchers.IO) {
        taskDao.deleteTaskById(taskId)
    }

    suspend fun logStudySession(session: StudySessionEntity) = withContext(Dispatchers.IO) {
        studySessionDao.insertSession(session)
    }

    suspend fun seedInitialDataIfEmpty() = withContext(Dispatchers.IO) {
        // Pre-populate with realistic study data if empty
        val currentTimestamp = System.currentTimeMillis()
        val c1 = CourseEntity(
            courseId = "c_cs101",
            title = "Data Structures & Algorithms",
            courseCode = "CS-301",
            colorHex = "#6366F1",
            examDateEpochMs = currentTimestamp + (14L * 86400000L),
            creditHours = 4
        )
        val c2 = CourseEntity(
            courseId = "c_math201",
            title = "Linear Algebra & Calculus",
            courseCode = "MATH-201",
            colorHex = "#10B981",
            examDateEpochMs = currentTimestamp + (7L * 86400000L),
            creditHours = 3
        )
        val c3 = CourseEntity(
            courseId = "c_phys102",
            title = "Quantum Physics",
            courseCode = "PHYS-102",
            colorHex = "#F59E0B",
            examDateEpochMs = currentTimestamp + (21L * 86400000L),
            creditHours = 3
        )

        courseDao.upsertCourse(c1)
        courseDao.upsertCourse(c2)
        courseDao.upsertCourse(c3)

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
        }
        val endOfToday = calendar.timeInMillis

        val t1 = TaskEntity(
            taskId = "t_1",
            courseId = "c_cs101",
            title = "Implement AVL Tree balancing logic in Kotlin",
            priority = "HIGH",
            status = "TODO",
            dueTimestamp = endOfToday,
            subtasks = listOf("Rotations", "Height balance check", "Unit tests"),
            scratchpadNotes = "Remember to handle double rotation cases carefully."
        )

        val t2 = TaskEntity(
            taskId = "t_2",
            courseId = "c_math201",
            title = "Solve Eigenvalue problem set 4",
            priority = "CRITICAL",
            status = "TODO",
            dueTimestamp = endOfToday,
            subtasks = listOf("Matrix diagonalization", "Characteristic polynomial"),
            scratchpadNotes = "Formula: det(A - lambda*I) = 0"
        )

        val t3 = TaskEntity(
            taskId = "t_3",
            courseId = "c_phys102",
            title = "Read Chapter 5: Schrödinger Equation",
            priority = "MEDIUM",
            status = "COMPLETED",
            dueTimestamp = endOfToday - 86400000L,
            subtasks = listOf("Wavefunctions", "Infinite potential well")
        )

        taskDao.upsertTask(t1)
        taskDao.upsertTask(t2)
        taskDao.upsertTask(t3)

        // Seed sample study sessions
        val s1 = StudySessionEntity(
            sessionId = UUID.randomUUID().toString(),
            courseId = "c_cs101",
            courseTitle = "Data Structures & Algorithms",
            durationSeconds = 3000L, // 50 mins
            sessionType = "POMODORO",
            timestamp = currentTimestamp - 3600000L,
            notes = "Completed binary tree traversal chapter."
        )
        val s2 = StudySessionEntity(
            sessionId = UUID.randomUUID().toString(),
            courseId = "c_math201",
            courseTitle = "Linear Algebra & Calculus",
            durationSeconds = 1800L, // 30 mins
            sessionType = "POMODORO",
            timestamp = currentTimestamp - (86400000L),
            notes = "Solved 3 matrix transformation questions."
        )
        studySessionDao.insertSession(s1)
        studySessionDao.insertSession(s2)
    }
}
