package com.example.bmicalculator1.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bmicalculator1.data.local.dao.BmiRecordDao
import com.example.bmicalculator1.data.local.dao.GoalDao
import com.example.bmicalculator1.data.local.entity.BmiRecordLocal
import com.example.bmicalculator1.data.local.entity.GoalLocal
import com.example.bmicalculator1.data.model.*
import com.example.bmicalculator1.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AuthRepository(
    private val apiService: ApiService
) {
    suspend fun register(email: String, password: String): Result<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.register(RegisterRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Registration failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class BMIRepository(
    private val apiService: ApiService,
    private val bmiRecordDao: BmiRecordDao
) {
    fun getLocalRecords(): Flow<List<BmiRecordLocal>> = bmiRecordDao.getAllRecords()

    suspend fun getRemoteRecords(token: String): Result<List<BMIRecordResponse>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getBmiRecords("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.records)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to fetch records"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createRecord(token: String, weight: Double, height: Double): Result<BMIRecordResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.createBmiRecord("Bearer $token", BMIRecordRequest(weight, height))
                if (response.isSuccessful && response.body() != null) {
                    // Save to local database
                    val record = response.body()!!
                    bmiRecordDao.insertRecord(
                        BmiRecordLocal(
                            id = record.id,
                            userId = record.userId,
                            weight = record.weight,
                            height = record.height,
                            bmiValue = record.bmiValue,
                            category = record.category,
                            recordedAt = record.recordedAt,
                            isSynced = true
                        )
                    )
                    Result.success(record)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Failed to create record"
                    Log.e("BMIRepository", "API Error: $errorBody")
                    Result.failure(Exception(errorBody))
                }
            } catch (e: Exception) {
                Log.e("BMIRepository", "Create record exception: ${e.message}", e)
                Result.failure(e)
            }
        }

    suspend fun saveLocal(record: BmiRecordLocal) {
        try {
            bmiRecordDao.insertRecord(record)
            Log.d("BMIRepository", "Record saved to local database: ${record.id}")
        } catch (e: Exception) {
            Log.e("BMIRepository", "Save local exception: ${e.message}", e)
            throw e
        }
    }

    suspend fun deleteRecord(token: String, recordId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deleteBmiRecord("Bearer $token", recordId)
            if (response.isSuccessful) {
                // Also delete from local database
                bmiRecordDao.deleteRecordById(recordId)
                Log.d("BMIRepository", "Deleted record from remote and local: $recordId")
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Failed to delete record"
                Log.e("BMIRepository", "Remote delete failed: $errorBody")
                Result.failure(Exception(errorBody))
            }
        } catch (e: Exception) {
            Log.e("BMIRepository", "Delete record exception: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun deleteLocalRecord(recordId: String): Boolean {
        return try {
            bmiRecordDao.deleteRecordById(recordId)
            Log.d("BMIRepository", "Deleted local record: $recordId")
            true
        } catch (e: Exception) {
            Log.e("BMIRepository", "Delete local record exception: ${e.message}", e)
            false
        }
    }
}

class GoalRepository(
    private val apiService: ApiService,
    private val goalDao: GoalDao
) {
    fun getLocalGoals(): Flow<List<GoalLocal>> = goalDao.getAllGoals()

    suspend fun getRemoteGoals(token: String): Result<List<GoalResponse>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getGoals("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.goals)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to fetch goals"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createGoal(
        token: String,
        targetWeight: Double,
        targetBmi: Double? = null,
        goalDate: String? = null,
        currentWeight: Double? = null
    ): Result<GoalResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createGoal(
                "Bearer $token",
                GoalRequest(targetWeight, targetBmi, goalDate, currentWeight)
            )
            if (response.isSuccessful && response.body() != null) {
                val goal = response.body()!!
                goalDao.insertGoal(
                    GoalLocal(
                        id = goal.id,
                        userId = goal.userId,
                        targetWeight = goal.targetWeight,
                        targetBmi = goal.targetBmi,
                        goalDate = goal.goalDate,
                        currentWeight = goal.currentWeight,
                        status = goal.status,
                        createdAt = goal.createdAt,
                        updatedAt = goal.updatedAt,
                        isSynced = true
                    )
                )
                Result.success(goal)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to create goal"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateGoal(
        token: String,
        goalId: String,
        targetWeight: Double? = null,
        targetBmi: Double? = null,
        goalDate: String? = null,
        currentWeight: Double? = null,
        status: String? = null
    ): Result<GoalResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateGoal(
                "Bearer $token",
                goalId,
                GoalRequest(targetWeight ?: 0.0, targetBmi, goalDate, currentWeight)
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to update goal"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteGoal(token: String, goalId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deleteGoal("Bearer $token", goalId)
            if (response.isSuccessful) {
                goalDao.deleteGoalById(goalId)
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to delete goal"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
