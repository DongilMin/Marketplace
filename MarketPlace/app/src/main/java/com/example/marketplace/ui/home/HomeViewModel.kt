package com.example.marketplace.ui.home

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.marketplace.R
import com.example.marketplace.data.FakeDrivingData

/**
 * 홈 화면 ViewModel
 * - 사용자의 전체 진행 상황 데이터를 제공
 * - 총 연습 횟수, 평균 점수, 합격률 계산
 * - 점수에 따른 색상 결정 로직 포함
 */
class HomeViewModel : ViewModel() {

    // 총 세션 수
    private val _totalSessions = MutableLiveData<Int>().apply {
        value = FakeDrivingData.drivingSessions.size
    }
    val totalSessions: LiveData<Int> = _totalSessions

    // 평균 점수
    private val _averageScore = MutableLiveData<Int>().apply {
        value = FakeDrivingData.getAverageOverallScore()
    }
    val averageScore: LiveData<Int> = _averageScore

    // 합격률 (70점 이상인 세션의 비율)
    private val _passRate = MutableLiveData<Int>().apply {
        val totalSessions = FakeDrivingData.drivingSessions.size
        val passedSessions = FakeDrivingData.getPassedSessions().size
        value = if (totalSessions > 0) {
            (passedSessions * 100) / totalSessions
        } else {
            0
        }
    }
    val passRate: LiveData<Int> = _passRate

    /**
     * 점수에 따른 색상을 반환합니다.
     *
     * @param context 컨텍스트 (색상 리소스 접근용)
     * @param score 점수 (0-100)
     * @return 점수에 맞는 색상
     */
    fun getScoreColor(context: Context, score: Int): Int {
        return when {
            score >= 90 -> ContextCompat.getColor(context, R.color.score_excellent)
            score >= 80 -> ContextCompat.getColor(context, R.color.score_good)
            score >= 70 -> ContextCompat.getColor(context, R.color.score_average)
            score >= 60 -> ContextCompat.getColor(context, R.color.score_needs_work)
            else -> ContextCompat.getColor(context, R.color.score_poor)
        }
    }

    /**
     * 합격률에 따른 색상을 반환합니다.
     *
     * @param context 컨텍스트 (색상 리소스 접근용)
     * @param passRate 합격률 (0-100%)
     * @return 합격률에 맞는 색상
     */
    fun getPassRateColor(context: Context, passRate: Int): Int {
        return when {
            passRate >= 80 -> ContextCompat.getColor(context, R.color.score_excellent)
            passRate >= 60 -> ContextCompat.getColor(context, R.color.score_good)
            passRate >= 40 -> ContextCompat.getColor(context, R.color.score_average)
            passRate >= 20 -> ContextCompat.getColor(context, R.color.score_needs_work)
            else -> ContextCompat.getColor(context, R.color.score_poor)
        }
    }

    /**
     * 데이터를 새로고침합니다.
     */
    fun refreshData() {
        _totalSessions.value = FakeDrivingData.drivingSessions.size
        _averageScore.value = FakeDrivingData.getAverageOverallScore()

        val totalSessions = FakeDrivingData.drivingSessions.size
        val passedSessions = FakeDrivingData.getPassedSessions().size
        _passRate.value = if (totalSessions > 0) {
            (passedSessions * 100) / totalSessions
        } else {
            0
        }
    }
}