package com.example.bmicalculator1.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.bmicalculator1.R
import com.example.bmicalculator1.data.local.AppDatabase
import com.example.bmicalculator1.data.remote.RetrofitClient
import com.example.bmicalculator1.data.repository.BMIRepository
import com.example.bmicalculator1.databinding.FragmentCalculatorBinding
import com.example.bmicalculator1.domain.BMICalculator
import com.example.bmicalculator1.domain.PreferenceManager
import com.example.bmicalculator1.ui.viewmodel.CalculatorViewModel
import com.example.bmicalculator1.ui.viewmodel.CalculatorViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class CalculatorFragment : Fragment() {

    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CalculatorViewModel by viewModels {
        val database = AppDatabase.getDatabase(requireContext())
        val apiService = RetrofitClient.apiService
        val bmiRepo = BMIRepository(apiService, database.bmiRecordDao())
        CalculatorViewModelFactory(bmiRepo, BMICalculator())
    }

    private val preferenceManager by lazy { PreferenceManager(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeLocalRecords()

        binding.btnCalculate.setOnClickListener {
            val weight = binding.etWeight.text.toString().toDoubleOrNull()
            val height = binding.etHeight.text.toString().toDoubleOrNull()

            if (weight != null && height != null && weight > 0 && height > 0) {
                viewModel.calculateBmi(weight, height)
                Log.d("CalculatorFragment", "BMI calculated: ${viewModel.bmiResult.value?.bmi}")
            } else {
                Snackbar.make(binding.root, "Please enter valid weight and height", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.btnReset.setOnClickListener {
            binding.etWeight.text?.clear()
            binding.etHeight.text?.clear()
            binding.cardResult.visibility = View.GONE
            viewModel.reset()
        }

        binding.btnSaveRecord.setOnClickListener {
            saveBmiRecord()
        }

        observeResult()
        observeSaveState()
    }

    private fun saveBmiRecord() {
        val weightStr = binding.etWeight.text.toString()
        val heightStr = binding.etHeight.text.toString()
        
        Log.d("CalculatorFragment", "=== SAVE CLICKED ===")
        Log.d("CalculatorFragment", "Weight: '$weightStr', Height: '$heightStr'")
        
        val weight = weightStr.toDoubleOrNull()
        val height = heightStr.toDoubleOrNull()

        if (weight == null || height == null || weight <= 0 || height <= 0) {
            Snackbar.make(binding.root, "Please enter valid weight and height", Snackbar.LENGTH_SHORT).show()
            Log.e("CalculatorFragment", "Invalid weight or height")
            return
        }

        // Get BMI result
        val bmiResult = viewModel.bmiResult.value
        
        Log.d("CalculatorFragment", "BMI Result: ${bmiResult?.bmi ?: "NULL"}")
        Log.d("CalculatorFragment", "BMI Category: ${bmiResult?.category ?: "NULL"}")
        
        if (bmiResult == null) {
            Snackbar.make(binding.root, "Please calculate BMI first!", Snackbar.LENGTH_LONG).show()
            Log.e("CalculatorFragment", "BMI result is null - user hasn't calculated yet")
            return
        }

        // ALWAYS save to local database first (guaranteed to work)
        lifecycleScope.launch {
            try {
                Log.d("CalculatorFragment", "Saving to LOCAL database...")
                viewModel.saveToLocal(weight, height, bmiResult.bmi, bmiResult.category)
                
                // Try to save to remote backend in background (optional)
                val token = preferenceManager.getToken()
                if (token != null) {
                    Log.d("CalculatorFragment", "Also trying to save to remote backend...")
                    // Don't wait for this, just fire and forget
                    viewModel.saveRecordToBackendAsync(token, weight, height)
                }
            } catch (e: Exception) {
                Log.e("CalculatorFragment", "Save exception: ${e.message}", e)
                Snackbar.make(binding.root, "Error: ${e.message}", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun observeResult() {
        viewModel.bmiResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                Log.d("CalculatorFragment", "observeResult: BMI = ${it.bmi}, Category = ${it.category}")
                
                binding.tvBmiResult.text = String.format("%.1f", it.bmi)
                binding.tvBmiCategoryResult.text = it.category
                binding.cardResult.visibility = View.VISIBLE

                // Set color based on category
                val color = when (it.category.lowercase()) {
                    "underweight" -> ContextCompat.getColor(requireContext(), R.color.bmi_underweight)
                    "normal weight" -> ContextCompat.getColor(requireContext(), R.color.bmi_normal)
                    "overweight" -> ContextCompat.getColor(requireContext(), R.color.bmi_overweight)
                    else -> ContextCompat.getColor(requireContext(), R.color.bmi_obese)
                }
                binding.tvBmiResult.setTextColor(color)

                // Health tip
                binding.tvBmiHealthTip.text = when (it.category.lowercase()) {
                    "underweight" -> "Consider consulting a nutritionist for healthy weight gain"
                    "normal weight" -> "Great job! Maintain your healthy lifestyle"
                    "overweight" -> "Consider regular exercise and balanced diet"
                    else -> "Consult a healthcare provider for personalized advice"
                }
            }
        }
    }

    private fun observeSaveState() {
        viewModel.saveState.observe(viewLifecycleOwner) { success ->
            val message = viewModel.saveMessage.value
            Log.d("CalculatorFragment", "observeSaveState: success = $success, message = $message")
            
            if (!message.isNullOrBlank()) {
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
            } else {
                if (success) {
                    Snackbar.make(binding.root, "Record saved successfully", Snackbar.LENGTH_SHORT).show()
                } else {
                    Snackbar.make(binding.root, "Failed to save record", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
