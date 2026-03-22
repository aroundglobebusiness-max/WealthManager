package com.soorya.wealthmanager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.soorya.wealthmanager.domain.model.*

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
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
    val locationName: String? = null,
    val pendingSync: Boolean = false
) {
    fun toDomain() = Transaction(
        id = id,
        type = TransactionType.valueOf(type),
        amount = amount,
        currency = currency,
        currencySymbol = currencySymbol,
        title = title,
        category = category,
        note = note,
        date = date,
        syncedToNotion = syncedToNotion,
        notionPageId = notionPageId,
        latitude = latitude,
        longitude = longitude,
        locationName = locationName
    )
}

fun Transaction.toEntity() = TransactionEntity(
    id = id,
    type = type.name,
    amount = amount,
    currency = currency,
    currencySymbol = currencySymbol,
    title = title,
    category = category,
    note = note,
    date = date,
    syncedToNotion = syncedToNotion,
    notionPageId = notionPageId,
    latitude = latitude,
    longitude = longitude,
    locationName = locationName
)

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val currency: String = "INR",
    val currencySymbol: String = "₹",
    val deadline: Long? = null,
    val emoji: String = "🎯",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain() = Goal(
        id = id,
        title = title,
        targetAmount = targetAmount,
        currentAmount = currentAmount,
        currency = currency,
        currencySymbol = currencySymbol,
        deadline = deadline,
        emoji = emoji,
        createdAt = createdAt
    )
}

fun Goal.toEntity() = GoalEntity(
    id = id,
    title = title,
    targetAmount = targetAmount,
    currentAmount = currentAmount,
    currency = currency,
    currencySymbol = currencySymbol,
    deadline = deadline,
    emoji = emoji,
    createdAt = createdAt
)

@Entity(tableName = "assets")
data class AssetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String,
    val value: Double,
    val currency: String = "INR",
    val currencySymbol: String = "₹",
    val isLiability: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toDomain() = Asset(
        id = id,
        name = name,
        type = AssetType.valueOf(type),
        value = value,
        currency = currency,
        currencySymbol = currencySymbol,
        isLiability = isLiability,
        updatedAt = updatedAt
    )
}

fun Asset.toEntity() = AssetEntity(
    id = id,
    name = name,
    type = type.name,
    value = value,
    currency = currency,
    currencySymbol = currencySymbol,
    isLiability = isLiability,
    updatedAt = updatedAt
)
