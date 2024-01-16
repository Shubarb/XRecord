package com.module.config.network

import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.module.config.network.serialize.BitmapSerializer
import com.module.config.network.serialize.BooleanSerializer
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Cache
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


private const val CACHE_SIZE = (10 * 1024 * 1024).toLong()
private const val READ_TIMEOUT = 30000L
private const val WRITE_TIMEOUT = 30000L
private const val CONNECT_TIMEOUT = 30000L
private const val CACHE_CONTROL = "Cache-Control"
private const val TIME_CACHE_ONLINE = "public, max-age = 60" // 1 minute

private const val TIME_CACHE_OFFLINE = "public, only-if-cached, max-stale = 86400" //1 day

object RetrofitClient {
    private var retrofit: Retrofit? = null

    fun getClient(context: Context, baseUrl: String): Retrofit {
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

        return retrofit ?: synchronized(this) {
            val instance = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(initClient(context))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(getGsonConfig()))
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
            retrofit = instance
            instance
        }
    }

    private fun getGsonConfig(): Gson {
        val booleanSerializer = BooleanSerializer()
        val gb = GsonBuilder()
        gb.setLenient()
        gb.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        gb.registerTypeAdapter(Boolean::class.java, booleanSerializer)
        gb.registerTypeAdapter(Boolean::class.javaPrimitiveType, booleanSerializer)
        gb.registerTypeAdapter(Bitmap::class.java, BitmapSerializer())
        return gb.create()
    }

    private fun initClient(context: Context): OkHttpClient {
        val builder: OkHttpClient.Builder = OkHttpClient.Builder()
            .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(true)
            .cache(Cache(context.getCacheDir(), CACHE_SIZE))
            .addInterceptor { chain ->
                var request: Request = chain.request()
                request = if (isNetworkAvailable(context)) {
                    request
                        .newBuilder()
                        .header(CACHE_CONTROL, TIME_CACHE_ONLINE)
                        .build()
                } else {
                    request
                        .newBuilder()
                        .header(CACHE_CONTROL, TIME_CACHE_OFFLINE)
                        .build()
                }
                val httpUrl: HttpUrl = request.url
                    .newBuilder()
                    .build()
                val requestBuilder: Request.Builder = request
                    .newBuilder()
                    .url(httpUrl)
                chain.proceed(requestBuilder.build())
            }
        return builder.build()
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager: ConnectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}