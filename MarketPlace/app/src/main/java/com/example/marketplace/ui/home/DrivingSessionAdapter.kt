package com.example.marketplace.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.marketplace.R
import com.example.marketplace.data.DrivingSession
import com.example.marketplace.util.ScoreUtil
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DrivingSessionAdapter(
    private val sessionList: List<DrivingSession>,
    private val onItemClicked: (DrivingSession) -> Unit
) : RecyclerView.Adapter<DrivingSessionAdapter.ViewHolder>() {

    private val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textDate: TextView = itemView.findViewById(R.id.text_session_date)
        private val textTime: TextView = itemView.findViewById(R.id.text_session_time)
        private val badgeDuration: TextView = itemView.findViewById(R.id.badge_duration)
        private val badgeStatus: TextView = itemView.findViewById(R.id.badge_status)
        private val textScore: TextView = itemView.findViewById(R.id.text_session_score)
        private val iconTrend: ImageView = itemView.findViewById(R.id.icon_trend)

        fun bind(session: DrivingSession) {
            // Set date with friendly format (Today, Yesterday, or date)
            val today = Calendar.getInstance()
            val sessionDate = Calendar.getInstance().apply { time = session.date }
            val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

            textDate.text = when {
                isSameDay(today, sessionDate) -> "Today"
                isSameDay(yesterday, sessionDate) -> "Yesterday"
                else -> dateFormat.format(session.date)
            }

            // Set time
            textTime.text = timeFormat.format(session.date)

            // Set duration
            badgeDuration.text = "${session.durationMinutes} min"

            // Set status badge
            val statusText = ScoreUtil.getRatingForScore(session.overallScore)
            badgeStatus.text = statusText

            // Set status badge background and text color based on score
            when {
                session.overallScore >= 85 -> {
                    badgeStatus.background = ContextCompat.getDrawable(itemView.context, R.drawable.badge_green_background)
                    badgeStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.badge_green_text))
                }
                session.overallScore >= 75 -> {
                    badgeStatus.background = ContextCompat.getDrawable(itemView.context, R.drawable.badge_yellow_background)
                    badgeStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.badge_yellow_text))
                }
                else -> {
                    badgeStatus.background = ContextCompat.getDrawable(itemView.context, R.drawable.badge_red_background)
                    badgeStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.badge_red_text))
                }
            }

            // Set score
            textScore.text = session.overallScore.toString()
            textScore.setTextColor(ScoreUtil.getColorForScore(itemView.context, session.overallScore))

            // Set trend icon color (always show as positive for demo)
            iconTrend.setColorFilter(ContextCompat.getColor(itemView.context, R.color.score_excellent))

            // Set click listener
            itemView.setOnClickListener {
                onItemClicked(session)
            }
        }

        private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_driving_session, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(sessionList[position])
    }

    override fun getItemCount() = sessionList.size
}