package com.example.bmicalculator1.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bmicalculator1.data.repository.BMIRepository
import com.example.bmicalculator1.domain.BMICalculator

class CalculatorViewModelFactory(
    private val bmiRepository: BMIRepository,
    private val bmiCalculator: BMICalculator
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalculatorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalculatorViewModel(bmiRepository, bmiCalculator) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
