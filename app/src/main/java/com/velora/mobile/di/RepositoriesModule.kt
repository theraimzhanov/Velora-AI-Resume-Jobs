package com.velora.mobile.di


import com.velora.mobile.data.auth.FirebaseAuthRepositoryImpl
import com.velora.mobile.data.jobs.FirestoreJobsRepositoryImpl
import com.velora.mobile.data.resume.LocalResumeAnalyzer
import com.velora.mobile.domain.auth.AuthRepository
import com.velora.mobile.domain.jobs.JobsRepository
import com.velora.mobile.domain.resume.ResumeAnalyzer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module @InstallIn(SingletonComponent::class)
object RepositoriesModule {
    @Provides @Singleton fun authRepo(auth: FirebaseAuth): AuthRepository = FirebaseAuthRepositoryImpl(auth)
    @Provides @Singleton fun jobsRepo(db: FirebaseFirestore): JobsRepository = FirestoreJobsRepositoryImpl(db)
    @Provides @Singleton fun resumeAnalyzer(): ResumeAnalyzer = LocalResumeAnalyzer()
}