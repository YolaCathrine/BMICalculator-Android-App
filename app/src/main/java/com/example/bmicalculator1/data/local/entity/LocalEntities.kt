package com.example.bmicalculator1.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bmi_records_local")
data class BmiRecordLocal(
    @PrimaryKey val id: String,
    val userId: String,
    val weight: Double,
    val height: Double,
    val bmiValue: String,
    val category: String,
    val recordedAt: String,
    val isSynced: Boolean = false
)

@Entity(tableName = "goals_local")
data class GoalLocal(
    @PrimaryKey val id: String,
    val userId: String,
    val targetWeight: Double,
    val targetBmi: Double?,
    val goalDate: String?,
    val currentWeight: Double?,
    val status: String,
    val createdAt: String,
    val updatedAt: String,
    val isSynced: Boolean = false
)
