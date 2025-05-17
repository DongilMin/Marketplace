package com.example.marketplace.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Data class representing a driving session record
 *
 * @property id Unique identifier for the driving session
 * @property date Date when the driving session occurred
 * @property durationMinutes Duration of the session in minutes
 * @property overallScore Overall driving score (0-100)
 * @property speedScore Score for speed management (0-100)
 * @property turningScore Score for turning technique (0-100)
 * @property brakingScore Score for braking technique (0-100)
 * @property isPassed Whether the session is considered passing (score >= 70)
 * @property isDisqualified Whether the driver was disqualified during the session
 * @property deductionReasons List of reasons for point deductions
 * @property instructorFeedback Feedback from the driving instructor
 */
data class DrivingSession(
    val id: String,
    val date: Date,
    val durationMinutes: Int,
    val overallScore: Int,
    val speedScore: Int,
    val turningScore: Int,
    val brakingScore: Int,
    val isPassed: Boolean,
    val isDisqualified: Boolean,
    val deductionReasons: List<DeductionReason>,
    val instructorFeedback: String
) {
    // Calculated property to determine if session was passed
    val hasPassedExam: Boolean get() = overallScore >= 70 && !isDisqualified
}

/**
 * Data class representing a reason for point deduction
 *
 * @property reason The description of the deduction reason
 * @property pointsDeducted Number of points deducted
 * @property severity The severity level of the deduction
 */
data class DeductionReason(
    val reason: String,
    val pointsDeducted: Int,
    val severity: DeductionSeverity
)

/**
 * Enum class representing the severity of a deduction
 */
enum class DeductionSeverity {
    MINOR, MAJOR, CRITICAL
}

/**
 * Enum representing different scoring categories for driving assessment
 */
enum class ScoreCategory {
    SPEED, TURNING, BRAKING
}

/**
 * Object containing fake driving data for demonstration purposes
 */
object FakeDrivingData {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    /**
     * List of sample driving sessions
     */
    val drivingSessions = listOf(
        DrivingSession(
            id = "session1",
            date = dateFormat.parse("2025-05-15") ?: Date(),
            durationMinutes = 45,
            overallScore = 85,
            speedScore = 80,
            turningScore = 90,
            brakingScore = 85,
            isPassed = true,
            isDisqualified = false,
            deductionReasons = listOf(
                DeductionReason("Exceeding speed limit by 5-10 km/h", 5, DeductionSeverity.MINOR),
                DeductionReason("Incomplete stop at stop sign", 5, DeductionSeverity.MINOR),
                DeductionReason("Late turn signal", 2, DeductionSeverity.MINOR),
                DeductionReason("Hesitation at intersection", 3, DeductionSeverity.MINOR)
            ),
            instructorFeedback = "Overall good driving. Maintain consistent speed on highways. Right turns executed well. Pay more attention to complete stops at intersections."
        ),
        DrivingSession(
            id = "session2",
            date = dateFormat.parse("2025-05-10") ?: Date(),
            durationMinutes = 30,
            overallScore = 70,
            speedScore = 65,
            turningScore = 75,
            brakingScore = 70,
            isPassed = true,
            isDisqualified = false,
            deductionReasons = listOf(
                DeductionReason("Rough braking", 10, DeductionSeverity.MAJOR),
                DeductionReason("Too slow on highway", 5, DeductionSeverity.MINOR),
                DeductionReason("Lane positioning issues", 8, DeductionSeverity.MINOR),
                DeductionReason("Improper mirror checks", 7, DeductionSeverity.MINOR)
            ),
            instructorFeedback = "Acceptable driving but with areas for improvement. Work on smoother braking and maintaining proper speed. Some handling issues in urban environments."
        ),
        DrivingSession(
            id = "session3",
            date = dateFormat.parse("2025-05-05") ?: Date(),
            durationMinutes = 60,
            overallScore = 92,
            speedScore = 95,
            turningScore = 90,
            brakingScore = 92,
            isPassed = true,
            isDisqualified = false,
            deductionReasons = listOf(
                DeductionReason("Minor parking alignment issue", 3, DeductionSeverity.MINOR),
                DeductionReason("Slightly delayed reaction to pedestrian", 5, DeductionSeverity.MINOR)
            ),
            instructorFeedback = "Excellent driving session! Very smooth handling, appropriate speed maintenance and good situational awareness. Only minor issues with parking and pedestrian awareness."
        ),
        DrivingSession(
            id = "session4",
            date = dateFormat.parse("2025-05-01") ?: Date(),
            durationMinutes = 25,
            overallScore = 45,
            speedScore = 30,
            turningScore = 50,
            brakingScore = 55,
            isPassed = false,
            isDisqualified = true,
            deductionReasons = listOf(
                DeductionReason("Red light violation", 0, DeductionSeverity.CRITICAL),
                DeductionReason("Excessive speed in school zone", 15, DeductionSeverity.MAJOR),
                DeductionReason("Unsafe lane change", 10, DeductionSeverity.MAJOR),
                DeductionReason("Poor observation at intersections", 10, DeductionSeverity.MAJOR),
                DeductionReason("Insufficient following distance", 8, DeductionSeverity.MINOR),
                DeductionReason("Incorrect use of signals", 5, DeductionSeverity.MINOR)
            ),
            instructorFeedback = "Session terminated due to critical safety violation (red light). Significant issues with speed control and observation. Must improve fundamentally before next attempt."
        ),
        DrivingSession(
            id = "session5",
            date = dateFormat.parse("2025-04-28") ?: Date(),
            durationMinutes = 40,
            overallScore = 78,
            speedScore = 75,
            turningScore = 80,
            brakingScore = 78,
            isPassed = true,
            isDisqualified = false,
            deductionReasons = listOf(
                DeductionReason("Hesitation during merge", 5, DeductionSeverity.MINOR),
                DeductionReason("Speed too slow for conditions", 5, DeductionSeverity.MINOR),
                DeductionReason("Slight over-steering in curves", 7, DeductionSeverity.MINOR),
                DeductionReason("Missed rear mirror check", 5, DeductionSeverity.MINOR)
            ),
            instructorFeedback = "Good driving with room for improvement. Work on maintaining appropriate speed for conditions and more confidence during merging maneuvers. Better mirror checking needed."
        ),
        DrivingSession(
            id = "session6",
            date = dateFormat.parse("2025-04-20") ?: Date(),
            durationMinutes = 35,
            overallScore = 60,
            speedScore = 55,
            turningScore = 65,
            brakingScore = 60,
            isPassed = false,
            isDisqualified = false,
            deductionReasons = listOf(
                DeductionReason("Incorrect lane positioning", 10, DeductionSeverity.MAJOR),
                DeductionReason("Abrupt braking", 10, DeductionSeverity.MAJOR),
                DeductionReason("Failed to yield right of way", 8, DeductionSeverity.MAJOR),
                DeductionReason("Improper following distance", 5, DeductionSeverity.MINOR),
                DeductionReason("Poor observation techniques", 7, DeductionSeverity.MINOR)
            ),
            instructorFeedback = "Below passing threshold. Major issues with lane positioning and right-of-way judgment. Practice smooth braking techniques and proper yielding at intersections."
        )
    )

    /**
     * Returns the average overall score from all driving sessions
     */
    fun getAverageOverallScore(): Int {
        return drivingSessions.map { it.overallScore }.average().toInt()
    }

    /**
     * Returns the average score for a specific scoring category
     *
     * @param category The scoring category to average
     * @return The average score as an integer
     */
    fun getAverageScoreByCategory(category: ScoreCategory): Int {
        return when (category) {
            ScoreCategory.SPEED -> drivingSessions.map { it.speedScore }.average().toInt()
            ScoreCategory.TURNING -> drivingSessions.map { it.turningScore }.average().toInt()
            ScoreCategory.BRAKING -> drivingSessions.map { it.brakingScore }.average().toInt()
        }
    }

    /**
     * Returns the most recent driving session
     */
    fun getMostRecentSession(): DrivingSession {
        return drivingSessions.maxByOrNull { it.date.time } ?: drivingSessions.first()
    }

    /**
     * Returns all passed sessions
     */
    fun getPassedSessions(): List<DrivingSession> {
        return drivingSessions.filter { it.hasPassedExam }
    }

    /**
     * Returns all failed sessions
     */
    fun getFailedSessions(): List<DrivingSession> {
        return drivingSessions.filter { !it.hasPassedExam }
    }

    /**
     * Returns the session with the given ID
     *
     * @param sessionId The ID of the session to find
     * @return The session if found, null otherwise
     */
    fun getSessionById(sessionId: String): DrivingSession? {
        return drivingSessions.find { it.id == sessionId }
    }
}