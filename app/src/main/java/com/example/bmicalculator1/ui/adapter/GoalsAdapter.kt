package com.example.bmicalculator1.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bmicalculator1.R
import com.example.bmicalculator1.data.local.entity.GoalLocal
import com.example.bmicalculator1.databinding.ItemGoalBinding

class GoalsAdapter(
    private val onEditClick: (GoalLocal) -> Unit,
    private val onDeleteClick: (GoalLocal) -> Unit
) : ListAdapter<GoalLocal, GoalsAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGoalBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onEditClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemGoalBinding,
        private val onEditClick: (GoalLocal) -> Unit,
        private val onDeleteClick: (GoalLocal) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(goal: GoalLocal) {
            binding.tvItemTargetWeight.text = "${goal.targetWeight} kg"
            binding.tvItemCurrentWeight.text = "${goal.currentWeight ?: "--"} kg"
            binding.tvItemGoalStatus.text = goal.status.replaceFirstChar { it.uppercase() }
            
            // Goal date
            goal.goalDate?.let {
                binding.tvItemGoalDate.text = "Target: $it"
            } ?: run {
                binding.tvItemGoalDate.text = "No target date"
            }

            // Calculate progress
            val progress = if (goal.currentWeight != null && goal.targetWeight > 0) {
                ((goal.currentWeight / goal.targetWeight) * 100).toInt().coerceIn(0, 100)
            } else {
                0
            }
            binding.progressGoal.progress = progress
            binding.tvItemProgressPercent.text = "$progress%"

            // Status indicator color
            val statusColor = if (goal.status == "active") R.color.success else R.color.on_surface_variant
            binding.viewStatusIndicator.backgroundTintList =
                ContextCompat.getColorStateList(binding.root.context, statusColor)
            binding.tvItemGoalStatus.setTextColor(
                ContextCompat.getColor(binding.root.context, statusColor)
            )

            // Click listeners
            binding.btnEditGoal.setOnClickListener { onEditClick(goal) }
            binding.btnDeleteGoal.setOnClickListener { onDeleteClick(goal) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<GoalLocal>() {
        override fun areItemsTheSame(oldItem: GoalLocal, newItem: GoalLocal): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GoalLocal, newItem: GoalLocal): Boolean {
            return oldItem == newItem
        }
    }
}
