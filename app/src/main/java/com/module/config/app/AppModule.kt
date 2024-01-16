package com.module.config.app

import android.app.Application
import android.content.Context
import com.module.config.data.AppDatabase
import com.module.config.data.dao.UserDao
import com.module.config.network.ApiServer
import com.module.config.network.RetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.*
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providerApplicationContext(application: Application): Context =
        application.applicationContext

    @Provides
    @Singleton
    fun provideAppDatabase(context: Context): AppDatabase = AppDatabase.getDatabase(context)

    @Provides
    fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao
    }

    @Provides
    @Singleton
    fun provideRetrofitClient(context: Context, baseUrl: String): Retrofit =
        RetrofitClient.getClient(context, baseUrl)

    @Provides
    @Singleton
    fun providerApiService(retrofit: Retrofit): ApiServer = retrofit.create(ApiServer::class.java)
}
