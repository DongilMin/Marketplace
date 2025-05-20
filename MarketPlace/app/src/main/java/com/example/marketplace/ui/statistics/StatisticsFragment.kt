package com.example.marketplace.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marketplace.databinding.FragmentStatisticsBinding
import com.example.marketplace.ui.statistics.adapter.TopDeductionAdapter
import com.example.marketplace.util.ScoreUtil

/**
 * 내 통계 화면 프래그먼트
 * - 전체 현황 요약 (총 연습 횟수, 평균 점수, 합격률)
 * - 항목별 평균 점수 막대 차트
 * - 감점 항목 TOP 3 리스트
 * - 최근 성과 트렌드 라인 차트
 */
class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private lateinit var statisticsViewModel: StatisticsViewModel
    private lateinit var topDeductionAdapter: TopDeductionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // ViewModel 초기화
        statisticsViewModel = ViewModelProvider(this)[StatisticsViewModel::class.java]

        // ViewBinding 초기화
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)

        // UI 설정
        setupRecyclerView()
        setupCharts()
        setupObservers()

        return binding.root
    }

    /**
     * TOP 감점 항목 RecyclerView를 설정합니다.
     */
    private fun setupRecyclerView() {
        topDeductionAdapter = TopDeductionAdapter()

        binding.recyclerTopDeductions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = topDeductionAdapter
            isNestedScrollingEnabled = false
        }
    }

    /**
     * 차트들을 설정합니다.
     */
    private fun setupCharts() {
        // 항목별 점수 막대 차트 설정
        val categoryScores = statisticsViewModel.getCategoryScoresData()
        statisticsViewModel.setupBarChart(binding.chartCategoryScores, categoryScores, requireContext())

        // 트렌드 라인 차트 설정
        val trendData = statisticsViewModel.getTrendData()
        statisticsViewModel.setupTrendChart(binding.chartTrend, trendData, requireContext())
    }

    /**
     * ViewModel의 LiveData를 관찰하여 UI를 업데이트합니다.
     */
    private fun setupObservers() {
        // 총 세션 수 관찰
        statisticsViewModel.totalSessions.observe(viewLifecycleOwner) { count ->
            binding.textTotalSessionsCount.text = count.toString()
        }

        // 전체 평균 점수 관찰
        statisticsViewModel.overallAverage.observe(viewLifecycleOwner) { score ->
            binding.textOverallAverage.text = score.toString()
            binding.textOverallAverage.setTextColor(
                ScoreUtil.getColorForScore(requireContext(), score)
            )
        }

        // 합격률 관찰
        statisticsViewModel.passRatePercentage.observe(viewLifecycleOwner) { rate ->
            binding.textPassRatePercentage.text = "${rate}%"
            binding.textPassRatePercentage.setTextColor(
                statisticsViewModel.getPassRateColor(requireContext(), rate)
            )
        }

        // 개별 항목 평균 점수 관찰
        statisticsViewModel.speedAverage.observe(viewLifecycleOwner) { score ->
            binding.textSpeedAverage.text = score.toString()
            binding.textSpeedAverage.setTextColor(ScoreUtil.getColorForScore(requireContext(), score))
            binding.progressSpeedAverage.progress = score
            setProgressBarColor(binding.progressSpeedAverage, score)
        }

        statisticsViewModel.turningAverage.observe(viewLifecycleOwner) { score ->
            binding.textTurningAverage.text = score.toString()
            binding.textTurningAverage.setTextColor(ScoreUtil.getColorForScore(requireContext(), score))
            binding.progressTurningAverage.progress = score
            setProgressBarColor(binding.progressTurningAverage, score)
        }

        statisticsViewModel.brakingAverage.observe(viewLifecycleOwner) { score ->
            binding.textBrakingAverage.text = score.toString()
            binding.textBrakingAverage.setTextColor(ScoreUtil.getColorForScore(requireContext(), score))
            binding.progressBrakingAverage.progress = score
            setProgressBarColor(binding.progressBrakingAverage, score)
        }

        // TOP 감점 항목 리스트 관찰
        statisticsViewModel.topDeductions.observe(viewLifecycleOwner) { topDeductions ->
            topDeductionAdapter.submitList(topDeductions)
        }

        // 트렌드 정보 관찰
        statisticsViewModel.trendDescription.observe(viewLifecycleOwner) { description ->
            binding.textTrendDescription.text = description
        }

        // 트렌드 방향 관찰
        statisticsViewModel.isImproveTrend.observe(viewLifecycleOwner) { isImproving ->
            if (isImproving) {
                binding.iconTrendArrow.setImageResource(com.example.marketplace.R.drawable.ic_trending_up)
                binding.iconTrendArrow.setColorFilter(
                    androidx.core.content.ContextCompat.getColor(requireContext(),
                        com.example.marketplace.R.color.score_excellent)
                )
            } else {
                binding.iconTrendArrow.setImageResource(com.example.marketplace.R.drawable.ic_trending_up) // trending_down 아이콘이 없어서 동일하게 사용
                binding.iconTrendArrow.setColorFilter(
                    androidx.core.content.ContextCompat.getColor(requireContext(),
                        com.example.marketplace.R.color.status_fail)
                )
            }
        }

        // 최근 점수 관찰
        statisticsViewModel.latestScore.observe(viewLifecycleOwner) { score ->
            binding.textLatestScore.text = "최근: ${score}점"
            binding.textLatestScore.setTextColor(ScoreUtil.getColorForScore(requireContext(), score))
        }
    }

    /**
     * 프로그레스 바의 색상을 점수에 따라 설정합니다.
     *
     * @param progressBar 색상을 설정할 프로그레스 바
     * @param score 점수 (0-100)
     */
    private fun setProgressBarColor(progressBar: android.widget.ProgressBar, score: Int) {
        val colorStateList = android.content.res.ColorStateList.valueOf(
            ScoreUtil.getColorForScore(requireContext(), score)
        )
        progressBar.progressTintList = colorStateList
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}