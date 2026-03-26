package com.example.bmicalculator1.domain

object BMIConstants {
    const val UNDERWEIGHT_MAX = 18.5
    const val NORMAL_WEIGHT_MAX = 25.0
    const val OVERWEIGHT_MAX = 30.0
    
    fun getCategory(bmi: Double): String {
        return when {
            bmi < UNDERWEIGHT_MAX -> "Underweight"
            bmi < NORMAL_WEIGHT_MAX -> "Normal weight"
            bmi < OVERWEIGHT_MAX -> "Overweight"
            else -> "Obese"
        }
    }
    
    fun getCategoryColorIndex(bmi: Double): Int {
        return when {
            bmi < UNDERWEIGHT_MAX -> 0 // Underweight
            bmi < NORMAL_WEIGHT_MAX -> 1 // Normal
            bmi < OVERWEIGHT_MAX -> 2 // Overweight
            else -> 3 // Obese
        }
    }
}

data class BMIResult(
    val bmi: Double,
    val category: String,
    val weight: Double,
    val height: Double,
    val isHealthy: Boolean
)

class BMICalculator {
    fun calculate(weight: Double, height: Double): BMIResult {
        val heightInMeters = height / 100
        val bmi = weight / (heightInMeters * heightInMeters)
        val category = BMIConstants.getCategory(bmi)
        
        return BMIResult(
            bmi = bmi,
            category = category,
            weight = weight,
            height = height,
            isHealthy = category == "Normal weight"
        )
    }
}
