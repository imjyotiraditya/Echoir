package dev.jyotiraditya.echoir.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.jyotiraditya.echoir.data.local.AppDatabase
import dev.jyotiraditya.echoir.data.local.dao.DownloadDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        AppDatabase.DATABASE_NAME
    )
        .addMigrations(AppDatabase.MIGRATION_1_2)
        .build()

    @Provides
    @Singleton
    fun provideDownloadDao(database: AppDatabase): DownloadDao = database.downloadDao()
}