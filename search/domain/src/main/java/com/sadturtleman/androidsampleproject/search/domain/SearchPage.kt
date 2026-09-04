package com.sadturtleman.androidsampleproject.search.domain

import com.sadturtleman.androidsampleproject.common.domain.navigation.NavRoute
import com.sadturtleman.androidsampleproject.common.domain.navigation.Page

/**
 * 앱의 시작 페이지이자 탭 루트.
 *
 * AppNavHost 에서 "스택 정리 후 단일 유지" 시맨틱을 가지므로,
 * 어디서 Search 로 이동하든 백스택에 Search 가 중복으로 쌓이지 않는다.
 */
object SearchPage : Page {
    const val PATH = "/search"

    override fun toRoute(): NavRoute = NavRoute(PATH)
}
