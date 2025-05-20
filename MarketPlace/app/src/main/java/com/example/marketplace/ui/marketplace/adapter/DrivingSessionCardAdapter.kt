package com.example.marketplace.ui.marketplace.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.marketplace.R
import com.example.marketplace.data.DrivingSession
import com.example.marketplace.databinding.ItemDrivingSessionCardBinding
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
        val binding = ItemDrivingSessionCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * ViewHolder 클래스
     * 각 세션 카드의 UI 요소를 관리합니다.
     */
    inner class ViewHolder(private val binding: ItemDrivingSessionCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

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

            binding.textSessionDate.text = when {
                isSameDay(today, sessionDate) -> "오늘"
                isSameDay(yesterday, sessionDate) -> "어제"
                else -> dateFormat.format(session.date)
            }

            // 시간 표시
            binding.textSessionTime.text = timeFormat.format(session.date)

            // 점수 표시 및 색상 설정
            binding.textSessionScore.text = session.overallScore.toString()
            binding.textSessionScore.setTextColor(
                ScoreUtil.getColorForScore(itemView.context, session.overallScore)
            )

            // 합격/불합격 배지 설정
            when {
                session.isDisqualified -> {
                    binding.badgeResult.text = "실격"
                    binding.badgeResult.background = ContextCompat.getDrawable(
                        itemView.context, R.drawable.badge_red_background
                    )
                    binding.badgeResult.setTextColor(
                        ContextCompat.getColor(itemView.context, R.color.badge_red_text)
                    )
                }
                session.isPassed -> {
                    binding.badgeResult.text = "합격"
                    binding.badgeResult.background = ContextCompat.getDrawable(
                        itemView.context, R.drawable.badge_green_background
                    )
                    binding.badgeResult.setTextColor(
                        ContextCompat.getColor(itemView.context, R.color.badge_green_text)
                    )
                }
                else -> {
                    binding.badgeResult.text = "불합격"
                    binding.badgeResult.background = ContextCompat.getDrawable(
                        itemView.context, R.drawable.badge_red_background
                    )
                    binding.badgeResult.setTextColor(
                        ContextCompat.getColor(itemView.context, R.color.badge_red_text)
                    )
                }
            }

            // 소요 시간 배지
            binding.badgeDuration.text = "${session.durationMinutes}분"

            // 세부 점수 프로그레스 바 설정
            setupProgressBar(
                binding.progressSpeed,
                binding.textSpeedScore,
                session.speedScore
            )
            setupProgressBar(
                binding.progressTurning,
                binding.textTurningScore,
                session.turningScore
            )
            setupProgressBar(
                binding.progressBraking,
                binding.textBrakingScore,
                session.brakingScore
            )

            // 카드 클릭 리스너 설정
            binding.root.setOnClickListener {
                onItemClick(session)
            }
        }

        /**
         * 프로그레스 바와 점수 텍스트를 설정합니다.
         *
         * @param progressBar 설정할 프로그레스 바
         * @param textView 점수를 표시할 텍스트 뷰
         * @param score 점수 (0-100)
         */
        private fun setupProgressBar(
            progressBar: android.widget.ProgressBar,
            textView: android.widget.TextView,
            score: Int
        ) {
            progressBar.progress = score
            textView.text = score.toString()

            // 프로그레스 바 색상 설정
            val colorStateList = android.content.res.ColorStateList.valueOf(
                ScoreUtil.getColorForScore(itemView.context, score)
            )
            progressBar.progressTintList = colorStateList
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