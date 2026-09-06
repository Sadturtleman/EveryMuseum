package com.sadturtleman.androidsampleproject.common.data.relic

import com.sadturtleman.androidsampleproject.common.data.code.CodeRepositoryImpl
import com.sadturtleman.androidsampleproject.common.data.remote.EmuseumApi
import com.sadturtleman.androidsampleproject.common.domain.code.CodeRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * 공용 코어 API 와 코드표 저장소를 제공한다.
 *
 * 소장품 조회 저장소는 여기 없다 — 홈 · 검색 · 상세가 각자 :data 모듈에서
 * 이 [EmuseumApi] 위에 자기 조회를 얹는다.
 */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class RelicDataModule {

    @Binds
    @Singleton
    abstract fun bindCodeRepository(impl: CodeRepositoryImpl): CodeRepository

    companion object {
        /** 통신 스택(:common:network)이 만든 Retrofit 에 공용 API 정의를 얹는다. */
        @Provides
        @Singleton
        fun provideEmuseumApi(retrofit: Retrofit): EmuseumApi =
            retrofit.create(EmuseumApi::class.java)
    }
}
