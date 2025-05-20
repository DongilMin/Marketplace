package com.example.marketplace.ui.statistics

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.marketplace.R
import com.example.marketplace.data.DeductionReason
import com.example.marketplace.data.FakeDrivingData
import com.example.marketplace.data.ScoreCategory
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

/**
 * 내 통계 화면 ViewModel
 * - 전체 통계 데이터 계산 및 제공
 * - 차트 데이터 생성 및 설정
 * - TOP 감점 항목 분석
 * - 성과 트렌드 분석
 */
class StatisticsViewModel : ViewModel() {

    // 총 세션 수
    private val _totalSessions = MutableLiveData<Int>().apply {
        value = FakeDrivingData.drivingSessions.size
    }
    val totalSessions: LiveData<Int> = _totalSessions

    // 전체 평균 점수
    private val _overallAverage = MutableLiveData<Int>().apply {
        value = FakeDrivingData.getAverageOverallScore()
    }
    val overallAverage: LiveData<Int> = _overallAverage

    // 합격률
    private val _passRatePercentage = MutableLiveData<Int>().apply {
        val total = FakeDrivingData.drivingSessions.size
        val passed = FakeDrivingData.getPassedSessions().size
        value = if (total > 0) (passed * 100) / total else 0
    }
    val passRatePercentage: LiveData<Int> = _passRatePercentage

    // 개별 항목 평균 점수
    private val _speedAverage = MutableLiveData<Int>().apply {
        value = FakeDrivingData.getAverageScoreByCategory(ScoreCategory.SPEED)
    }
    val speedAverage: LiveData<Int> = _speedAverage

    private val _turningAverage = MutableLiveData<Int>().apply {
        value = FakeDrivingData.getAverageScoreByCategory(ScoreCategory.TURNING)
    }
    val turningAverage: LiveData<Int> = _turningAverage

    private val _brakingAverage = MutableLiveData<Int>().apply {
        value = FakeDrivingData.getAverageScoreByCategory(ScoreCategory.BRAKING)
    }
    val brakingAverage: LiveData<Int> = _brakingAverage

    // TOP 감점 항목
    private val _topDeductions = MutableLiveData<List<TopDeductionItem>>().apply {
        value = calculateTopDeductions()
    }
    val topDeductions: LiveData<List<TopDeductionItem>> = _topDeductions

    // 트렌드 정보
    private val _trendDescription = MutableLiveData<String>().apply {
        value = calculateTrendDescription()
    }
    val trendDescription: LiveData<String> = _trendDescription

    private val _isImproveTrend = MutableLiveData<Boolean>().apply {
        value = calculateIsImprovingTrend()
    }
    val isImproveTrend: LiveData<Boolean> = _isImproveTrend

    private val _latestScore = MutableLiveData<Int>().apply {
        value = FakeDrivingData.getMostRecentSession().overallScore
    }
    val latestScore: LiveData<Int> = _latestScore

    /**
     * 항목별 점수 데이터를 반환합니다.
     *
     * @return 항목명과 점수의 쌍 리스트
     */
    fun getCategoryScoresData(): List<Pair<String, Float>> {
        return listOf(
            Pair("속도", _speedAverage.value?.toFloat() ?: 0f),
            Pair("회전", _turningAverage.value?.toFloat() ?: 0f),
            Pair("제동", _brakingAverage.value?.toFloat() ?: 0f)
        )
    }

    /**
     * 막대 차트를 설정합니다.
     *
     * @param chart 설정할 BarChart 객체
     * @param data 차트에 표시할 데이터
     * @param context 컨텍스트 (색상 리소스 접근용)
     */
    fun setupBarChart(
        chart: BarChart,
        data: List<Pair<String, Float>>,
        context: Context
    ) {
        if (data.isEmpty()) return

        val entries = data.mapIndexed { index, (_, score) ->
            BarEntry(index.toFloat(), score)
        }

        val dataSet = BarDataSet(entries, "항목별 점수").apply {
            colors = listOf(
                ContextCompat.getColor(context, R.color.primary),
                ContextCompat.getColor(context, R.color.secondary),
                ContextCompat.getColor(context, R.color.score_excellent)
            )
            valueTextSize = 12f
            valueTextColor = ContextCompat.getColor(context, R.color.text_primary)
        }

        chart.apply {
            this.data = BarData(dataSet)
            description = Description().apply { isEnabled = false }
            legend.isEnabled = false
            setTouchEnabled(false)

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

            invalidate()
        }
    }

    /**
     * 트렌드 데이터를 반환합니다.
     *
     * @return 최근 세션들의 점수 트렌드 데이터
     */
    fun getTrendData(): List<Pair<String, Float>> {
        val recentSessions = FakeDrivingData.drivingSessions
            .sortedByDescending { it.date }
            .take(5)
            .reversed()

        return recentSessions.mapIndexed { index, session ->
            Pair((index + 1).toString(), session.overallScore.toFloat())
        }
    }

    /**
     * 트렌드 라인 차트를 설정합니다.
     *
     * @param chart 설정할 LineChart 객체
     * @param data 차트에 표시할 데이터
     * @param context 컨텍스트 (색상 리소스 접근용)
     */
    fun setupTrendChart(
        chart: LineChart,
        data: List<Pair<String, Float>>,
        context: Context
    ) {
        if (data.isEmpty()) return

        val entries = data.mapIndexed { index, (_, score) ->
            Entry(index.toFloat(), score)
        }

        val dataSet = LineDataSet(entries, "점수 트렌드").apply {
            color = ContextCompat.getColor(context, R.color.primary)
            setCircleColor(ContextCompat.getColor(context, R.color.primary))
            lineWidth = 3f
            circleRadius = 5f
            setDrawCircleHole(false)
            valueTextSize = 0f
            setDrawFilled(true)
            fillDrawable = ContextCompat.getDrawable(context, R.drawable.chart_fill_gradient)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawValues(false)
        }

        chart.apply {
            this.data = LineData(dataSet)
            description = Description().apply { isEnabled = false }
            legend.isEnabled = false
            setTouchEnabled(false)

            // X축 설정
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                setDrawAxisLine(false)
                textColor = ContextCompat.getColor(context, R.color.text_secondary)
                textSize = 12f
                valueFormatter = IndexAxisValueFormatter(data.map { "세션 ${it.first}" })
                granularity = 1f
            }

            // 왼쪽 Y축 설정
            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = ContextCompat.getColor(context, R.color.divider)
                setDrawAxisLine(false)
                textColor = ContextCompat.getColor(context, R.color.text_secondary)
                textSize = 12f
                axisMinimum = 60f
                axisMaximum = 100f
            }

            // 오른쪽 Y축 숨김
            axisRight.isEnabled = false

            invalidate()
        }
    }

    /**
     * TOP 감점 항목을 계산합니다.
     *
     * @return TOP 3 감점 항목 리스트
     */
    private fun calculateTopDeductions(): List<TopDeductionItem> {
        val deductionMap = mutableMapOf<String, Pair<Int, Int>>() // reason -> (count, totalPoints)

        FakeDrivingData.drivingSessions.forEach { session ->
            session.deductionReasons.forEach { deduction ->
                val key = deduction.reason
                val current = deductionMap[key] ?: Pair(0, 0)
                deductionMap[key] = Pair(
                    current.first + 1,
                    current.second + deduction.pointsDeducted
                )
            }
        }

        return deductionMap.entries
            .sortedByDescending { it.value.second }
            .take(3)
            .mapIndexed { index, entry ->
                TopDeductionItem(
                    rank = index + 1,
                    reason = entry.key,
                    occurrenceCount = entry.value.first,
                    totalPointsDeducted = entry.value.second
                )
            }
    }

    /**
     * 트렌드 설명을 계산합니다.
     *
     * @return 트렌드 설명 문자열
     */
    private fun calculateTrendDescription(): String {
        val recentSessions = FakeDrivingData.drivingSessions
            .sortedByDescending { it.date }
            .take(3)

        if (recentSessions.size < 3) return "데이터 부족"

        val recentAverage = recentSessions.map { it.overallScore }.average()
        val previousAverage = FakeDrivingData.drivingSessions
            .sortedByDescending { it.date }
            .drop(3)
            .take(3)
            .map { it.overallScore }
            .average()

        val difference = recentAverage - previousAverage
        return when {
            difference > 2 -> "최근 3회 평균 ${difference.toInt()}점 상승"
            difference < -2 -> "최근 3회 평균 ${(-difference).toInt()}점 하락"
            else -> "최근 점수 변화 없음"
        }
    }

    /**
     * 개선 트렌드 여부를 계산합니다.
     *
     * @return 개선 중이면 true, 아니면 false
     */
    private fun calculateIsImprovingTrend(): Boolean {
        val recentSessions = FakeDrivingData.drivingSessions
            .sortedByDescending { it.date }
            .take(3)

        if (recentSessions.size < 3) return true

        val recentAverage = recentSessions.map { it.overallScore }.average()
        val previousAverage = FakeDrivingData.drivingSessions
            .sortedByDescending { it.date }
            .drop(3)
            .take(3)
            .map { it.overallScore }
            .average()

        return recentAverage >= previousAverage
    }

    /**
     * 합격률에 따른 색상을 반환합니다.
     *
     * @param context 컨텍스트
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
        _overallAverage.value = FakeDrivingData.getAverageOverallScore()

        val total = FakeDrivingData.drivingSessions.size
        val passed = FakeDrivingData.getPassedSessions().size
        _passRatePercentage.value = if (total > 0) (passed * 100) / total else 0

        _speedAverage.value = FakeDrivingData.getAverageScoreByCategory(ScoreCategory.SPEED)
        _turningAverage.value = FakeDrivingData.getAverageScoreByCategory(ScoreCategory.TURNING)
        _brakingAverage.value = FakeDrivingData.getAverageScoreByCategory(ScoreCategory.BRAKING)

        _topDeductions.value = calculateTopDeductions()
        _trendDescription.value = calculateTrendDescription()
        _isImproveTrend.value = calculateIsImprovingTrend()
        _latestScore.value = FakeDrivingData.getMostRecentSession().overallScore
    }
}

/**
 * TOP 감점 항목 데이터 클래스
 *
 * @property rank 순위 (1-3)
 * @property reason 감점 사유
 * @property occurrenceCount 발생 횟수
 * @property totalPointsDeducted 총 감점
 */
data class TopDeductionItem(
    val rank: Int,
    val reason: String,
    val occurrenceCount: Int,
    val totalPointsDeducted: Int
)