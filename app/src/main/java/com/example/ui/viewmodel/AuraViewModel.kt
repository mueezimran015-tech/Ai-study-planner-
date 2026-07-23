package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.CourseEntity
import com.example.data.local.StudySessionEntity
import com.example.data.local.TaskEntity
import com.example.data.repository.StudyPlannerRepositoryImpl
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID

data class AuraUiState(
    val selectedTab: Int = 0,
    val userName: String = "Alex",
    val streakDays: Int = 12,
    val todayStudyMinutes: Int = 140,
    val targetStudyMinutes: Int = 240,
    val todayTasks: List<TaskEntity> = emptyList(),
    val allTasks: List<TaskEntity> = emptyList(),
    val courses: List<CourseEntity> = emptyList(),
    val studySessions: List<StudySessionEntity> = emptyList(),
    val isLoading: Boolean = false,
    
    // Dialog States
    val isAddTaskDialogOpen: Boolean = false,
    val isAddCourseDialogOpen: Boolean = false,
    
    // Focus Timer State
    val isFocusRunning: Boolean = false,
    val focusSecondsLeft: Int = 1500, // 25 Min Default
    val focusTotalSeconds: Int = 1500,
    val focusMode: String = "POMODORO", // POMODORO, STOPWATCH
    val selectedCourseForFocus: CourseEntity? = null,
    val scratchpadNotes: String = ""
)

class AuraViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StudyPlannerRepositoryImpl
    private val _uiState = MutableStateFlow(AuraUiState())
    val uiState: StateFlow<AuraUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        val db = AppDatabase.getDatabase(application)
        repository = StudyPlannerRepositoryImpl(
            courseDao = db.courseDao(),
            taskDao = db.taskDao(),
            studySessionDao = db.studySessionDao()
        )

        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
            loadData()
        }
    }

    private fun loadData() {
        val (startOfDay, endOfDay) = getTodayTimeBounds()

        // Observe Courses
        viewModelScope.launch {
            repository.getCoursesStream().collect { courseList ->
                _uiState.update { it.copy(courses = courseList) }
            }
        }

        // Observe Today's Tasks
        viewModelScope.launch {
            repository.getTodayTasksStream(startOfDay, endOfDay).collect { tasks ->
                _uiState.update { it.copy(todayTasks = tasks) }
            }
        }

        // Observe All Tasks
        viewModelScope.launch {
            repository.getTasksStream().collect { tasks ->
                _uiState.update { it.copy(allTasks = tasks) }
            }
        }

        // Observe Today Study Time
        viewModelScope.launch {
            repository.getTodayStudySecondsStream(startOfDay, endOfDay).collect { seconds ->
                val totalMins = ((seconds ?: 0L) / 60L).toInt()
                _uiState.update { it.copy(todayStudyMinutes = totalMins) }
            }
        }

        // Observe Study Sessions
        viewModelScope.launch {
            repository.getStudySessionsStream().collect { sessions ->
                _uiState.update { it.copy(studySessions = sessions) }
            }
        }
    }

    fun selectTab(tabIndex: Int) {
        _uiState.update { it.copy(selectedTab = tabIndex) }
    }

    fun toggleTaskChecked(taskId: String, isChecked: Boolean) {
        viewModelScope.launch {
            repository.toggleTaskStatus(taskId, isChecked)
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
        }
    }

    fun setAddTaskDialogOpen(isOpen: Boolean) {
        _uiState.update { it.copy(isAddTaskDialogOpen = isOpen) }
    }

    fun setAddCourseDialogOpen(isOpen: Boolean) {
        _uiState.update { it.copy(isAddCourseDialogOpen = isOpen) }
    }

    fun createNewTask(title: String, courseId: String, priority: String, dueInDays: Int = 0) {
        if (title.isBlank()) return
        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, dueInDays)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
        }

        val newTask = TaskEntity(
            taskId = UUID.randomUUID().toString(),
            courseId = courseId,
            title = title,
            priority = priority,
            status = "TODO",
            dueTimestamp = calendar.timeInMillis
        )

        viewModelScope.launch {
            repository.createTask(newTask)
            setAddTaskDialogOpen(false)
        }
    }

    fun createNewCourse(title: String, courseCode: String, creditHours: Int, colorHex: String, examInDays: Int) {
        if (title.isBlank()) return
        val examDate = System.currentTimeMillis() + (examInDays * 86400000L)
        val newCourse = CourseEntity(
            courseId = UUID.randomUUID().toString(),
            title = title,
            courseCode = courseCode,
            creditHours = creditHours,
            colorHex = colorHex,
            examDateEpochMs = examDate
        )

        viewModelScope.launch {
            repository.createCourse(newCourse)
            setAddCourseDialogOpen(false)
        }
    }

    fun deleteCourse(courseId: String) {
        viewModelScope.launch {
            repository.deleteCourse(courseId)
        }
    }

    // --- Focus Timer Controller ---
    fun toggleFocusTimer() {
        val currentlyRunning = _uiState.value.isFocusRunning
        if (currentlyRunning) {
            timerJob?.cancel()
            _uiState.update { it.copy(isFocusRunning = false) }
        } else {
            _uiState.update { it.copy(isFocusRunning = true) }
            timerJob = viewModelScope.launch {
                while (_uiState.value.isFocusRunning && _uiState.value.focusSecondsLeft > 0) {
                    delay(1000L)
                    _uiState.update { it.copy(focusSecondsLeft = it.focusSecondsLeft - 1) }
                }
                if (_uiState.value.focusSecondsLeft == 0) {
                    onFocusSessionComplete()
                }
            }
        }
    }

    fun resetFocusTimer(minutes: Int = 25) {
        timerJob?.cancel()
        val totalSecs = minutes * 60
        _uiState.update {
            it.copy(
                isFocusRunning = false,
                focusSecondsLeft = totalSecs,
                focusTotalSeconds = totalSecs
            )
        }
    }

    fun setScratchpadNotes(notes: String) {
        _uiState.update { it.copy(scratchpadNotes = notes) }
    }

    fun selectCourseForFocus(course: CourseEntity?) {
        _uiState.update { it.copy(selectedCourseForFocus = course) }
    }

    private fun onFocusSessionComplete() {
        _uiState.update { it.copy(isFocusRunning = false) }
        val currentState = _uiState.value
        val completedDuration = (currentState.focusTotalSeconds - currentState.focusSecondsLeft).toLong()
        if (completedDuration < 10) return

        val session = StudySessionEntity(
            sessionId = UUID.randomUUID().toString(),
            courseId = currentState.selectedCourseForFocus?.courseId,
            courseTitle = currentState.selectedCourseForFocus?.title ?: "General Focus",
            durationSeconds = completedDuration,
            sessionType = currentState.focusMode,
            notes = currentState.scratchpadNotes
        )

        viewModelScope.launch {
            repository.logStudySession(session)
        }
    }

    private fun getTodayTimeBounds(): Pair<Long, Long> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = calendar.timeInMillis
        val endOfDay = startOfDay + 86400000L - 1L
        return Pair(startOfDay, endOfDay)
    }
}
