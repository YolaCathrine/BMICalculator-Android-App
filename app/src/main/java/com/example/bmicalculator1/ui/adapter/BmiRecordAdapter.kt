package com.example.bmicalculator1.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bmicalculator1.R
import com.example.bmicalculator1.data.local.entity.BmiRecordLocal
import com.example.bmicalculator1.databinding.ItemBmiRecordBinding
import java.text.SimpleDateFormat
import java.util.Locale

class BmiRecordAdapter(
    private val onDeleteClick: (BmiRecordLocal) -> Unit
) : ListAdapter<BmiRecordLocal, BmiRecordAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBmiRecordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onDeleteClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemBmiRecordBinding,
        private val onDeleteClick: (BmiRecordLocal) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(record: BmiRecordLocal) {
            binding.tvItemBmi.text = record.bmiValue
            binding.tvItemCategory.text = record.category
            binding.tvItemWeight.text = "${record.weight} kg"
            binding.tvItemHeight.text = "${record.height} cm"

            // Format date
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val date = try {
                dateFormat.format(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(record.recordedAt) ?: java.util.Date())
            } catch (e: Exception) {
                record.recordedAt
            }
            binding.tvItemDate.text = date

            // Set circle color based on category
            val circleColor = when (record.category.lowercase()) {
                "underweight" -> R.color.bmi_underweight
                "normal weight" -> R.color.bmi_normal
                "overweight" -> R.color.bmi_overweight
                else -> R.color.bmi_obese
            }
            binding.tvItemBmi.backgroundTintList =
                ContextCompat.getColorStateList(binding.root.context, circleColor)

            binding.btnDeleteRecord.setOnClickListener {
                onDeleteClick(record)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<BmiRecordLocal>() {
        override fun areItemsTheSame(oldItem: BmiRecordLocal, newItem: BmiRecordLocal): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BmiRecordLocal, newItem: BmiRecordLocal): Boolean {
            return oldItem == newItem
        }
    }
}
