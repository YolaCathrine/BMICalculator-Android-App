package com.example.bmicalculator1.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bmicalculator1.data.repository.BMIRepository
import com.example.bmicalculator1.data.repository.GoalRepository

class DashboardViewModelFactory(
    private val bmiRepository: BMIRepository,
    private val goalRepository: GoalRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(bmiRepository, goalRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
