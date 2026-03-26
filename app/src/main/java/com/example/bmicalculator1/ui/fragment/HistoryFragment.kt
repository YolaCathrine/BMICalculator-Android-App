package com.example.bmicalculator1.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bmicalculator1.data.local.AppDatabase
import com.example.bmicalculator1.data.local.entity.BmiRecordLocal
import com.example.bmicalculator1.data.remote.RetrofitClient
import com.example.bmicalculator1.data.repository.BMIRepository
import com.example.bmicalculator1.databinding.FragmentHistoryBinding
import com.example.bmicalculator1.domain.PreferenceManager
import com.example.bmicalculator1.ui.adapter.BmiRecordAdapter
import com.example.bmicalculator1.ui.viewmodel.HistoryViewModel
import com.example.bmicalculator1.ui.viewmodel.HistoryViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HistoryViewModel by viewModels {
        val database = AppDatabase.getDatabase(requireContext())
        val apiService = RetrofitClient.apiService
        val bmiRepo = BMIRepository(apiService, database.bmiRecordDao())
        HistoryViewModelFactory(bmiRepo)
    }

    private val preferenceManager by lazy { PreferenceManager(requireContext()) }
    private lateinit var adapter: BmiRecordAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        viewModel.loadRecords()
        observeData()
    }

    private fun setupRecyclerView() {
        adapter = BmiRecordAdapter(
            onDeleteClick = { record ->
                Log.d("HistoryFragment", "Delete clicked for record: ${record.id}")
                viewLifecycleOwner.lifecycleScope.launch {
                    val token = preferenceManager.getToken()
                    Log.d("HistoryFragment", "Token: ${if (token != null) "EXISTS" else "NULL"}")
                    
                    if (token != null) {
                        // Delete from both remote and local
                        viewModel.deleteRecord(token, record.id)
                    } else {
                        // Delete from local only
                        viewModel.deleteLocalRecord(record)
                    }
                }
            }
        )
        binding.rvBmiRecords.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBmiRecords.adapter = adapter
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.records.collect { records ->
                Log.d("HistoryFragment", "Records updated: ${records.size} records")
                updateUI(records)
            }
        }

        viewModel.deleteSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Snackbar.make(binding.root, "Record deleted successfully", Snackbar.LENGTH_SHORT).show()
                // Refresh data
                viewModel.loadRecords()
            } else {
                Snackbar.make(binding.root, "Failed to delete record", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(records: List<BmiRecordLocal>) {
        Log.d("HistoryFragment", "updateUI called with ${records.size} records")
        if (records.isEmpty()) {
            binding.emptyState.visibility = View.VISIBLE
            binding.rvBmiRecords.visibility = View.GONE
        } else {
            binding.emptyState.visibility = View.GONE
            binding.rvBmiRecords.visibility = View.VISIBLE
            adapter.submitList(records.toList())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
