package com.example.bmicalculator1.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.bmicalculator1.data.local.AppDatabase
import com.example.bmicalculator1.data.remote.RetrofitClient
import com.example.bmicalculator1.data.repository.BMIRepository
import com.example.bmicalculator1.data.repository.GoalRepository
import com.example.bmicalculator1.databinding.FragmentProfileBinding
import com.example.bmicalculator1.domain.PreferenceManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val preferenceManager by lazy { PreferenceManager(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadProfileData()
        loadStats()

        binding.btnLogout.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                preferenceManager.clearAuthData()
                // Navigate to login or restart app
                activity?.recreate()
            }
        }
    }

    private fun loadProfileData() {
        viewLifecycleOwner.lifecycleScope.launch {
            val email = preferenceManager.getEmail()
            val userId = preferenceManager.getUserId()

            email?.let {
                binding.tvProfileEmail.text = it
                binding.tvAccountEmail.text = it
            }

            userId?.let {
                // Show only first 8 characters of UUID
                binding.tvUserId.text = "${it.take(8)}..."
            }

            // Member since - would need to fetch from server or store locally
            binding.tvMemberSince.text = "Member since 2024"
        }
    }

    private fun loadStats() {
        val database = AppDatabase.getDatabase(requireContext())
        val apiService = RetrofitClient.apiService
        val bmiRepo = BMIRepository(apiService, database.bmiRecordDao())
        val goalRepo = GoalRepository(apiService, database.goalDao())

        viewLifecycleOwner.lifecycleScope.launch {
            // Total records
            bmiRepo.getLocalRecords().collect { records ->
                binding.tvStatsTotalRecords.text = records.size.toString()

                // Calculate average BMI
                if (records.isNotEmpty()) {
                    val avgBmi = records.map { it.bmiValue.toDoubleOrNull() ?: 0.0 }.average()
                    binding.tvStatsAvgBmi.text = String.format(Locale.getDefault(), "%.1f", avgBmi)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            goalRepo.getLocalGoals().collect { goals ->
                binding.tvStatsGoals.text = goals.size.toString()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
