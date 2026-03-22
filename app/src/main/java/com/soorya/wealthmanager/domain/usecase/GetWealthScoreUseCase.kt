package com.soorya.wealthmanager.domain.usecase

import com.soorya.wealthmanager.domain.model.WealthScore
import javax.inject.Inject

class GetWealthScoreUseCase @Inject constructor() {
    operator fun invoke(
        totalIncome: Double,
        totalExpense: Double,
        totalAssets: Double,
        totalLiabilities: Double,
        goalProgress: Float
    ): WealthScore {
        val breakdown = mutableMapOf<String, Int>()

        // 1. Savings Rate (30 points)
        val savingsRate = if (totalIncome > 0) (totalIncome - totalExpense) / totalIncome else 0.0
        val savingsScore = when {
            savingsRate >= 0.30 -> 30
            savingsRate >= 0.20 -> 24
            savingsRate >= 0.10 -> 18
            savingsRate >= 0.05 -> 12
            savingsRate > 0 -> 6
            else -> 0
        }
        breakdown["Savings Rate"] = savingsScore

        // 2. Expense Control (25 points)
        val expenseRatio = if (totalIncome > 0) totalExpense / totalIncome else 1.0
        val expenseScore = when {
            expenseRatio <= 0.50 -> 25
            expenseRatio <= 0.65 -> 20
            expenseRatio <= 0.75 -> 15
            expenseRatio <= 0.85 -> 10
            expenseRatio <= 0.95 -> 5
            else -> 0
        }
        breakdown["Expense Control"] = expenseScore

        // 3. Net Worth Ratio (25 points)
        val netWorthRatio = if (totalAssets > 0) (totalAssets - totalLiabilities) / totalAssets else 0.0
        val netWorthScore = when {
            netWorthRatio >= 0.80 -> 25
            netWorthRatio >= 0.60 -> 20
            netWorthRatio >= 0.40 -> 15
            netWorthRatio >= 0.20 -> 10
            netWorthRatio > 0 -> 5
            else -> 0
        }
        breakdown["Net Worth"] = netWorthScore

        // 4. Goal Progress (20 points)
        val goalScore = (goalProgress * 20).toInt().coerceIn(0, 20)
        breakdown["Goal Progress"] = goalScore

        val totalScore = breakdown.values.sum()
        val label = when {
            totalScore >= 80 -> "Excellent"
            totalScore >= 60 -> "Good"
            totalScore >= 40 -> "Fair"
            totalScore >= 20 -> "Needs Work"
            else -> "Critical"
        }

        return WealthScore(
            score = totalScore,
            label = label,
            breakdown = breakdown
        )
    }
}
