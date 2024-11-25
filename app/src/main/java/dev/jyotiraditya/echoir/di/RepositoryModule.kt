package dev.jyotiraditya.echoir.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.jyotiraditya.echoir.data.local.dao.DownloadDao
import dev.jyotiraditya.echoir.data.remote.api.ApiService
import dev.jyotiraditya.echoir.data.repository.DownloadRepositoryImpl
import dev.jyotiraditya.echoir.data.repository.MusicRepositoryImpl
import dev.jyotiraditya.echoir.data.repository.SettingsRepositoryImpl
import dev.jyotiraditya.echoir.domain.repository.DownloadRepository
import dev.jyotiraditya.echoir.domain.repository.MusicRepository
import dev.jyotiraditya.echoir.domain.repository.SettingsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideMusicRepository(
        apiService: ApiService
    ): MusicRepository = MusicRepositoryImpl(apiService)

    @Provides
    @Singleton
    fun provideDownloadRepository(
        apiService: ApiService,
        downloadDao: DownloadDao,
        settingsRepository: SettingsRepository,
        @ApplicationContext context: Context
    ): DownloadRepository = DownloadRepositoryImpl(apiService, downloadDao, settingsRepository, context)

    @Provides
    @Singleton
    fun provideSettingsRepository(
        @ApplicationContext context: Context
    ): SettingsRepository = SettingsRepositoryImpl(context)
}