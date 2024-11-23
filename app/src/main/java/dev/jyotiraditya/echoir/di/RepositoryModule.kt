package dev.jyotiraditya.echoir.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.jyotiraditya.echoir.data.remote.api.ApiService
import dev.jyotiraditya.echoir.data.repository.MusicRepositoryImpl
import dev.jyotiraditya.echoir.domain.repository.MusicRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideMusicRepository(
        apiService: ApiService
    ): MusicRepository = MusicRepositoryImpl(apiService)
}