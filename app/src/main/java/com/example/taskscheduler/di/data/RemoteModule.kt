package com.example.taskscheduler.di.data

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {

//    @Singleton
//    @Provides
//    fun provideRetrofit() = AApiClient.getRetrofit()
//
//    @Singleton
//    @Provides
//    fun provideQuoteApiClient(retrofit: Retrofit) = AApiClient.getQuoteApiClient(retrofit)

    @Provides
    fun provideFirestoreCollectionForTasks() = FirestoreCollectionForTasks(
        Firebase.auth.uid?.let { uid ->
            Firebase.firestore.collection("users/$uid/tasks")
//            Firebase.firestore.collection("users/zzz_test/tasks")
        }
    )
}

data class FirestoreCollectionForTasks(
    val collection: CollectionReference?
)
