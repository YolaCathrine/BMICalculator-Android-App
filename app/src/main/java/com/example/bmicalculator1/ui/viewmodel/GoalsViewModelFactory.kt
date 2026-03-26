package com.example.bmicalculator1.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bmicalculator1.data.repository.GoalRepository

class GoalsViewModelFactory(
    private val goalRepository: GoalRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GoalsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GoalsViewModel(goalRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
