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
import com.example.marketplace.util.ScoreUtil
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Fragment for the Home screen
 * Displays welcome message, latest driving session, and action buttons
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel
    private val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize ViewModel
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        // Inflate layout
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Set up observers
        setupObservers()

        // Set up button click listeners
        setupClickListeners()

        return binding.root
    }

    /**
     * Sets up LiveData observers to update UI when data changes
     */
    private fun setupObservers() {
        // Observe whether any sessions exist
        homeViewModel.hasAnySessions.observe(viewLifecycleOwner) { hasAnySessions ->
            // Show/hide appropriate UI groups based on whether sessions exist
            binding.groupHasSessions.visibility = if (hasAnySessions) View.VISIBLE else View.GONE
            binding.groupNoSessions.visibility = if (hasAnySessions) View.GONE else View.VISIBLE
        }

        // Observe latest session data
        homeViewModel.latestSession.observe(viewLifecycleOwner) { session ->
            // Only update UI if session exists
            session?.let {
                // Set date and duration
                binding.textSessionDate.text = dateFormat.format(it.date)
                binding.textSessionDuration.text = "${it.durationMinutes} ${getString(R.string.minutes_short)}"

                // Set pass/fail status chip
                val statusText = if (it.isDisqualified) {
                    getString(R.string.disqualified)
                } else if (it.isPassed) {
                    getString(R.string.passed)
                } else {
                    getString(R.string.failed)
                }

                binding.chipStatus.text = statusText

                // Set chip background color based on status
                val chipColor = when {
                    it.isDisqualified -> resources.getColor(R.color.status_disqualified, null)
                    it.isPassed -> resources.getColor(R.color.status_pass, null)
                    else -> resources.getColor(R.color.status_fail, null)
                }
                binding.chipStatus.chipBackgroundColor = android.content.res.ColorStateList.valueOf(chipColor)

                // Set score and its color
                binding.textScoreValue.text = it.overallScore.toString()
                binding.textScoreValue.setTextColor(ScoreUtil.getColorForScore(requireContext(), it.overallScore))

                // Set rating text and color
                val ratingText = ScoreUtil.getRatingForScore(it.overallScore)
                binding.textScoreRating.text = ratingText
                binding.textScoreRating.setTextColor(ScoreUtil.getColorForScore(requireContext(), it.overallScore))

                // Set category scores
                binding.textSpeedScore.text = it.speedScore.toString()
                binding.textSpeedScore.setTextColor(ScoreUtil.getColorForScore(requireContext(), it.speedScore))

                binding.textTurningScore.text = it.turningScore.toString()
                binding.textTurningScore.setTextColor(ScoreUtil.getColorForScore(requireContext(), it.turningScore))

                binding.textBrakingScore.text = it.brakingScore.toString()
                binding.textBrakingScore.setTextColor(ScoreUtil.getColorForScore(requireContext(), it.brakingScore))

                // Set feedback text
                binding.textFeedbackContent.text = it.instructorFeedback
            }
        }
    }

    /**
     * Sets up click listeners for buttons
     */
    private fun setupClickListeners() {
        // View Dashboard button - navigates to Dashboard screen
        binding.buttonViewDashboard.setOnClickListener {
            findNavController().navigate(R.id.navigation_dashboard)
        }

        // View History button - also navigates to Dashboard screen
        binding.buttonViewHistory.setOnClickListener {
            findNavController().navigate(R.id.navigation_dashboard)
        }

        // Start New Session buttons (both in with-sessions and no-sessions views)
        binding.buttonStartSession.setOnClickListener {
            // In a real app, would navigate to session creation flow
            // For demo, just show a toast
            showFeatureNotAvailableMessage()
        }

        binding.buttonStartNewSession.setOnClickListener {
            showFeatureNotAvailableMessage()
        }

        // Make latest session card clickable to view details
        binding.cardLatestSession.setOnClickListener {
            homeViewModel.latestSession.value?.let { session ->
                val bundle = Bundle().apply {
                    putString("sessionId", session.id)
                }
                findNavController().navigate(R.id.action_navigation_home_to_sessionDetailsFragment, bundle)
            }
        }
    }

    /**
     * Shows a message indicating a feature is not available in the demo
     */
    private fun showFeatureNotAvailableMessage() {
        com.google.android.material.snackbar.Snackbar.make(
            binding.root,
            "This feature is not available in the demo",
            com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}