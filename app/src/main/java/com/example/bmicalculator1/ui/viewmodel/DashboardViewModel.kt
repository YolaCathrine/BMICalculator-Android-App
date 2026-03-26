package com.example.bmicalculator1.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bmicalculator1.data.local.entity.BmiRecordLocal
import com.example.bmicalculator1.data.local.entity.GoalLocal
import com.example.bmicalculator1.data.repository.BMIRepository
import com.example.bmicalculator1.data.repository.GoalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val bmiRepository: BMIRepository,
    private val goalRepository: GoalRepository
) : ViewModel() {

    private val _bmiRecords = MutableStateFlow<List<BmiRecordLocal>>(emptyList())
    val bmiRecords: StateFlow<List<BmiRecordLocal>> = _bmiRecords

    private val _goals = MutableStateFlow<List<GoalLocal>>(emptyList())
    val goals: StateFlow<List<GoalLocal>> = _goals

    private val _currentBmi = MutableLiveData<String>()
    val currentBmi: LiveData<String> = _currentBmi

    private val _bmiCategory = MutableLiveData<String>()
    val bmiCategory: LiveData<String> = _bmiCategory

    private val _chartData = MutableLiveData<List<Float>>()
    val chartData: LiveData<List<Float>> = _chartData

    fun loadRecords() {
        viewModelScope.launch {
            bmiRepository.getLocalRecords().collect { records ->
                _bmiRecords.value = records
                if (records.isNotEmpty()) {
                    val latest = records.first()
                    _currentBmi.value = latest.bmiValue
                    _bmiCategory.value = latest.category
                    _chartData.value = records.reversed().map { it.bmiValue.toFloat() }
                }
            }
        }
    }

    fun loadGoals() {
        viewModelScope.launch {
            goalRepository.getLocalGoals().collect { goals ->
                _goals.value = goals
            }
        }
    }

    fun getActiveGoalsCount(): Int {
        return _goals.value.count { it.status == "active" }
    }
}
