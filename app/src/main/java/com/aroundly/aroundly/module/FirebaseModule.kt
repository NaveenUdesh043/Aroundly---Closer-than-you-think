package com.aroundly.aroundly.module

import android.content.Context
import com.aroundly.aroundly.data.location.LocationProvider
import com.aroundly.aroundly.data.remote.FirebaseAuthService
import com.aroundly.aroundly.data.remote.FirebaseSubmissionService
import com.aroundly.aroundly.data.repository.AuthRepositoryImpl
import com.aroundly.aroundly.data.repository.DashboardRepositoryImpl
import com.aroundly.aroundly.data.repository.MenuRepositoryImpl
import com.aroundly.aroundly.data.repository.ReviewRepositoryImpl
import com.aroundly.aroundly.data.repository.SubmissionRepositoryImpl
import com.aroundly.aroundly.data.repository.UserRepositoryImpl
import com.aroundly.aroundly.domain.repository.AuthRepository
import com.aroundly.aroundly.domain.repository.DashboardRepository
import com.aroundly.aroundly.domain.repository.MenuRepository
import com.aroundly.aroundly.domain.repository.ReviewRepository
import com.aroundly.aroundly.domain.repository.SubmissionRepository
import com.aroundly.aroundly.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindDashboardRepository(impl: DashboardRepositoryImpl): DashboardRepository

    @Binds
    @Singleton
    abstract fun bindSubmissionRepository(impl: SubmissionRepositoryImpl): SubmissionRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindReviewRepository(impl: ReviewRepositoryImpl): ReviewRepository

    @Binds
    @Singleton
    abstract fun bindMenuRepository(impl: MenuRepositoryImpl): MenuRepository

    companion object {
        @Provides
        @Singleton
        fun provideLocationProvider(@ApplicationContext context: Context): LocationProvider =
            LocationProvider(context)

        @Provides
        @Singleton
        fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

        @Provides
        @Singleton
        fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

        @Provides
        @Singleton
        fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

        @Provides
        @Singleton
        fun provideFirebaseAuthService(auth: FirebaseAuth, firestore: FirebaseFirestore): FirebaseAuthService =
            FirebaseAuthService(auth, firestore)

        @Provides
        @Singleton
        fun provideFirebaseSubmissionService(
            firestore: FirebaseFirestore,
            storage: FirebaseStorage
        ): FirebaseSubmissionService = FirebaseSubmissionService(firestore, storage)
    }
}