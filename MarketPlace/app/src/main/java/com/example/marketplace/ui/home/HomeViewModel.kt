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
 * Provides data about the most recent driving session
 */
class HomeViewModel : ViewModel() {

    private val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())

    // LiveData for the latest session
    private val _latestSession = MutableLiveData<DrivingSession>().apply {
        value = FakeDrivingData.getMostRecentSession()
    }
    val latestSession: LiveData<DrivingSession> = _latestSession

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

    /**
     * Refreshes the latest session data
     */
    fun refreshLatestSession() {
        val latestSession = FakeDrivingData.getMostRecentSession()
        _latestSession.value = latestSession
        _formattedDate.value = dateFormat.format(latestSession.date)
        _hasAnySessions.value = FakeDrivingData.drivingSessions.isNotEmpty()
    }
}