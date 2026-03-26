package com.example.bmicalculator1.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bmicalculator1.data.local.entity.GoalLocal
import com.example.bmicalculator1.data.repository.GoalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GoalsViewModel(
    private val goalRepository: GoalRepository
) : ViewModel() {

    private val _goals = MutableStateFlow<List<GoalLocal>>(emptyList())
    val goals: StateFlow<List<GoalLocal>> = _goals

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _createSuccess = MutableLiveData<Boolean>()
    val createSuccess: LiveData<Boolean> = _createSuccess

    private val _deleteSuccess = MutableLiveData<Boolean>()
    val deleteSuccess: LiveData<Boolean> = _deleteSuccess

    fun loadGoals() {
        viewModelScope.launch {
            goalRepository.getLocalGoals().collect { goals ->
                _goals.value = goals
            }
        }
    }

    fun createGoal(
        token: String,
        targetWeight: Double,
        targetBmi: Double? = null,
        goalDate: String? = null,
        currentWeight: Double? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = goalRepository.createGoal(token, targetWeight, targetBmi, goalDate, currentWeight)
            _createSuccess.value = result.isSuccess
            _isLoading.value = false
        }
    }

    fun deleteGoal(token: String, goalId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = goalRepository.deleteGoal(token, goalId)
            _deleteSuccess.value = result.isSuccess
            _isLoading.value = false
        }
    }
}
