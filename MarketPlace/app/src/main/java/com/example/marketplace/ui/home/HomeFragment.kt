package com.example.marketplace.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.marketplace.R
import com.example.marketplace.databinding.FragmentHomeBinding
import com.example.marketplace.ui.dashboard.DrivingSessionAdapter

/**
 * 홈 화면 프래그먼트
 * - 앱 소개 및 환영 메시지 표시
 * - 현재 사용자의 진행 상황 요약 (총 연습 횟수, 평균 점수, 합격률)
 * - AI 도로주행 시작하기 버튼 (시각적으로만 존재)
 * - 내 기록 보기 버튼 (마켓플레이스로 네비게이션)
 * - 빠른 액세스 메뉴 (감점 항목 사전, 내 통계)
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // ViewModel 초기화
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        // ViewBinding 초기화
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // UI 설정
        setupObservers()
        setupClickListeners()

        return binding.root
    }

    /**
     * ViewModel의 LiveData를 관찰하여 UI를 업데이트합니다.
     */
    private fun setupObservers() {
        // 총 세션 수 관찰
        homeViewModel.totalSessions.observe(viewLifecycleOwner) { count ->
            binding.textTotalSessions.text = count.toString()
        }

        // 평균 점수 관찰
        homeViewModel.averageScore.observe(viewLifecycleOwner) { score ->
            binding.textAverageScore.text = score.toString()
            binding.textAverageScore.setTextColor(
                homeViewModel.getScoreColor(requireContext(), score)
            )
        }

        // 합격률 관찰
        homeViewModel.passRate.observe(viewLifecycleOwner) { rate ->
            binding.textPassRate.text = "${rate}%"
            binding.textPassRate.setTextColor(
                homeViewModel.getPassRateColor(requireContext(), rate)
            )
        }
    }

    /**
     * 버튼 클릭 리스너를 설정합니다.
     */
    private fun setupClickListeners() {
        // AI 도로주행 시작하기 버튼 (아무 동작하지 않음 - DEMO용)
        binding.buttonStartDriving.setOnClickListener {
            // DEMO 버전: 클릭 리스너는 비어있음
            // 실제 앱에서는 여기에 AI 도로주행 시작 로직이 들어감
        }

        // 내 기록 보기 버튼 - 마켓플레이스로 이동
        binding.buttonViewRecords.setOnClickListener {
            findNavController().navigate(R.id.navigation_marketplace)
        }

        // 감점 항목 사전 카드 클릭
        binding.cardDeductionGuide.setOnClickListener {
            findNavController().navigate(R.id.deductionGuideFragment) // 직접 대상 프래그먼트로 이동
        }

        // 내 통계 카드 클릭
        binding.cardStatistics.setOnClickListener {
            findNavController().navigate(R.id.navigation_statistics)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}