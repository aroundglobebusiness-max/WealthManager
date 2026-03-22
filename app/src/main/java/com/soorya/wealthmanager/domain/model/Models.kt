package com.soorya.wealthmanager.domain.model

data class Transaction(
    val id: Long = 0,
    val type: TransactionType,
    val amount: Double,
    val currency: String = "INR",
    val currencySymbol: String = "₹",
    val title: String,
    val category: String,
    val note: String = "",
    val date: Long = System.currentTimeMillis(),
    val syncedToNotion: Boolean = false,
    val notionPageId: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val locationName: String? = null
)

enum class TransactionType {
    INCOME, EXPENSE
}

data class Goal(
    val id: Long = 0,
    val title: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val currency: String = "INR",
    val currencySymbol: String = "₹",
    val deadline: Long? = null,
    val emoji: String = "🎯",
    val createdAt: Long = System.currentTimeMillis()
) {
    val progress: Float get() = (currentAmount / targetAmount).coerceIn(0f, 1f)
    val isCompleted: Boolean get() = currentAmount >= targetAmount
    val remainingAmount: Double get() = (targetAmount - currentAmount).coerceAtLeast(0.0)
}

data class Asset(
    val id: Long = 0,
    val name: String,
    val type: AssetType,
    val value: Double,
    val currency: String = "INR",
    val currencySymbol: String = "₹",
    val isLiability: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

enum class AssetType {
    CASH, INVESTMENT, PROPERTY, VEHICLE, OTHER,
    LOAN, CREDIT_CARD, MORTGAGE
}

data class WealthScore(
    val score: Int,
    val label: String,
    val breakdown: Map<String, Int>
) {
    val color get() = when {
        score >= 80 -> "excellent"
        score >= 60 -> "good"
        score >= 40 -> "fair"
        else -> "poor"
    }
}

data class NetWorth(
    val totalAssets: Double,
    val totalLiabilities: Double,
    val netWorth: Double = totalAssets - totalLiabilities
)
