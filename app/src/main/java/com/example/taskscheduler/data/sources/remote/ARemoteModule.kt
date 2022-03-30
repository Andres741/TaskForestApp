package com.example.taskscheduler.data.sources.remote

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
object ARemoteModule {

    private const val BASE_URL =
        ""

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
//            .addConverterFactory(MoshiConverterFactory.create())
            //Needed because @JsonClass(generateAdapter = true) annotation doesn't work.
            .addConverterFactory(MoshiConverterFactory.create(
                Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()))
            .build()
    }

    @Singleton
    @Provides
    fun provideQuoteApiClient(retrofit: Retrofit): AApiClient {
        return retrofit.create(AApiClient::class.java)
    }
}
