package com.example.bmicalculator1.data.remote

import com.example.bmicalculator1.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // Authentication
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("api/auth/profile")
    suspend fun getProfile(@Header("Authorization") token: String): Response<UserData>

    // BMI Records
    @GET("api/bmi")
    suspend fun getBmiRecords(@Header("Authorization") token: String): Response<BMIRecordsResponse>

    @GET("api/bmi/{id}")
    suspend fun getBmiRecord(
        @Header("Authorization") token: String,
        @Path("id") recordId: String
    ): Response<BMIRecordResponse>

    @POST("api/bmi")
    suspend fun createBmiRecord(
        @Header("Authorization") token: String,
        @Body request: BMIRecordRequest
    ): Response<BMIRecordResponse>

    @DELETE("api/bmi/{id}")
    suspend fun deleteBmiRecord(
        @Header("Authorization") token: String,
        @Path("id") recordId: String
    ): Response<Unit>

    @GET("api/bmi/stats/history")
    suspend fun getBmiHistory(
        @Header("Authorization") token: String
    ): Response<Map<String, Any>>

    // Goals
    @GET("api/goals")
    suspend fun getGoals(@Header("Authorization") token: String): Response<GoalsResponse>

    @GET("api/goals/{id}")
    suspend fun getGoal(
        @Header("Authorization") token: String,
        @Path("id") goalId: String
    ): Response<GoalResponse>

    @POST("api/goals")
    suspend fun createGoal(
        @Header("Authorization") token: String,
        @Body request: GoalRequest
    ): Response<GoalResponse>

    @PUT("api/goals/{id}")
    suspend fun updateGoal(
        @Header("Authorization") token: String,
        @Path("id") goalId: String,
        @Body request: GoalRequest
    ): Response<GoalResponse>

    @DELETE("api/goals/{id}")
    suspend fun deleteGoal(
        @Header("Authorization") token: String,
        @Path("id") goalId: String
    ): Response<Unit>
}
