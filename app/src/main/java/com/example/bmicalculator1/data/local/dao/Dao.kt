package com.example.bmicalculator1.data.local.dao

import androidx.room.*
import com.example.bmicalculator1.data.local.entity.BmiRecordLocal
import com.example.bmicalculator1.data.local.entity.GoalLocal
import kotlinx.coroutines.flow.Flow

@Dao
interface BmiRecordDao {
    @Query("SELECT * FROM bmi_records_local ORDER BY recordedAt DESC")
    fun getAllRecords(): Flow<List<BmiRecordLocal>>

    @Query("SELECT * FROM bmi_records_local WHERE id = :id")
    suspend fun getRecordById(id: String): BmiRecordLocal?

    @Query("SELECT * FROM bmi_records_local WHERE isSynced = 0")
    suspend fun getUnsyncedRecords(): List<BmiRecordLocal>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: BmiRecordLocal)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecords(records: List<BmiRecordLocal>)

    @Delete
    suspend fun deleteRecord(record: BmiRecordLocal)

    @Query("DELETE FROM bmi_records_local WHERE id = :id")
    suspend fun deleteRecordById(id: String)

    @Query("UPDATE bmi_records_local SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)
}

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals_local ORDER BY createdAt DESC")
    fun getAllGoals(): Flow<List<GoalLocal>>

    @Query("SELECT * FROM goals_local WHERE id = :id")
    suspend fun getGoalById(id: String): GoalLocal?

    @Query("SELECT * FROM goals_local WHERE isSynced = 0")
    suspend fun getUnsyncedGoals(): List<GoalLocal>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalLocal)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoals(goals: List<GoalLocal>)

    @Delete
    suspend fun deleteGoal(goal: GoalLocal)

    @Query("DELETE FROM goals_local WHERE id = :id")
    suspend fun deleteGoalById(id: String)

    @Query("UPDATE goals_local SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)
}
