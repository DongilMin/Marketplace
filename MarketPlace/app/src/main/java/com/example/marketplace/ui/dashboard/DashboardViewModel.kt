package com.example.marketplace.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.marketplace.data.DrivingSession
import com.example.marketplace.data.FakeDrivingData
import com.example.marketplace.data.ScoreCategory

/**
 * ViewModel for the Dashboard screen
 * Provides data for displaying driving performance metrics and session list
 */
class DashboardViewModel : ViewModel() {

    // LiveData for overall average score
    private val _averageScore = MutableLiveData<Int>().apply {
        value = FakeDrivingData.getAverageOverallScore()
    }
    val averageScore: LiveData<Int> = _averageScore

    // LiveData for speed score
    private val _speedScore = MutableLiveData<Int>().apply {
        value = FakeDrivingData.getAverageScoreByCategory(ScoreCategory.SPEED)
    }
    val speedScore: LiveData<Int> = _speedScore

    // LiveData for turning score
    private val _turningScore = MutableLiveData<Int>().apply {
        value = FakeDrivingData.getAverageScoreByCategory(ScoreCategory.TURNING)
    }
    val turningScore: LiveData<Int> = _turningScore

    // LiveData for braking score
    private val _brakingScore = MutableLiveData<Int>().apply {
        value = FakeDrivingData.getAverageScoreByCategory(ScoreCategory.BRAKING)
    }
    val brakingScore: LiveData<Int> = _brakingScore

    // LiveData for all driving sessions
    private val _allSessions = MutableLiveData<List<DrivingSession>>().apply {
        value = FakeDrivingData.drivingSessions.sortedByDescending { it.date }
    }
    val allSessions: LiveData<List<DrivingSession>> = _allSessions

    // LiveData for whether there are any sessions
    private val _hasSessions = MutableLiveData<Boolean>().apply {
        value = FakeDrivingData.drivingSessions.isNotEmpty()
    }
    val hasSessions: LiveData<Boolean> = _hasSessions

    /**
     * Refreshes all data from the data source
     */
    fun refreshData() {
        _averageScore.value = FakeDrivingData.getAverageOverallScore()
        _speedScore.value = FakeDrivingData.getAverageScoreByCategory(ScoreCategory.SPEED)
        _turningScore.value = FakeDrivingData.getAverageScoreByCategory(ScoreCategory.TURNING)
        _brakingScore.value = FakeDrivingData.getAverageScoreByCategory(ScoreCategory.BRAKING)
        _allSessions.value = FakeDrivingData.drivingSessions.sortedByDescending { it.date }
        _hasSessions.value = FakeDrivingData.drivingSessions.isNotEmpty()
    }
}