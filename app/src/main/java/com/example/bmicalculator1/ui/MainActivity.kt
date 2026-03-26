package com.example.bmicalculator1.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.bmicalculator1.R
import com.example.bmicalculator1.databinding.ActivityMainBinding
import com.example.bmicalculator1.domain.PreferenceManager
import com.example.bmicalculator1.ui.AuthActivity
import com.example.bmicalculator1.ui.fragment.CalculatorFragment
import com.example.bmicalculator1.ui.fragment.DashboardFragment
import com.example.bmicalculator1.ui.fragment.GoalsFragment
import com.example.bmicalculator1.ui.fragment.HistoryFragment
import com.example.bmicalculator1.ui.fragment.ProfileFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val preferenceManager by lazy { PreferenceManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check authentication
        checkAuthStatus()

        // Set default fragment
        if (savedInstanceState == null) {
            loadFragment(DashboardFragment())
        }

        // Setup bottom navigation
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_dashboard -> {
                    loadFragment(DashboardFragment())
                    true
                }
                R.id.navigation_calculator -> {
                    loadFragment(CalculatorFragment())
                    true
                }
                R.id.navigation_history -> {
                    loadFragment(HistoryFragment())
                    true
                }
                R.id.navigation_goals -> {
                    loadFragment(GoalsFragment())
                    true
                }
                R.id.navigation_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun checkAuthStatus() {
        CoroutineScope(Dispatchers.Main).launch {
            preferenceManager.isLoggedIn.collect { isLoggedIn ->
                if (!isLoggedIn) {
                    // Navigate to login
                    startActivity(Intent(this@MainActivity, AuthActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
