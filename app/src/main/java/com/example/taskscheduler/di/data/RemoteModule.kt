package com.example.taskscheduler.di.data

import com.example.taskscheduler.data.ALocalRepository
import com.example.taskscheduler.data.ARemoteRepository
import com.example.taskscheduler.data.sources.remote.apiClient.AApiClient
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
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
}
