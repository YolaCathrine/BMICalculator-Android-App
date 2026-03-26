package com.example.bmicalculator1.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bmicalculator1.data.repository.BMIRepository

class HistoryViewModelFactory(
    private val bmiRepository: BMIRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoryViewModel(bmiRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
