package com.example.marketplace.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.marketplace.data.DrivingSession
import com.example.marketplace.data.FakeDrivingData
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * ViewModel for the Home screen
 * Provides data about the most recent driving session and all sessions
 */
class HomeViewModel : ViewModel() {

    private val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())

    // LiveData for the latest session
    private val _latestSession = MutableLiveData<DrivingSession>().apply {
        value = FakeDrivingData.getMostRecentSession()
    }
    val latestSession: LiveData<DrivingSession> = _latestSession

    // LiveData for all sessions (sorted by date, most recent first)
    private val _allSessions = MutableLiveData<List<DrivingSession>>().apply {
        value = FakeDrivingData.drivingSessions.sortedByDescending { it.date }
    }
    val allSessions: LiveData<List<DrivingSession>> = _allSessions

    // LiveData for formatted date
    private val _formattedDate = MutableLiveData<String>().apply {
        value = dateFormat.format(FakeDrivingData.getMostRecentSession().date)
    }
    val formattedDate: LiveData<String> = _formattedDate

    // LiveData for whether any sessions exist
    private val _hasAnySessions = MutableLiveData<Boolean>().apply {
        value = FakeDrivingData.drivingSessions.isNotEmpty()
    }
    val hasAnySessions: LiveData<Boolean> = _hasAnySessions

    // LiveData for average score
    private val _averageScore = MutableLiveData<Int>().apply {
        value = FakeDrivingData.getAverageOverallScore()
    }
    val averageScore: LiveData<Int> = _averageScore

    /**
     * Refreshes all session data
     */
    fun refreshLatestSession() {
        val latestSession = FakeDrivingData.getMostRecentSession()
        _latestSession.value = latestSession
        _formattedDate.value = dateFormat.format(latestSession.date)
        _hasAnySessions.value = FakeDrivingData.drivingSessions.isNotEmpty()
        _allSessions.value = FakeDrivingData.drivingSessions.sortedByDescending { it.date }
        _averageScore.value = FakeDrivingData.getAverageOverallScore()
    }

    /**
     * Gets the recent sessions (last 4 sessions)
     */
    fun getRecentSessions(): List<DrivingSession> {
        return _allSessions.value?.take(4) ?: emptyList()
    }

    /**
     * Gets sessions for chart display (last 7 sessions)
     */
    fun getChartData(): List<Pair<String, Float>> {
        val sessions = _allSessions.value?.take(7) ?: emptyList()
        return sessions.reversed().map { session ->
            val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
            Pair(dayFormat.format(session.date), session.overallScore.toFloat())
        }
    }
}