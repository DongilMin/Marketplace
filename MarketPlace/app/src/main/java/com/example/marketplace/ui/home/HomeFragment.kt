package com.example.marketplace.ui.home

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marketplace.R
import com.example.marketplace.data.DrivingSession
import com.example.marketplace.databinding.FragmentHomeBinding
import com.example.marketplace.util.ScoreUtil
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.tabs.TabLayout

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var sessionAdapter: DrivingSessionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupChart()
        setupTabLayout()
        setupRecyclerView()
        observeViewModel()
        setupClickListeners()

        return binding.root
    }

    private fun setupChart() {
        val lineChart = binding.lineChart

        // Get chart data from ViewModel
        val scoreData = homeViewModel.getChartData()

        if (scoreData.isNotEmpty()) {
            setupLineChart(lineChart, scoreData)
        } else {
            // Hide chart if no data
            lineChart.visibility = View.GONE
        }
    }

    private fun setupLineChart(chart: LineChart, data: List<Pair<String, Float>>) {
        // Prepare data entries
        val entries = data.mapIndexed { index, (_, score) ->
            Entry(index.toFloat(), score)
        }

        // Create dataset
        val dataSet = LineDataSet(entries, "Score").apply {
            color = ContextCompat.getColor(requireContext(), R.color.primary)
            setCircleColor(ContextCompat.getColor(requireContext(), R.color.primary))
            lineWidth = 3f
            circleRadius = 5f
            setDrawCircleHole(false)
            valueTextSize = 0f // Hide values on points
            setDrawFilled(true)
            fillDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.chart_fill_gradient)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawValues(false)
        }

        // Set chart data
        chart.data = LineData(dataSet)

        // Customize chart appearance
        chart.apply {
            description = Description().apply { isEnabled = false }
            setTouchEnabled(true)
            isDragEnabled = false
            setScaleEnabled(false)
            setPinchZoom(false)
            legend.isEnabled = false

            // Customize X axis
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                setDrawAxisLine(false)
                textColor = ContextCompat.getColor(requireContext(), R.color.text_secondary)
                textSize = 12f
                valueFormatter = IndexAxisValueFormatter(data.map { it.first })
                granularity = 1f
            }

            // Customize left Y axis
            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = ContextCompat.getColor(requireContext(), R.color.divider)
                setDrawAxisLine(false)
                textColor = ContextCompat.getColor(requireContext(), R.color.text_secondary)
                textSize = 12f
                axisMinimum = 60f
                axisMaximum = 100f
            }

            // Hide right Y axis
            axisRight.isEnabled = false

            // Animation
            animateY(1000, Easing.EaseInOutCubic)

            // Refresh
            invalidate()
        }
    }

    private fun setupTabLayout() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        // Sessions tab
                        binding.layoutSessionsHeader.visibility = View.VISIBLE
                        binding.recyclerSessions.visibility = View.VISIBLE
                        binding.layoutInsights.visibility = View.GONE
                    }
                    1 -> {
                        // Insights tab
                        binding.layoutSessionsHeader.visibility = View.GONE
                        binding.recyclerSessions.visibility = View.GONE
                        binding.layoutInsights.visibility = View.VISIBLE
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupRecyclerView() {
        binding.recyclerSessions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            // Add item spacing
            addItemDecoration(object : androidx.recyclerview.widget.RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: android.graphics.Rect, view: View, parent: androidx.recyclerview.widget.RecyclerView, state: androidx.recyclerview.widget.RecyclerView.State) {
                    outRect.bottom = resources.getDimensionPixelSize(R.dimen.item_spacing)
                }
            })
        }

        sessionAdapter = DrivingSessionAdapter(emptyList()) { session ->
            navigateToSessionDetails(session)
        }
        binding.recyclerSessions.adapter = sessionAdapter
    }

    private fun observeViewModel() {
        homeViewModel.latestSession.observe(viewLifecycleOwner) { session ->
            session?.let {
                binding.textCurrentScore.text = it.overallScore.toString()
                binding.textCurrentScore.setTextColor(
                    ScoreUtil.getColorForScore(requireContext(), it.overallScore)
                )
            }
        }

        homeViewModel.allSessions.observe(viewLifecycleOwner) { sessions ->
            // Show only first 4 sessions for home screen
            val recentSessions = sessions.take(4)
            sessionAdapter = DrivingSessionAdapter(recentSessions) { session ->
                navigateToSessionDetails(session)
            }
            binding.recyclerSessions.adapter = sessionAdapter
        }
    }

    private fun setupClickListeners() {
        binding.buttonViewFeedback.setOnClickListener {
            // Navigate to feedback/notification screen
            findNavController().navigate(R.id.navigation_notifications)
        }

        binding.textViewAll.setOnClickListener {
            findNavController().navigate(R.id.navigation_dashboard)
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> true
                R.id.navigation_dashboard -> {
                    findNavController().navigate(R.id.navigation_dashboard)
                    true
                }
                R.id.navigation_notifications -> {
                    findNavController().navigate(R.id.navigation_notifications)
                    true
                }
                else -> false
            }
        }
    }

    private fun navigateToSessionDetails(session: DrivingSession) {
        val bundle = Bundle().apply {
            putString("sessionId", session.id)
        }
        findNavController().navigate(
            R.id.action_navigation_home_to_sessionDetailsFragment,
            bundle
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// Extension function to get color safely
private fun Fragment.getCompatColor(colorRes: Int): Int {
    return ContextCompat.getColor(requireContext(), colorRes)
}