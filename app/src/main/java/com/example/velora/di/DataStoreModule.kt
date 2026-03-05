package com.example.velora.di

import android.content.Context
import com.example.velora.data.prefs.IntroPrefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides @Singleton
    fun introPrefs(@ApplicationContext ctx: Context): IntroPrefs = IntroPrefs(ctx)
}