package com.sadturtleman.androidsampleproject.detail.data

import com.sadturtleman.androidsampleproject.detail.data.remote.EmuseumDetailApi
import com.sadturtleman.androidsampleproject.detail.domain.RelicDetailRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DetailDataModule {

    @Binds
    @Singleton
    abstract fun bindRelicDetailRepository(
        impl: RelicDetailRepositoryImpl,
    ): RelicDetailRepository

    companion object {
        /** 통신 스택(:common:network)이 만든 Retrofit 에 이 모듈의 API 정의를 얹는다. */
        @Provides
        @Singleton
        fun provideEmuseumDetailApi(retrofit: Retrofit): EmuseumDetailApi =
            retrofit.create(EmuseumDetailApi::class.java)
    }
}
