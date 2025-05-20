package com.example.marketplace.ui.guide

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marketplace.databinding.FragmentDeductionGuideBinding

/**
 * 감점 항목 사전 프래그먼트
 * 도로주행 시험의 감점 항목과 기준을 상세히 보여줍니다.
 */
class DeductionGuideFragment : Fragment() {

    private var _binding: FragmentDeductionGuideBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeductionGuideBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 툴바 설정
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        // 리사이클러뷰 설정
        binding.recyclerDeductionItems.apply {
            layoutManager = LinearLayoutManager(context)
            // 여기에 어댑터 설정 (실제 구현에서는 어댑터를 만들어 연결해야 함)
            // adapter = DeductionGuideAdapter(getDeductionItems())
        }

        // 탭 레이아웃 리스너 설정
        binding.tabLayout.addOnTabSelectedListener(object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                // 탭에 따른 필터링 기능 구현
            }

            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                // 필요한 경우 구현
            }

            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                // 필요한 경우 구현
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}