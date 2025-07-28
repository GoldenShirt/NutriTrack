package com.nutritrack.di

import android.content.Context
import com.nutritrack.BuildConfig
import com.nutritrack.data.local.AppDatabase
import com.nutritrack.data.local.MealDao
import com.nutritrack.ai.GPTManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.get(context)
    }

    @Provides
    fun provideMealDao(database: AppDatabase): MealDao {
        return database.mealDao()
    }

    @Provides
    @Singleton
    fun provideGPTManager(mealDao: MealDao): GPTManager {
        return GPTManager(BuildConfig.OPENAI_API_KEY, mealDao)
    }
}