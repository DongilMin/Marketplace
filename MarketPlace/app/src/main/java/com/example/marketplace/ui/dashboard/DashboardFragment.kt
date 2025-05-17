package com.example.marketplace.ui.dashboard

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
import com.example.marketplace.databinding.FragmentDashboardBinding
import com.example.marketplace.util.ScoreUtil

/**
 * Fragment for the Dashboard screen
 * Displays performance metrics and a list of driving sessions
 */
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var sessionAdapter: DrivingSessionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize ViewModel
        dashboardViewModel = ViewModelProvider(this)[DashboardViewModel::class.java]

        // Inflate layout
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        // Set up observers and UI
        setupObservers()
        setupRecyclerView()

        return binding.root
    }

    /**
     * Sets up LiveData observers
     */
    private fun setupObservers() {
        // Observe average score
        dashboardViewModel.averageScore.observe(viewLifecycleOwner) { score ->
            binding.textAverageScore.text = score.toString()
            binding.textAverageScore.setTextColor(ScoreUtil.getColorForScore(requireContext(), score))
        }

        // Observe category scores
        dashboardViewModel.speedScore.observe(viewLifecycleOwner) { score ->
            binding.textSpeedScore.text = score.toString()
            binding.textSpeedScore.setTextColor(ScoreUtil.getColorForScore(requireContext(), score))
            binding.progressSpeed.progress = score

            // Set progress bar color
            val colorStateList = android.content.res.ColorStateList.valueOf(
                ScoreUtil.getColorForScore(requireContext(), score)
            )
            binding.progressSpeed.progressTintList = colorStateList
        }

        dashboardViewModel.turningScore.observe(viewLifecycleOwner) { score ->
            binding.textTurningScore.text = score.toString()
            binding.textTurningScore.setTextColor(ScoreUtil.getColorForScore(requireContext(), score))
            binding.progressTurning.progress = score

            // Set progress bar color
            val colorStateList = android.content.res.ColorStateList.valueOf(
                ScoreUtil.getColorForScore(requireContext(), score)
            )
            binding.progressTurning.progressTintList = colorStateList
        }

        dashboardViewModel.brakingScore.observe(viewLifecycleOwner) { score ->
            binding.textBrakingScore.text = score.toString()
            binding.textBrakingScore.setTextColor(ScoreUtil.getColorForScore(requireContext(), score))
            binding.progressBraking.progress = score

            // Set progress bar color
            val colorStateList = android.content.res.ColorStateList.valueOf(
                ScoreUtil.getColorForScore(requireContext(), score)
            )
            binding.progressBraking.progressTintList = colorStateList
        }

        // Observe whether there are any sessions
        dashboardViewModel.hasSessions.observe(viewLifecycleOwner) { hasSessions ->
            binding.groupHasSessions.visibility = if (hasSessions) View.VISIBLE else View.GONE
            binding.groupNoSessions.visibility = if (hasSessions) View.GONE else View.VISIBLE
        }
    }

    /**
     * Sets up the session RecyclerView
     */
    private fun setupRecyclerView() {
        // Create adapter with empty list initially
        sessionAdapter = DrivingSessionAdapter(emptyList()) { session ->
            navigateToSessionDetails(session)
        }

        // Set up RecyclerView
        binding.recyclerSessions.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = sessionAdapter
        }

        // Observe session list
        dashboardViewModel.allSessions.observe(viewLifecycleOwner) { sessions ->
            sessionAdapter = DrivingSessionAdapter(sessions) { session ->
                navigateToSessionDetails(session)
            }
            binding.recyclerSessions.adapter = sessionAdapter
        }
    }

    /**
     * Navigates to the session details screen
     */
    private fun navigateToSessionDetails(session: DrivingSession) {
        val bundle = Bundle().apply {
            putString("sessionId", session.id)
        }
        findNavController().navigate(R.id.action_navigation_dashboard_to_sessionDetailsFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}