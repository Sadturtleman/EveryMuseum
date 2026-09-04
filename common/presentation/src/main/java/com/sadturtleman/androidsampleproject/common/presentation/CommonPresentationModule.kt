package com.sadturtleman.androidsampleproject.common.presentation

import com.sadturtleman.androidsampleproject.common.domain.helper.NavigationHelper
import com.sadturtleman.androidsampleproject.common.presentation.helper.NavigationHelperImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommonPresentationModule {
    /**
     * 네비게이션 신호 채널은 앱 전역에 하나만 존재해야 한다.
     * (여러 인스턴스가 생기면 호스트가 구독하지 않는 채널로 신호가 흘러가 유실된다.)
     */
    @Provides
    @Singleton
    fun provideNavigationHelper(): NavigationHelper = NavigationHelperImpl()
}
