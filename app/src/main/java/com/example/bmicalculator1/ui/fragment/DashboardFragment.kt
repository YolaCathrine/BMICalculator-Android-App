package com.example.bmicalculator1.ui.fragment

import android.graphics.Color
import android.os.Bundle
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
import com.example.bmicalculator1.data.repository.GoalRepository
import com.example.bmicalculator1.databinding.FragmentDashboardBinding
import com.example.bmicalculator1.domain.PreferenceManager
import com.example.bmicalculator1.ui.viewmodel.DashboardViewModel
import com.example.bmicalculator1.ui.viewmodel.DashboardViewModelFactory
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModels {
        val database = AppDatabase.getDatabase(requireContext())
        val apiService = RetrofitClient.apiService
        val bmiRepo = BMIRepository(apiService, database.bmiRecordDao())
        val goalRepo = GoalRepository(apiService, database.goalDao())
        DashboardViewModelFactory(bmiRepo, goalRepo)
    }

    private val preferenceManager by lazy { PreferenceManager(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        lifecycleScope.launch {
            val email = preferenceManager.getEmail()
            email?.let {
                binding.tvUserEmail.text = it
            }
        }

        viewModel.loadRecords()
        viewModel.loadGoals()

        observeData()
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.bmiRecords.collectLatest { records ->
                binding.tvTotalRecords.text = records.size.toString()
                
                if (records.isNotEmpty()) {
                    val latest = records.first()
                    binding.tvCurrentBmi.text = latest.bmiValue
                    binding.tvBmiCategory.text = latest.category
                    
                    // Set category color
                    val color = when (latest.category.lowercase()) {
                        "underweight" -> ContextCompat.getColor(requireContext(), R.color.bmi_underweight)
                        "normal weight" -> ContextCompat.getColor(requireContext(), R.color.bmi_normal)
                        "overweight" -> ContextCompat.getColor(requireContext(), R.color.bmi_overweight)
                        else -> ContextCompat.getColor(requireContext(), R.color.bmi_obese)
                    }
                    binding.tvCurrentBmi.setTextColor(color)
                } else {
                    binding.tvCurrentBmi.text = "--"
                    binding.tvBmiCategory.text = "Calculate your BMI"
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.goals.collectLatest { goals ->
                binding.tvActiveGoals.text = goals.count { it.status == "active" }.toString()
            }
        }

        // chartData is LiveData, use observe instead of collect
        viewModel.chartData.observe(viewLifecycleOwner) { data ->
            if (data.isNotEmpty()) {
                binding.tvNoChartData.visibility = View.GONE
                binding.lineChart.visibility = View.VISIBLE
                setupChart(binding.lineChart, data)
            } else {
                binding.tvNoChartData.visibility = View.VISIBLE
                binding.lineChart.visibility = View.GONE
            }
        }
    }

    private fun setupChart(chart: LineChart, data: List<Float>) {
        val entries = data.mapIndexed { index, value ->
            Entry(index.toFloat(), value)
        }

        val dataSet = LineDataSet(entries, "BMI Trend").apply {
            color = ContextCompat.getColor(requireContext(), R.color.accent)
            valueTextColor = ContextCompat.getColor(requireContext(), R.color.on_surface)
            lineWidth = 2f
            setCircleColor(ContextCompat.getColor(requireContext(), R.color.accent))
            setDrawCircles(true)
            setDrawFilled(true)
            fillColor = ContextCompat.getColor(requireContext(), R.color.accent_light)
            fillAlpha = 100
        }

        chart.data = LineData(dataSet)
        chart.description.isEnabled = false
        chart.setTouchEnabled(false)
        chart.setPinchZoom(false)
        chart.setDrawGridBackground(false)

        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            isEnabled = false
        }

        chart.axisLeft.apply {
            setDrawGridLines(true)
            gridColor = ContextCompat.getColor(requireContext(), R.color.divider)
        }

        chart.axisRight.isEnabled = false
        chart.legend.isEnabled = false
        chart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
