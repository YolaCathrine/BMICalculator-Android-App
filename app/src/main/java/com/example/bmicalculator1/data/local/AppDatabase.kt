package com.example.bmicalculator1.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bmicalculator1.data.local.dao.BmiRecordDao
import com.example.bmicalculator1.data.local.dao.GoalDao
import com.example.bmicalculator1.data.local.entity.BmiRecordLocal
import com.example.bmicalculator1.data.local.entity.GoalLocal

@Database(
    entities = [BmiRecordLocal::class, GoalLocal::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bmiRecordDao(): BmiRecordDao
    abstract fun goalDao(): GoalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bmi_calculator_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
