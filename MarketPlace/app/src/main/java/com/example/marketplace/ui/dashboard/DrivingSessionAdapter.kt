package com.example.marketplace.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.marketplace.R
import com.example.marketplace.data.DrivingSession
import com.example.marketplace.util.ScoreUtil
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Adapter for displaying driving session items in a RecyclerView
 *
 * @property sessionList List of driving sessions to display
 * @property onItemClicked Callback for when an item is clicked
 */
class DrivingSessionAdapter(
    private val sessionList: List<DrivingSession>,
    private val onItemClicked: (DrivingSession) -> Unit
) : RecyclerView.Adapter<DrivingSessionAdapter.ViewHolder>() {

    private val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())

    /**
     * ViewHolder for session list items
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textDate: TextView = itemView.findViewById(R.id.text_session_date)
        private val textDuration: TextView = itemView.findViewById(R.id.text_session_duration)
        private val textScore: TextView = itemView.findViewById(R.id.text_session_score)
        private val chipPassFail: Chip = itemView.findViewById(R.id.chip_pass_fail)
        private val chipDisqualified: Chip = itemView.findViewById(R.id.chip_disqualified)
        private val buttonViewDetails: MaterialButton = itemView.findViewById(R.id.button_view_details)

        /**
         * Binds a driving session data item to the view
         */
        fun bind(session: DrivingSession) {
            // Set basic session data
            textDate.text = dateFormat.format(session.date)
            textDuration.text = "${session.durationMinutes} ${itemView.context.getString(R.string.minutes_short)}"

            // Set score and its color
            textScore.text = session.overallScore.toString()
            val scoreColor = ScoreUtil.getColorForScore(itemView.context, session.overallScore)
            textScore.setTextColor(scoreColor)

            // Set pass/fail status
            val passFailText = if (session.isPassed) {
                itemView.context.getString(R.string.passed)
            } else {
                itemView.context.getString(R.string.failed)
            }
            chipPassFail.text = passFailText

            // Set pass/fail color
            val passFailColor = if (session.isPassed) {
                itemView.context.getColor(R.color.status_pass)
            } else {
                itemView.context.getColor(R.color.status_fail)
            }
            chipPassFail.chipBackgroundColor = android.content.res.ColorStateList.valueOf(passFailColor)

            // Show/hide disqualified chip
            chipDisqualified.visibility = if (session.isDisqualified) View.VISIBLE else View.GONE

            // Set click listeners
            buttonViewDetails.setOnClickListener {
                onItemClicked(session)
            }

            itemView.setOnClickListener {
                onItemClicked(session)
            }
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