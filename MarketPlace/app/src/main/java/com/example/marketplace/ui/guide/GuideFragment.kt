package com.example.marketplace.ui.guide

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.marketplace.R
import com.example.marketplace.databinding.FragmentGuideBinding

/**
 * 가이드 화면 프래그먼트
 * 앱 사용법, 도로주행 시험 개요, AI 분석 방식 등의 정보를 제공합니다.
 */
class GuideFragment : Fragment() {

    private var _binding: FragmentGuideBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGuideBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 감점 항목 사전 버튼 클릭 리스너 설정
        binding.buttonDeductionGuide.setOnClickListener {
            findNavController().navigate(R.id.action_guide_to_deductionGuide)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}