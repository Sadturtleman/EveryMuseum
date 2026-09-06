package com.sadturtleman.androidsampleproject.search.navigation

import com.sadturtleman.androidsampleproject.common.navigation.NavRoute
import com.sadturtleman.androidsampleproject.common.navigation.Page

/**
 * 검색 결과 페이지 (Figma: 최종 → 03 · 검색 결과).
 *
 * 고른 필터 값은 인자에 넣지 않는다 — 화면 안에서 바텀시트로 고른다.
 * 다만 "코드로 둘러보기" 로 들어오는 경로가 있어서, **어느 갈래를 펼지**([Args.filterTabCode])는 받는다.
 * (딥링크로 고른 값까지 복원해야 하면 그때 코드 목록을 [Args] 에 더한다.)
 */
object SearchResultPage {
    const val PATH = "/search/result"

    private const val KEY_QUERY = "query"
    private const val KEY_FILTER_TAB = "filterTab"

    /**
     * @param query 검색어. 비면 전체 목록이다.
     * @param filterTabCode 진입하자마자 펼 필터 갈래의 상위 코드(예: PS08 재질).
     *  null 이면 시트를 열지 않는다.
     */
    data class Args(
        val query: String = "",
        val filterTabCode: String? = null,
    ) : Page {
        override fun toRoute(): NavRoute = NavRoute(
            PATH,
            buildMap {
                put(KEY_QUERY, query)
                filterTabCode?.let { put(KEY_FILTER_TAB, it) }
            },
        )

        companion object {
            fun from(args: Map<String, String>): Args = Args(
                query = args[KEY_QUERY].orEmpty(),
                filterTabCode = args[KEY_FILTER_TAB]?.takeIf { it.isNotBlank() },
            )
        }
    }
}
