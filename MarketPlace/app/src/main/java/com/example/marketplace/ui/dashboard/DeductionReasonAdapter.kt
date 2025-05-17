package com.example.marketplace.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.marketplace.R
import com.example.marketplace.data.DeductionReason
import com.example.marketplace.data.DeductionSeverity
import com.example.marketplace.util.ScoreUtil
import com.google.android.material.chip.Chip

/**
 * Adapter for displaying deduction reason items in a RecyclerView
 *
 * @property deductionList List of deduction reasons to display
 */
class DeductionReasonAdapter(
    private val deductionList: List<DeductionReason>
) : RecyclerView.Adapter<DeductionReasonAdapter.ViewHolder>() {

    /**
     * ViewHolder for deduction reason items
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textReason: TextView = itemView.findViewById(R.id.text_reason)
        private val chipSeverity: Chip = itemView.findViewById(R.id.chip_severity)
        private val textPoints: TextView = itemView.findViewById(R.id.text_points)

        /**
         * Binds deduction reason data to the view
         */
        fun bind(deduction: DeductionReason) {
            // Set reason text
            textReason.text = deduction.reason

            // Set severity chip
            val severityText = when (deduction.severity) {
                DeductionSeverity.MINOR -> itemView.context.getString(R.string.minor)
                DeductionSeverity.MAJOR -> itemView.context.getString(R.string.major)
                DeductionSeverity.CRITICAL -> itemView.context.getString(R.string.critical)
            }
            chipSeverity.text = severityText

            // Set severity chip color
            val severityColor = ScoreUtil.getColorForSeverity(itemView.context, deduction.severity)
            chipSeverity.chipBackgroundColor = android.content.res.ColorStateList.valueOf(severityColor)

            // Set points text
            textPoints.text = "-${deduction.pointsDeducted}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_deduction_reason, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(deductionList[position])
    }

    override fun getItemCount() = deductionList.size
}