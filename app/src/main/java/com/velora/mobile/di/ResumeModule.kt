package com.velora.mobile.di

import com.velora.mobile.data.resume.FirebaseResumeAiRepositoryImpl
import com.velora.mobile.domain.resume.ResumeAiRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ResumeModule {

    @Provides
    @Singleton
    fun provideResumeAiRepo(): ResumeAiRepository = FirebaseResumeAiRepositoryImpl()
}