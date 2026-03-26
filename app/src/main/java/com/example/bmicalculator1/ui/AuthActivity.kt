package com.example.bmicalculator1.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bmicalculator1.data.local.AppDatabase
import com.example.bmicalculator1.data.remote.RetrofitClient
import com.example.bmicalculator1.data.repository.AuthRepository
import com.example.bmicalculator1.databinding.ActivityAuthBinding
import com.example.bmicalculator1.domain.PreferenceManager
import com.example.bmicalculator1.ui.MainActivity
import com.example.bmicalculator1.ui.viewmodel.AuthViewModel
import com.example.bmicalculator1.ui.viewmodel.AuthViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    private val viewModel: AuthViewModel by viewModels {
        val apiService = RetrofitClient.apiService
        val authRepo = AuthRepository(apiService)
        val preferenceManager = PreferenceManager(applicationContext)
        AuthViewModelFactory(authRepo, preferenceManager)
    }

    private val preferenceManager by lazy { PreferenceManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if already logged in
        checkAuthStatus()

        setupClickListeners()
        observeAuthState()
    }

    private fun checkAuthStatus() {
        lifecycleScope.launch {
            preferenceManager.isLoggedIn.collect { isLoggedIn ->
                if (isLoggedIn) {
                    navigateToMain()
                }
            }
        }
    }

    private fun setupClickListeners() {
        // Login form
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isNotBlank() && password.isNotBlank()) {
                viewModel.login(email, password)
            } else {
                Snackbar.make(binding.root, "Please enter email and password", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.tvRegister.setOnClickListener {
            binding.cardLogin.visibility = View.GONE
            binding.cardRegister.visibility = View.VISIBLE
        }

        // Register form
        binding.btnRegister.setOnClickListener {
            val email = binding.etRegisterEmail.text.toString()
            val password = binding.etRegisterPassword.text.toString()

            if (email.isNotBlank() && password.length >= 6) {
                viewModel.register(email, password)
            } else {
                Snackbar.make(binding.root, "Please enter valid email and password (min 6 chars)", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.tvLogin.setOnClickListener {
            binding.cardRegister.visibility = View.GONE
            binding.cardLogin.visibility = View.VISIBLE
        }
    }

    private fun observeAuthState() {
        viewModel.authState.observe(this) { state ->
            when (state) {
                is com.example.bmicalculator1.ui.viewmodel.AuthState.Loading -> {
                    binding.btnLogin.isEnabled = false
                    binding.btnRegister.isEnabled = false
                }
                is com.example.bmicalculator1.ui.viewmodel.AuthState.Success -> {
                    binding.btnLogin.isEnabled = true
                    binding.btnRegister.isEnabled = true
                    Snackbar.make(binding.root, state.data.message, Snackbar.LENGTH_SHORT).show()
                    navigateToMain()
                }
                is com.example.bmicalculator1.ui.viewmodel.AuthState.Error -> {
                    binding.btnLogin.isEnabled = true
                    binding.btnRegister.isEnabled = true
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
