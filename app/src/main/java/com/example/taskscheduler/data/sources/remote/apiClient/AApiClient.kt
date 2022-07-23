package com.example.taskscheduler.data.sources.remote.apiClient

//import com.squareup.moshi.Moshi
//import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
//import retrofit2.Retrofit
//import retrofit2.converter.moshi.MoshiConverterFactory
//import retrofit2.http.GET
//import javax.inject.Singleton
//
//@Singleton
//interface AApiClient {
//    @GET("/.json")
//    suspend fun getAll(): Any?
//
//    companion object {
//        private const val BASE_URL =
//            ""
//
//        fun getRetrofit(): Retrofit {
//            return Retrofit.Builder()
//                .baseUrl(BASE_URL)
////            .addConverterFactory(MoshiConverterFactory.create())
//                //Needed because @JsonClass(generateAdapter = true) annotation doesn't work.
//                .addConverterFactory(
//                    MoshiConverterFactory.create(
//                    Moshi.Builder()
//                        .add(KotlinJsonAdapterFactory())
//                        .build()))
//                .build()
//        }
//
//        fun getQuoteApiClient(retrofit: Retrofit): AApiClient {
//            return retrofit.create(AApiClient::class.java)
//        }
//    }
//}
