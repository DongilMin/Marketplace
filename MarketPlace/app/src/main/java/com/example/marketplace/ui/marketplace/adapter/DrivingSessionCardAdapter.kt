package com.example.marketplace.ui.marketplace.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.marketplace.R
import com.example.marketplace.data.DrivingSession
import com.example.marketplace.util.ScoreUtil
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * 드라이빙 세션 카드 어댑터
 * RecyclerView에 드라이빙 세션을 카드 형태로 표시합니다.
 *
 * @param onItemClick 카드 클릭 시 호출되는 콜백 함수
 */
class DrivingSessionCardAdapter(
    private val onItemClick: (DrivingSession) -> Unit
) : ListAdapter<DrivingSession, DrivingSessionCardAdapter.ViewHolder>(DiffCallback()) {

    private val dateFormat = SimpleDateFormat("MM월 dd일", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("오전/오후 h:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_driving_session_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * ViewHolder 클래스
     * 각 세션 카드의 UI 요소를 관리합니다.
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // View 요소 초기화 - 레이아웃 ID와 일치시킴
        private val textSessionDate: TextView = itemView.findViewById(R.id.text_session_date)
        private val textSessionTime: TextView = itemView.findViewById(R.id.text_session_time)
        private val textSessionScore: TextView = itemView.findViewById(R.id.text_session_score)
        // badge_status로 변경 (badge_result가 아님)
        private val badgeStatus: TextView = itemView.findViewById(R.id.badge_status)
        private val badgeDuration: TextView = itemView.findViewById(R.id.badge_duration)
        private val iconTrend: ImageView = itemView.findViewById(R.id.icon_trend)

        /**
         * 드라이빙 세션 데이터를 UI에 바인딩합니다.
         *
         * @param session 바인딩할 드라이빙 세션 데이터
         */
        fun bind(session: DrivingSession) {
            // 날짜 표시 (오늘, 어제, 또는 날짜)
            val today = Calendar.getInstance()
            val sessionDate = Calendar.getInstance().apply { time = session.date }
            val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

            textSessionDate.text = when {
                isSameDay(today, sessionDate) -> "오늘"
                isSameDay(yesterday, sessionDate) -> "어제"
                else -> dateFormat.format(session.date)
            }

            // 시간 표시
            textSessionTime.text = timeFormat.format(session.date)

            // 점수 표시 및 색상 설정
            textSessionScore.text = session.overallScore.toString()
            textSessionScore.setTextColor(
                ScoreUtil.getColorForScore(itemView.context, session.overallScore)
            )

            // 합격/불합격 배지 설정 - badgeResult가 아닌 badgeStatus 사용
            when {
                session.isDisqualified -> {
                    badgeStatus.text = "실격"
                    badgeStatus.background = ContextCompat.getDrawable(
                        itemView.context, R.drawable.badge_red_background
                    )
                    badgeStatus.setTextColor(
                        ContextCompat.getColor(itemView.context, R.color.badge_red_text)
                    )
                }
                session.isPassed -> {
                    badgeStatus.text = "합격"
                    badgeStatus.background = ContextCompat.getDrawable(
                        itemView.context, R.drawable.badge_green_background
                    )
                    badgeStatus.setTextColor(
                        ContextCompat.getColor(itemView.context, R.color.badge_green_text)
                    )
                }
                else -> {
                    badgeStatus.text = "불합격"
                    badgeStatus.background = ContextCompat.getDrawable(
                        itemView.context, R.drawable.badge_red_background
                    )
                    badgeStatus.setTextColor(
                        ContextCompat.getColor(itemView.context, R.color.badge_red_text)
                    )
                }
            }

            // 소요 시간 배지
            badgeDuration.text = "${session.durationMinutes}분"

            // 트렌드 아이콘 색상 설정
            iconTrend.setColorFilter(
                ContextCompat.getColor(itemView.context, R.color.score_excellent)
            )

            // 카드 클릭 리스너 설정
            itemView.setOnClickListener {
                onItemClick(session)
            }
        }

        /**
         * 두 Calendar 객체가 같은 날인지 확인합니다.
         *
         * @param cal1 첫 번째 Calendar
         * @param cal2 두 번째 Calendar
         * @return 같은 날이면 true, 아니면 false
         */
        private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
        }
    }

    /**
     * DiffUtil.ItemCallback 구현
     * 리스트 아이템의 변경 사항을 효율적으로 처리합니다.
     */
    private class DiffCallback : DiffUtil.ItemCallback<DrivingSession>() {
        override fun areItemsTheSame(oldItem: DrivingSession, newItem: DrivingSession): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DrivingSession, newItem: DrivingSession): Boolean {
            return oldItem == newItem
        }
    }
}