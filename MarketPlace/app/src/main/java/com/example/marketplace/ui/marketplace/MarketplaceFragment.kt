package com.example.marketplace.ui.marketplace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marketplace.R
import com.example.marketplace.data.DrivingSession
import com.example.marketplace.databinding.FragmentMarketplaceBinding
import com.example.marketplace.ui.marketplace.adapter.DrivingSessionCardAdapter
import com.example.marketplace.util.ScoreUtil

/**
 * 마켓플레이스 대시보드 프래그먼트
 * - 평균 점수 및 합격률 차트 표시
 * - 최근 AI 피드백 카드 표시
 * - 주행 세션 리스트 (카드 형태로 표시)
 * - 새 세션 시작 FAB 버튼 (시각적으로만 존재)
 */
class MarketplaceFragment : Fragment() {

    private var _binding: FragmentMarketplaceBinding? = null
    private val binding get() = _binding!!

    private lateinit var marketplaceViewModel: MarketplaceViewModel
    private lateinit var sessionAdapter: DrivingSessionCardAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // ViewModel 초기화
        marketplaceViewModel = ViewModelProvider(this)[MarketplaceViewModel::class.java]

        // ViewBinding 초기화
        _binding = FragmentMarketplaceBinding.inflate(inflater, container, false)

        // UI 설정
        setupChart()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        return binding.root
    }

    /**
     * 차트를 설정합니다.
     */
    private fun setupChart() {
        val chartData = marketplaceViewModel.getPerformanceChartData()

        if (chartData.isNotEmpty()) {
            // MPAndroidChart를 사용한 라인 차트 설정
            marketplaceViewModel.setupLineChart(binding.chartPerformance, chartData, requireContext())
        } else {
            // 차트 숨김
            binding.chartPerformance.visibility = View.GONE
        }
    }

    /**
     * RecyclerView 어댑터를 설정합니다.
     */
    private fun setupRecyclerView() {
        sessionAdapter = DrivingSessionCardAdapter { session ->
            navigateToSessionDetails(session)
        }

        binding.recyclerSessions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = sessionAdapter

            // 아이템 간격 설정
            addItemDecoration(object : androidx.recyclerview.widget.RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: android.graphics.Rect,
                    view: View,
                    parent: androidx.recyclerview.widget.RecyclerView,
                    state: androidx.recyclerview.widget.RecyclerView.State
                ) {
                    outRect.bottom = resources.getDimensionPixelSize(R.dimen.spacing_sm)
                }
            })
        }
    }

    /**
     * ViewModel의 LiveData를 관찰하여 UI를 업데이트합니다.
     */
    private fun setupObservers() {
        // 평균 점수 관찰
        marketplaceViewModel.averageScore.observe(viewLifecycleOwner) { score ->
            binding.textAverageScoreValue.text = score.toString()
            binding.textAverageScoreValue.setTextColor(
                ScoreUtil.getColorForScore(requireContext(), score)
            )
        }

        // 합격률 관찰
        marketplaceViewModel.passRate.observe(viewLifecycleOwner) { rate ->
            binding.textPassRateValue.text = "${rate}%"
            binding.textPassRateValue.setTextColor(
                marketplaceViewModel.getPassRateColor(requireContext(), rate)
            )
        }

        // 최근 피드백 관찰
        marketplaceViewModel.latestFeedback.observe(viewLifecycleOwner) { feedback ->
            feedback?.let { session ->
                binding.textFeedbackContent.text = session.instructorFeedback
                binding.textFeedbackDate.text = marketplaceViewModel.formatDate(session.date)
                binding.textFeedbackScore.text = "세션 점수: ${session.overallScore}점"
                binding.textFeedbackScore.setTextColor(
                    ScoreUtil.getColorForScore(requireContext(), session.overallScore)
                )
            }
        }

        // 세션 리스트 관찰
        marketplaceViewModel.sessions.observe(viewLifecycleOwner) { sessions ->
            if (sessions.isNotEmpty()) {
                sessionAdapter.submitList(sessions)
                binding.recyclerSessions.visibility = View.VISIBLE
                binding.groupEmptyState.visibility = View.GONE
            } else {
                binding.recyclerSessions.visibility = View.GONE
                binding.groupEmptyState.visibility = View.VISIBLE
            }
        }
    }

    /**
     * 클릭 리스너를 설정합니다.
     */
    private fun setupClickListeners() {
        // 새 세션 시작 FAB (DEMO용 - 아무 동작하지 않음)
        binding.fabStartSession.setOnClickListener {
            // DEMO 버전: 클릭 리스너는 비어있음
            // 실제 앱에서는 여기에 AI 도로주행 시작 로직이 들어감
        }
    }

    /**
     * 세션 상세 화면으로 이동합니다.
     *
     * @param session 상세를 보고자 하는 드라이빙 세션
     */
    private fun navigateToSessionDetails(session: DrivingSession) {
        val bundle = Bundle().apply {
            putString("sessionId", session.id)
        }
        findNavController().navigate(
            R.id.action_marketplace_to_sessionDetails,
            bundle
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}