package com.soorya.wealthmanager.data.repository

import com.soorya.wealthmanager.data.local.dao.*
import com.soorya.wealthmanager.data.local.entity.*
import com.soorya.wealthmanager.data.remote.NotionSyncService
import com.soorya.wealthmanager.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WealthRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val goalDao: GoalDao,
    private val assetDao: AssetDao,
    private val notionSyncService: NotionSyncService
) {
    // ── Transactions ──
    fun getAllTransactions(): Flow<List<Transaction>> =
        transactionDao.getAllTransactions().map { list -> list.map { it.toDomain() } }

    fun getRecentTransactions(): Flow<List<Transaction>> =
        transactionDao.getRecentTransactions().map { list -> list.map { it.toDomain() } }

    fun getTotalIncome(): Flow<Double> =
        transactionDao.getTotalIncome().map { it ?: 0.0 }

    fun getTotalExpense(): Flow<Double> =
        transactionDao.getTotalExpense().map { it ?: 0.0 }

    fun getIncomeByRange(start: Long, end: Long): Flow<Double> =
        transactionDao.getIncomeByDateRange(start, end).map { it ?: 0.0 }

    fun getExpenseByRange(start: Long, end: Long): Flow<Double> =
        transactionDao.getExpenseByDateRange(start, end).map { it ?: 0.0 }

    suspend fun getSuggestions(query: String): List<String> =
        transactionDao.getSuggestions("%$query%")

    suspend fun getLastByTitle(title: String): Transaction? =
        transactionDao.getLastTransactionByTitle(title)?.toDomain()

    suspend fun addTransaction(transaction: Transaction, notionToken: String, notionDbId: String): Long {
        val entity = transaction.toEntity()
        val id = transactionDao.insert(entity)

        // Try Notion sync
        if (notionToken.isNotEmpty() && notionDbId.isNotEmpty()) {
            val pageId = notionSyncService.syncTransaction(transaction.copy(id = id), notionDbId)
            if (pageId != null) {
                transactionDao.update(entity.copy(id = id, syncedToNotion = true, notionPageId = pageId))
            } else {
                transactionDao.update(entity.copy(id = id, pendingSync = true))
            }
        }
        return id
    }

    suspend fun deleteTransaction(id: Long) = transactionDao.deleteById(id)

    suspend fun syncPendingTransactions(notionToken: String, notionDbId: String): Int {
        if (notionToken.isEmpty() || notionDbId.isEmpty()) return 0
        val pending = transactionDao.getPendingSyncTransactions()
        var synced = 0
        for (entity in pending) {
            val pageId = notionSyncService.syncTransaction(entity.toDomain(), notionDbId)
            if (pageId != null) {
                transactionDao.update(entity.copy(syncedToNotion = true, notionPageId = pageId, pendingSync = false))
                synced++
            }
        }
        return synced
    }

    // ── Goals ──
    fun getAllGoals(): Flow<List<Goal>> =
        goalDao.getAllGoals().map { list -> list.map { it.toDomain() } }

    suspend fun addGoal(goal: Goal) = goalDao.insert(goal.toEntity())
    suspend fun updateGoal(goal: Goal) = goalDao.update(goal.toEntity())
    suspend fun deleteGoal(id: Long) = goalDao.deleteById(id)

    // ── Assets ──
    fun getAllAssets(): Flow<List<Asset>> =
        assetDao.getAllAssets().map { list -> list.map { it.toDomain() } }

    fun getTotalAssets(): Flow<Double> =
        assetDao.getTotalAssets().map { it ?: 0.0 }

    fun getTotalLiabilities(): Flow<Double> =
        assetDao.getTotalLiabilities().map { it ?: 0.0 }

    suspend fun addAsset(asset: Asset) = assetDao.insert(asset.toEntity())
    suspend fun updateAsset(asset: Asset) = assetDao.update(asset.toEntity())
    suspend fun deleteAsset(id: Long) = assetDao.deleteById(id)

    // ── Notion ──
    suspend fun testNotionConnection(token: String, dbId: String): Boolean =
        notionSyncService.testConnection(dbId)
}
