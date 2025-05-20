package com.example.marketplace.ui.marketplace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marketplace.R
import com.example.marketplace.data.DeductionReason
import com.example.marketplace.data.DrivingSession
import com.example.marketplace.databinding.FragmentSessionDetailsBinding
import com.example.marketplace.ui.dashboard.DeductionReasonAdapter
import com.example.marketplace.ui.dashboard.SessionDetailsViewModel
import com.example.marketplace.util.ScoreUtil
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Fragment for the Session Details screen
 * Displays detailed information about a specific driving session
 */
class SessionDetailsFragment : Fragment() {

    private var _binding: FragmentSessionDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SessionDetailsViewModel
    private val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
    private lateinit var deductionAdapter: DeductionReasonAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[SessionDetailsViewModel::class.java]

        // Inflate layout
        _binding = FragmentSessionDetailsBinding.inflate(inflater, container, false)

        // Get session ID from arguments and load session
        arguments?.getString("sessionId")?.let { sessionId ->
            viewModel.loadSession(sessionId)
        }

        // Set up observers and UI
        setupObservers()
        setupRecyclerView()

        return binding.root
    }

    /**
     * Sets up LiveData observers
     */
    private fun setupObservers() {
        // Observe whether session data exists
        viewModel.hasSessionData.observe(viewLifecycleOwner) { hasData ->
            // Show content or empty state based on whether data exists
            binding.textSessionTitle.visibility = if (hasData) View.VISIBLE else View.GONE
            binding.textSessionDate.visibility = if (hasData) View.VISIBLE else View.GONE
            binding.cardResult.visibility = if (hasData) View.VISIBLE else View.GONE
            binding.cardDeductions.visibility = if (hasData) View.VISIBLE else View.GONE
            binding.cardFeedback.visibility = if (hasData) View.VISIBLE else View.GONE
            binding.textEmptyState.visibility = if (hasData) View.GONE else View.VISIBLE
        }

        // Observe session data
        viewModel.session.observe(viewLifecycleOwner) { session ->
            session?.let { updateUI(it) }
        }

        // Observe total points deducted
        viewModel.totalPointsDeducted.observe(viewLifecycleOwner) { points ->
            binding.textTotalDeductions.text = "-$points ${getString(R.string.points_deducted)}"
        }
    }

    /**
     * Sets up the deduction reasons RecyclerView
     */
    private fun setupRecyclerView() {
        // Set up RecyclerView with empty list initially
        deductionAdapter = DeductionReasonAdapter(emptyList())
        binding.recyclerDeductions.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = deductionAdapter
        }
    }

    /**
     * Updates the UI with session data
     */
    private fun updateUI(session: DrivingSession) {
        // Set title and date
        binding.textSessionTitle.text = getString(R.string.session_details)
        binding.textSessionDate.text = "${dateFormat.format(session.date)} • ${session.durationMinutes}분"

        // Set examination result
        val resultText = if (session.isDisqualified) {
            getString(R.string.disqualified)
        } else if (session.isPassed) {
            getString(R.string.passed)
        } else {
            getString(R.string.failed)
        }
        binding.textResultStatus.text = resultText

        // Set result color
        val resultColor = when {
            session.isDisqualified -> resources.getColor(R.color.status_disqualified, null)
            session.isPassed -> resources.getColor(R.color.status_pass, null)
            else -> resources.getColor(R.color.status_fail, null)
        }
        binding.textResultStatus.setTextColor(resultColor)

        // Show/hide disqualified banner
        binding.textDisqualified.visibility = if (session.isDisqualified) View.VISIBLE else View.GONE

        // Set scores
        binding.textOverallScore.text = session.overallScore.toString()
        binding.textOverallScore.setTextColor(ScoreUtil.getColorForScore(requireContext(), session.overallScore))

        binding.textSpeedScore.text = session.speedScore.toString()
        binding.textSpeedScore.setTextColor(ScoreUtil.getColorForScore(requireContext(), session.speedScore))

        binding.textTurningScore.text = session.turningScore.toString()
        binding.textTurningScore.setTextColor(ScoreUtil.getColorForScore(requireContext(), session.turningScore))

        binding.textBrakingScore.text = session.brakingScore.toString()
        binding.textBrakingScore.setTextColor(ScoreUtil.getColorForScore(requireContext(), session.brakingScore))

        // Set deduction reasons
        deductionAdapter = DeductionReasonAdapter(session.deductionReasons)
        binding.recyclerDeductions.adapter = deductionAdapter

        // Set instructor feedback
        binding.textFeedbackContent.text = session.instructorFeedback
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}