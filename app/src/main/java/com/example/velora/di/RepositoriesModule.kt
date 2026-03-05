package com.example.velora.di


import com.example.velora.data.auth.FirebaseAuthRepositoryImpl
import com.example.velora.data.jobs.FirestoreJobsRepositoryImpl
import com.example.velora.data.resume.LocalResumeAnalyzer
import com.example.velora.domain.auth.AuthRepository
import com.example.velora.domain.jobs.JobsRepository
import com.example.velora.domain.resume.ResumeAnalyzer
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