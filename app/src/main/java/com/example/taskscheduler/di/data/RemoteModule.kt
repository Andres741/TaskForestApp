package com.example.taskscheduler.di.data

import com.example.taskscheduler.data.sources.remote.apiClient.AApiClient
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {

    @Singleton
    @Provides
    fun provideRetrofit() = AApiClient.getRetrofit()

    @Singleton
    @Provides
    fun provideQuoteApiClient(retrofit: Retrofit) = AApiClient.getQuoteApiClient(retrofit)

    @Singleton
    @Provides
    fun provideFirestore(): FirebaseFirestore = Firebase.firestore

    @Singleton
    @Provides
    fun provideFirestoreTasksCollection(firestore: FirebaseFirestore) = FirestoreForTasks(firestore)
}

private interface IFirestoreForInjection  {
    val collection: CollectionReference
    val firestore get() = collection.firestore
}

@JvmInline
private value class FirestoreForInjection (override val collection: CollectionReference): IFirestoreForInjection

class FirestoreForTasks(firestore: FirebaseFirestore, collection: String = "tasks"):
    IFirestoreForInjection by FirestoreForInjection(firestore.collection(collection))
