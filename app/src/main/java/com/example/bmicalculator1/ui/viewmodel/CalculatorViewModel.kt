package com.example.bmicalculator1.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bmicalculator1.data.local.AppDatabase
import com.example.bmicalculator1.data.local.entity.BmiRecordLocal
import com.example.bmicalculator1.data.model.BMIRecordResponse
import com.example.bmicalculator1.data.repository.BMIRepository
import com.example.bmicalculator1.domain.BMICalculator
import com.example.bmicalculator1.domain.BMIResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

sealed class BMIResultState {
    object Idle : BMIResultState()
    object Calculated : BMIResultState()
}

class CalculatorViewModel(
    private val bmiRepository: BMIRepository,
    private val bmiCalculator: BMICalculator
) : ViewModel() {

    private val _bmiResult = MutableLiveData<BMIResult?>()
    val bmiResult: LiveData<BMIResult?> = _bmiResult

    private val _resultState = MutableLiveData<BMIResultState>()
    val resultState: LiveData<BMIResultState> = _resultState

    private val _saveState = MutableLiveData<Boolean>()
    val saveState: LiveData<Boolean> = _saveState

    private val _saveMessage = MutableLiveData<String>()
    val saveMessage: LiveData<String> = _saveMessage

    private val _localRecords = MutableStateFlow<List<BmiRecordLocal>>(emptyList())
    val localRecords: StateFlow<List<BmiRecordLocal>> = _localRecords

    fun calculateBmi(weight: Double, height: Double) {
        val result = bmiCalculator.calculate(weight, height)
        _bmiResult.value = result
        _resultState.value = BMIResultState.Calculated
    }

    fun reset() {
        _bmiResult.value = null
        _resultState.value = BMIResultState.Idle
    }

    fun saveRecord(token: String, weight: Double, height: Double) {
        viewModelScope.launch {
            val result = _bmiResult.value
            if (result == null) {
                _saveState.value = false
                _saveMessage.value = "No BMI result to save"
                return@launch
            }
            
            try {
                val apiResult = bmiRepository.createRecord(token, weight, height)
                if (apiResult.isSuccess) {
                    _saveState.value = true
                    _saveMessage.value = "Saved successfully!"
                } else {
                    _saveState.value = false
                    val errorMsg = apiResult.exceptionOrNull()?.message ?: "Unknown error"
                    _saveMessage.value = "Failed: $errorMsg"
                    Log.e("CalculatorViewModel", "Save error: $errorMsg")
                }
            } catch (e: Exception) {
                _saveState.value = false
                _saveMessage.value = "Error: ${e.message}"
                Log.e("CalculatorViewModel", "Save exception: ${e.message}", e)
            }
        }
    }

    fun saveToLocal(weight: Double, height: Double, bmi: Double, category: String) {
        viewModelScope.launch {
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                val record = BmiRecordLocal(
                    id = System.currentTimeMillis().toString(),
                    userId = "local",
                    weight = weight,
                    height = height,
                    bmiValue = String.format("%.2f", bmi),
                    category = category,
                    recordedAt = dateFormat.format(java.util.Date()),
                    isSynced = false
                )
                
                // Save to local database through repository
                bmiRepository.saveLocal(record)
                
                _saveState.value = true
                _saveMessage.value = "Saved locally!"
                Log.d("CalculatorViewModel", "Record saved to local database")
            } catch (e: Exception) {
                _saveState.value = false
                _saveMessage.value = "Local save error: ${e.message}"
                Log.e("CalculatorViewModel", "Local save exception: ${e.message}", e)
            }
        }
    }

    // Save to backend asynchronously (fire and forget)
    fun saveRecordToBackendAsync(token: String, weight: Double, height: Double) {
        viewModelScope.launch {
            try {
                val result = _bmiResult.value ?: return@launch
                val apiResult = bmiRepository.createRecord(token, weight, height)
                if (apiResult.isSuccess) {
                    Log.d("CalculatorViewModel", "Successfully synced to backend")
                } else {
                    Log.w("CalculatorViewModel", "Backend sync failed: ${apiResult.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                Log.w("CalculatorViewModel", "Backend sync exception: ${e.message}")
                // Don't update UI state, this is optional
            }
        }
    }

    fun observeLocalRecords() {
        viewModelScope.launch {
            bmiRepository.getLocalRecords().collect { records ->
                _localRecords.value = records
            }
        }
    }
}
