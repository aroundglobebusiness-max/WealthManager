package com.soorya.wealthmanager.data.remote

import android.util.Log
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.soorya.wealthmanager.domain.model.Transaction
import com.soorya.wealthmanager.domain.model.TransactionType
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotionSyncService @Inject constructor(
    private val notionApi: NotionApi
) {
    suspend fun testConnection(databaseId: String): Boolean {
        return try {
            val response = notionApi.getDatabase(databaseId)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("NotionSync", "Connection test failed", e)
            false
        }
    }

    suspend fun syncTransaction(
        transaction: Transaction,
        databaseId: String
    ): String? {
        return try {
            val body = buildTransactionBody(transaction, databaseId)
            val response = notionApi.createPage(body)
            if (response.isSuccessful) {
                response.body()?.get("id")?.asString
            } else {
                Log.e("NotionSync", "Sync failed: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("NotionSync", "Sync exception", e)
            null
        }
    }

    private fun buildTransactionBody(transaction: Transaction, databaseId: String): JsonObject {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateStr = dateFormat.format(Date(transaction.date))

        return JsonObject().apply {
            add("parent", JsonObject().apply {
                addProperty("database_id", databaseId)
            })
            add("properties", JsonObject().apply {
                // Title / Name
                add("Name", JsonObject().apply {
                    add("title", JsonArray().apply {
                        add(JsonObject().apply {
                            add("text", JsonObject().apply {
                                addProperty("content", transaction.title)
                            })
                        })
                    })
                })
                // Amount
                add("Amount", JsonObject().apply {
                    addProperty("number", transaction.amount)
                })
                // Type
                add("Type", JsonObject().apply {
                    add("select", JsonObject().apply {
                        addProperty("name", if (transaction.type == TransactionType.INCOME) "Income" else "Expense")
                    })
                })
                // Category
                add("Category", JsonObject().apply {
                    add("select", JsonObject().apply {
                        addProperty("name", transaction.category)
                    })
                })
                // Currency
                add("Currency", JsonObject().apply {
                    add("rich_text", JsonArray().apply {
                        add(JsonObject().apply {
                            add("text", JsonObject().apply {
                                addProperty("content", transaction.currency)
                            })
                        })
                    })
                })
                // Note
                if (transaction.note.isNotEmpty()) {
                    add("Note", JsonObject().apply {
                        add("rich_text", JsonArray().apply {
                            add(JsonObject().apply {
                                add("text", JsonObject().apply {
                                    addProperty("content", transaction.note)
                                })
                            })
                        })
                    })
                }
                // Date
                add("Date", JsonObject().apply {
                    add("date", JsonObject().apply {
                        addProperty("start", dateStr)
                    })
                })
            })
        }
    }
}
