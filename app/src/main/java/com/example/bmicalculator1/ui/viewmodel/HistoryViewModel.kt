package com.example.bmicalculator1.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bmicalculator1.data.local.entity.BmiRecordLocal
import com.example.bmicalculator1.data.repository.BMIRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val bmiRepository: BMIRepository
) : ViewModel() {

    private val _records = MutableStateFlow<List<BmiRecordLocal>>(emptyList())
    val records: StateFlow<List<BmiRecordLocal>> = _records

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _deleteSuccess = MutableLiveData<Boolean>()
    val deleteSuccess: LiveData<Boolean> = _deleteSuccess

    fun loadRecords() {
        viewModelScope.launch {
            bmiRepository.getLocalRecords().collect { records ->
                Log.d("HistoryViewModel", "Loaded ${records.size} records from local database")
                _records.value = records
            }
        }
    }

    fun deleteRecord(token: String, recordId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // First delete from remote
                val remoteResult = bmiRepository.deleteRecord(token, recordId)
                
                if (remoteResult.isSuccess) {
                    Log.d("HistoryViewModel", "Deleted from remote successfully")
                    _deleteSuccess.value = true
                } else {
                    Log.w("HistoryViewModel", "Remote delete failed, deleting local only")
                    // If remote fails, still delete from local
                    val localResult = bmiRepository.deleteLocalRecord(recordId)
                    _deleteSuccess.value = localResult
                }
            } catch (e: Exception) {
                Log.e("HistoryViewModel", "Delete exception: ${e.message}", e)
                // Still try to delete from local
                try {
                    bmiRepository.deleteLocalRecord(recordId)
                    _deleteSuccess.value = true
                } catch (e2: Exception) {
                    _deleteSuccess.value = false
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteLocalRecord(record: BmiRecordLocal) {
        viewModelScope.launch {
            try {
                Log.d("HistoryViewModel", "Deleting local record: ${record.id}")
                bmiRepository.deleteLocalRecord(record.id)
                _deleteSuccess.value = true
            } catch (e: Exception) {
                Log.e("HistoryViewModel", "Local delete exception: ${e.message}", e)
                _deleteSuccess.value = false
            }
        }
    }
}
