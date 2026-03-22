package com.soorya.wealthmanager.di

import android.content.Context
import androidx.room.Room
import com.soorya.wealthmanager.data.local.WealthDatabase
import com.soorya.wealthmanager.data.local.dao.*
import com.soorya.wealthmanager.data.remote.NotionApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): WealthDatabase =
        Room.databaseBuilder(context, WealthDatabase::class.java, WealthDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideTransactionDao(db: WealthDatabase): TransactionDao = db.transactionDao()

    @Provides
    fun provideGoalDao(db: WealthDatabase): GoalDao = db.goalDao()

    @Provides
    fun provideAssetDao(db: WealthDatabase): AssetDao = db.assetDao()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Notion-Version", NotionApi.NOTION_VERSION)
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideNotionApi(okHttpClient: OkHttpClient): NotionApi =
        Retrofit.Builder()
            .baseUrl(NotionApi.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NotionApi::class.java)
}
