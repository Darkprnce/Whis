package com.whis.Network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(300, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS)
            .writeTimeout(300, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://canime.in/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideWorkoutApi(retrofit: Retrofit): ApiServices {
        return retrofit.create(ApiServices::class.java)
    }
}

sealed class ApiResponse<out T> {
    data class Loading(val tag: String,val coroutineScope: CoroutineScope? = null) : ApiResponse<Nothing>()
    object None : ApiResponse<Nothing>()
    data class Error<T>(val tag: String,val message: String,val item: T?=null) : ApiResponse<T?>()
    data class Success<T>(val tag: String,val item: T?) : ApiResponse<T?>()
}