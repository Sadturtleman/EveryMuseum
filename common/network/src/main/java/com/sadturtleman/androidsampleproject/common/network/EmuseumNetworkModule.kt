package com.sadturtleman.androidsampleproject.common.network

import com.sadturtleman.androidsampleproject.common.network.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * 앱 공용 통신 스택.
 *
 * OkHttp · Retrofit · 인증키 주입까지만 책임진다.
 * 어떤 엔드포인트가 있는지(API 인터페이스)는 이 Retrofit 을 주입받는 data 모듈이 정한다.
 *
 * 인증키와 주소는 소스에 두지 않는다. `local.properties` 의 `EMUSEUM_SERVICE_KEY` ·
 * `EMUSEUM_BASE_URL` 을 빌드 시 [BuildConfig] 로 굽고, 인증키는 [ServiceKeyInterceptor] 가
 * 요청마다 붙인다. 주소는 값이 없으면 빌드 스크립트의 기본값으로 채워진다.
 */
@Module
@InstallIn(SingletonComponent::class)
object EmuseumNetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(ServiceKeyInterceptor(BuildConfig.EMUSEUM_SERVICE_KEY))
        .apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(
                    HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
                )
            }
        }
        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.EMUSEUM_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(EmuseumXmlConverterFactory())
        .build()

    private const val TIMEOUT_SECONDS = 15L
}
