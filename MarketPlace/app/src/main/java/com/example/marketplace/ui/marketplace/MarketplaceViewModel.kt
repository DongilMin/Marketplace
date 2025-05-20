package com.example.marketplace.ui.marketplace

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.marketplace.R
import com.example.marketplace.data.DrivingSession
import com.example.marketplace.data.FakeDrivingData
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 마켓플레이스 화면 ViewModel
 * - 평균 점수 및 합격률 계산
 * - 차트 데이터 생성 및 설정
 * - 최근 피드백 및 세션 리스트 제공
 */
class MarketplaceViewModel : ViewModel() {

    private val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())

    // 평균 점수
    private val _averageScore = MutableLiveData<Int>().apply {
        value = FakeDrivingData.getAverageOverallScore()
    }
    val averageScore: LiveData<Int> = _averageScore

    // 합격률
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

    // 최근 피드백 (가장 최근 세션)
    private val _latestFeedback = MutableLiveData<DrivingSession>().apply {
        value = FakeDrivingData.getMostRecentSession()
    }
    val latestFeedback: LiveData<DrivingSession> = _latestFeedback

    // 모든 세션 (날짜순 정렬)
    private val _sessions = MutableLiveData<List<DrivingSession>>().apply {
        value = FakeDrivingData.drivingSessions.sortedByDescending { it.date }
    }
    val sessions: LiveData<List<DrivingSession>> = _sessions

    /**
     * 성능 차트용 데이터를 생성합니다.
     * 최근 7개 세션의 점수를 반환합니다.
     *
     * @return 차트 데이터 (날짜, 점수) 쌍의 리스트
     */
    fun getPerformanceChartData(): List<Pair<String, Float>> {
        val recentSessions = _sessions.value?.take(7) ?: emptyList()
        val chartFormat = SimpleDateFormat("MM/dd", Locale.getDefault())

        return recentSessions.reversed().map { session ->
            val dateString = chartFormat.format(session.date)
            Pair(dateString, session.overallScore.toFloat())
        }
    }

    /**
     * 라인 차트를 설정합니다.
     *
     * @param chart 설정할 LineChart 객체
     * @param data 차트에 표시할 데이터
     * @param context 컨텍스트 (색상 리소스 접근용)
     */
    fun setupLineChart(
        chart: LineChart,
        data: List<Pair<String, Float>>,
        context: Context
    ) {
        if (data.isEmpty()) return

        // 데이터 엔트리 생성
        val entries = data.mapIndexed { index, (_, score) ->
            Entry(index.toFloat(), score)
        }

        // 데이터셋 생성 및 스타일링
        val dataSet = LineDataSet(entries, "점수").apply {
            color = ContextCompat.getColor(context, R.color.primary)
            setCircleColor(ContextCompat.getColor(context, R.color.primary))
            lineWidth = 3f
            circleRadius = 6f
            setDrawCircleHole(false)
            valueTextSize = 0f
            setDrawFilled(true)
            fillDrawable = ContextCompat.getDrawable(context, R.drawable.chart_fill_gradient)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawValues(false)
        }

        // 차트 데이터 설정
        chart.data = LineData(dataSet)

        // 차트 스타일링
        chart.apply {
            description = Description().apply { isEnabled = false }
            setTouchEnabled(true)
            isDragEnabled = false
            setScaleEnabled(false)
            setPinchZoom(false)
            legend.isEnabled = false

            // X축 설정
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                setDrawAxisLine(false)
                textColor = ContextCompat.getColor(context, R.color.text_secondary)
                textSize = 12f
                valueFormatter = IndexAxisValueFormatter(data.map { it.first })
                granularity = 1f
            }

            // 왼쪽 Y축 설정
            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = ContextCompat.getColor(context, R.color.divider)
                setDrawAxisLine(false)
                textColor = ContextCompat.getColor(context, R.color.text_secondary)
                textSize = 12f
                axisMinimum = 0f
                axisMaximum = 100f
            }

            // 오른쪽 Y축 숨김
            axisRight.isEnabled = false

            // 차트 새로고침
            invalidate()
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
     * Date 객체를 형식화된 문자열로 변환합니다.
     *
     * @param date 변환할 Date 객체
     * @return 형식화된 날짜 문자열 (yyyy.MM.dd)
     */
    fun formatDate(date: Date): String {
        return dateFormat.format(date)
    }

    /**
     * 데이터를 새로고침합니다.
     */
    fun refreshData() {
        _averageScore.value = FakeDrivingData.getAverageOverallScore()

        val totalSessions = FakeDrivingData.drivingSessions.size
        val passedSessions = FakeDrivingData.getPassedSessions().size
        _passRate.value = if (totalSessions > 0) {
            (passedSessions * 100) / totalSessions
        } else {
            0
        }

        _latestFeedback.value = FakeDrivingData.getMostRecentSession()
        _sessions.value = FakeDrivingData.drivingSessions.sortedByDescending { it.date }
    }
}