package com.example.bmicalculator1.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bmicalculator1.data.model.AuthResponse
import com.example.bmicalculator1.data.repository.AuthRepository
import com.example.bmicalculator1.domain.PreferenceManager
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val data: AuthResponse) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.login(email, password)
            
            result.onSuccess { response ->
                preferenceManager.saveAuthData(
                    token = response.token,
                    userId = response.user.id,
                    email = response.user.email
                )
                _authState.value = AuthState.Success(response)
            }.onFailure { error ->
                _authState.value = AuthState.Error(error.message ?: "Login failed")
            }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.register(email, password)
            
            result.onSuccess { response ->
                preferenceManager.saveAuthData(
                    token = response.token,
                    userId = response.user.id,
                    email = response.user.email
                )
                _authState.value = AuthState.Success(response)
            }.onFailure { error ->
                _authState.value = AuthState.Error(error.message ?: "Registration failed")
            }
        }
    }
}
