package com.example.marketplace.util

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.marketplace.R
import com.example.marketplace.data.DeductionSeverity

/**
 * Utility class for score-related functionality
 */
object ScoreUtil {

    /**
     * Gets the appropriate color resource for a given score
     *
     * @param context Context to access resources
     * @param score The score value (0-100)
     * @return Color resource ID
     */
    fun getColorForScore(context: Context, score: Int): Int {
        return when {
            score >= 90 -> ContextCompat.getColor(context, R.color.score_excellent)
            score >= 80 -> ContextCompat.getColor(context, R.color.score_good)
            score >= 70 -> ContextCompat.getColor(context, R.color.score_average)
            score >= 60 -> ContextCompat.getColor(context, R.color.score_needs_work)
            else -> ContextCompat.getColor(context, R.color.score_poor)
        }
    }

    /**
     * Gets a textual rating description for a given score
     *
     * @param score The score value (0-100)
     * @return String description of the score
     */
    fun getRatingForScore(score: Int): String {
        return when {
            score >= 90 -> "Excellent"
            score >= 80 -> "Good"
            score >= 70 -> "Satisfactory"
            score >= 60 -> "Needs Improvement"
            else -> "Poor"
        }
    }

    /**
     * Determines pass/fail status based on score
     *
     * @param score The score value (0-100)
     * @return True if passed (score >= 70), false otherwise
     */
    fun isPassingScore(score: Int): Boolean {
        return score >= 70
    }

    /**
     * Gets the color for a pass/fail status
     *
     * @param context Context to access resources
     * @param isPassed Whether the session was passed
     * @return Color resource ID
     */
    fun getPassFailColor(context: Context, isPassed: Boolean): Int {
        return if (isPassed) {
            ContextCompat.getColor(context, R.color.status_pass)
        } else {
            ContextCompat.getColor(context, R.color.status_fail)
        }
    }

    /**
     * Gets the color for a deduction severity
     *
     * @param context Context to access resources
     * @param severity The deduction severity
     * @return Color resource ID
     */
    fun getColorForSeverity(context: Context, severity: DeductionSeverity): Int {
        return when (severity) {
            DeductionSeverity.MINOR -> ContextCompat.getColor(context, R.color.severity_minor)
            DeductionSeverity.MAJOR -> ContextCompat.getColor(context, R.color.severity_major)
            DeductionSeverity.CRITICAL -> ContextCompat.getColor(context, R.color.severity_critical)
        }
    }
}