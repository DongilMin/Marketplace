package com.example.marketplace.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.marketplace.data.DrivingSession
import com.example.marketplace.data.FakeDrivingData

/**
 * ViewModel for the Session Details screen
 * Provides data for displaying detailed information about a specific driving session
 */
class SessionDetailsViewModel : ViewModel() {

    // LiveData for the current session being displayed
    private val _session = MutableLiveData<DrivingSession?>()
    val session: LiveData<DrivingSession?> = _session

    // LiveData for whether session data exists
    private val _hasSessionData = MutableLiveData<Boolean>()
    val hasSessionData: LiveData<Boolean> = _hasSessionData

    // LiveData for total points deducted
    private val _totalPointsDeducted = MutableLiveData<Int>()
    val totalPointsDeducted: LiveData<Int> = _totalPointsDeducted

    /**
     * Loads a driving session by ID
     *
     * @param sessionId The ID of the session to load
     */
    fun loadSession(sessionId: String) {
        val sessionData = FakeDrivingData.getSessionById(sessionId)
        _session.value = sessionData
        _hasSessionData.value = sessionData != null

        // Calculate total points deducted if session exists
        sessionData?.let { session ->
            val totalDeducted = session.deductionReasons.sumOf { it.pointsDeducted }
            _totalPointsDeducted.value = totalDeducted
        }
    }
}