package com.example.marketplace.ui.statistics.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.marketplace.databinding.ItemTopDeductionBinding
import com.example.marketplace.ui.statistics.TopDeductionItem

/**
 * TOP 감점 항목 어댑터
 * TOP 3 감점 항목을 RecyclerView에 표시합니다.
 */
class TopDeductionAdapter : ListAdapter<TopDeductionItem, TopDeductionAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTopDeductionBinding.inflate(
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
     * 각 TOP 감점 항목의 UI 요소를 관리합니다.
     */
    inner class ViewHolder(private val binding: ItemTopDeductionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * TOP 감점 항목 데이터를 UI에 바인딩합니다.
         *
         * @param item 바인딩할 TOP 감점 항목 데이터
         */
        fun bind(item: TopDeductionItem) {
            // 순위 표시
            binding.textRank.text = item.rank.toString()

            // 감점 사유
            binding.textDeductionReason.text = item.reason

            // 발생 횟수
            binding.textOccurrenceCount.text = "${item.occurrenceCount}회 발생"

            // 총 감점
            binding.textTotalPointsDeducted.text = "-${item.totalPointsDeducted}"
        }
    }

    /**
     * DiffUtil.ItemCallback 구현
     * 리스트 아이템의 변경 사항을 효율적으로 처리합니다.
     */
    private class DiffCallback : DiffUtil.ItemCallback<TopDeductionItem>() {
        override fun areItemsTheSame(oldItem: TopDeductionItem, newItem: TopDeductionItem): Boolean {
            return oldItem.rank == newItem.rank
        }

        override fun areContentsTheSame(oldItem: TopDeductionItem, newItem: TopDeductionItem): Boolean {
            return oldItem == newItem
        }
    }
}