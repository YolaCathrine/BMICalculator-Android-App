package com.example.bmicalculator1.ui.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bmicalculator1.data.local.AppDatabase
import com.example.bmicalculator1.data.local.entity.GoalLocal
import com.example.bmicalculator1.data.remote.RetrofitClient
import com.example.bmicalculator1.data.repository.GoalRepository
import com.example.bmicalculator1.databinding.DialogGoalBinding
import com.example.bmicalculator1.databinding.FragmentGoalsBinding
import com.example.bmicalculator1.domain.PreferenceManager
import com.example.bmicalculator1.ui.adapter.GoalsAdapter
import com.example.bmicalculator1.ui.viewmodel.GoalsViewModel
import com.example.bmicalculator1.ui.viewmodel.GoalsViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.Calendar

class GoalsFragment : Fragment() {

    private var _binding: FragmentGoalsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GoalsViewModel by viewModels {
        val database = AppDatabase.getDatabase(requireContext())
        val apiService = RetrofitClient.apiService
        val goalRepo = GoalRepository(apiService, database.goalDao())
        GoalsViewModelFactory(goalRepo)
    }

    private val preferenceManager by lazy { PreferenceManager(requireContext()) }
    private lateinit var adapter: GoalsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoalsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        viewModel.loadGoals()
        observeData()

        binding.fabAddGoal.setOnClickListener {
            showGoalDialog()
        }
    }

    private fun setupRecyclerView() {
        adapter = GoalsAdapter(
            onEditClick = { goal ->
                showGoalDialog(goal)
            },
            onDeleteClick = { goal ->
                viewLifecycleOwner.lifecycleScope.launch {
                    val token = preferenceManager.getToken()
                    if (token != null) {
                        viewModel.deleteGoal(token, goal.id)
                    }
                }
            }
        )
        binding.rvGoals.layoutManager = LinearLayoutManager(requireContext())
        binding.rvGoals.adapter = adapter
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.goals.collect { goals ->
                updateUI(goals)
            }
        }

        viewModel.createSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Snackbar.make(binding.root, "Goal created successfully", Snackbar.LENGTH_SHORT).show()
            }
        }

        viewModel.deleteSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Snackbar.make(binding.root, "Goal deleted", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(goals: List<GoalLocal>) {
        if (goals.isEmpty()) {
            binding.emptyState.visibility = View.VISIBLE
            binding.rvGoals.visibility = View.GONE
        } else {
            binding.emptyState.visibility = View.GONE
            binding.rvGoals.visibility = View.VISIBLE
            adapter.submitList(goals.toList())
        }
    }

    private fun showGoalDialog(goal: GoalLocal? = null) {
        val dialogBinding = DialogGoalBinding.inflate(LayoutInflater.from(requireContext()))
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        // Set existing values if editing
        goal?.let {
            dialogBinding.etTargetWeight.setText(it.targetWeight.toString())
            dialogBinding.etCurrentWeight.setText(it.currentWeight?.toString() ?: "")
            dialogBinding.etGoalDate.setText(it.goalDate ?: "")
        }

        // Date picker
        dialogBinding.etGoalDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    dialogBinding.etGoalDate.setText("$year-${month + 1}-$day")
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnSaveGoal.setOnClickListener {
            val targetWeight = dialogBinding.etTargetWeight.text.toString().toDoubleOrNull()
            val currentWeight = dialogBinding.etCurrentWeight.text.toString().toDoubleOrNull()
            val goalDate = dialogBinding.etGoalDate.text.toString().ifEmpty { null }

            if (targetWeight != null && targetWeight > 0) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val token = preferenceManager.getToken()
                    if (token != null) {
                        viewModel.createGoal(token, targetWeight, null, goalDate, currentWeight)
                        dialog.dismiss()
                    } else {
                        Snackbar.make(binding.root, "Please login to create goals", Snackbar.LENGTH_SHORT).show()
                    }
                }
            } else {
                Snackbar.make(binding.root, "Please enter valid target weight", Snackbar.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
