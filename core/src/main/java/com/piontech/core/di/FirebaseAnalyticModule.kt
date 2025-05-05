package com.piontech.core.di

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import com.piontech.core.firebaseAnalytics.FirebaseAnalyticsLogger
import com.piontech.core.firebaseAnalytics.FirebaseAnalyticsLoggerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object FirebaseAnalyticsModule {
    @Provides
    @Singleton
    fun provideFirebaseAnalytics(application: Application): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(application)
    }

    @Provides
    @Singleton
    fun provideFirebaseAnalyticsLogger(firebaseAnalytics: FirebaseAnalytics): FirebaseAnalyticsLogger {
        return FirebaseAnalyticsLoggerImpl(firebaseAnalytics)
    }
}