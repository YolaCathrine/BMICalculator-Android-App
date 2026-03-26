package com.example.bmicalculator1.data.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class AuthResponse(
    @SerializedName("message") val message: String,
    @SerializedName("user") val user: UserData,
    @SerializedName("token") val token: String
)

data class UserData(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName("createdAt") val createdAt: String
)

data class BMIRecordRequest(
    @SerializedName("weight") val weight: Double,
    @SerializedName("height") val height: Double
)

data class BMIRecordResponse(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("weight") val weight: Double,
    @SerializedName("height") val height: Double,
    @SerializedName("bmi_value") val bmiValue: String,
    @SerializedName("category") val category: String,
    @SerializedName("recorded_at") val recordedAt: String
)

data class BMIRecordsResponse(
    @SerializedName("records") val records: List<BMIRecordResponse>
)

data class GoalRequest(
    @SerializedName("target_weight") val targetWeight: Double,
    @SerializedName("target_bmi") val targetBmi: Double? = null,
    @SerializedName("goal_date") val goalDate: String? = null,
    @SerializedName("current_weight") val currentWeight: Double? = null
)

data class GoalResponse(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("target_weight") val targetWeight: Double,
    @SerializedName("target_bmi") val targetBmi: Double?,
    @SerializedName("goal_date") val goalDate: String?,
    @SerializedName("current_weight") val currentWeight: Double?,
    @SerializedName("status") val status: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

data class GoalsResponse(
    @SerializedName("goals") val goals: List<GoalResponse>
)

data class ApiError(
    @SerializedName("error") val error: String
)
