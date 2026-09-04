package com.sadturtleman.androidsampleproject.main.presentation.navigation

import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.sadturtleman.androidsampleproject.detail.domain.DetailPage
import com.sadturtleman.androidsampleproject.detail.presentation.DetailPage
import com.sadturtleman.androidsampleproject.detail.presentation.DetailViewModel
import com.sadturtleman.androidsampleproject.search.domain.SearchPage
import com.sadturtleman.androidsampleproject.search.presentation.SearchPage
import com.sadturtleman.androidsampleproject.search.presentation.SearchViewModel
import com.sadturtleman.androidsampleproject.store.domain.StorePage
import com.sadturtleman.androidsampleproject.store.presentation.StorePage
import com.sadturtleman.androidsampleproject.store.presentation.StoreViewModel

/**
 * 앱의 모든 페이지 메타데이터 + 렌더러 모음.
 * 새 화면 추가 시 본 리스트에 한 줄을 더한다. (AppNavHost 코드는 손대지 않는다.)
 *
 * feature 간 화면 참조가 여기 한 곳으로만 모이기 때문에, feature 모듈끼리는
 * 서로의 presentation 을 참조하지 않고 :*:domain 의 Page 정의만 알면 된다.
 */
val appRoutes: List<AppRoute> = listOf(
    AppRoute(
        path = SearchPage.PATH,
        isTopTab = true,
        render = { SearchPage(viewModel = hiltViewModel<SearchViewModel>()) },
    ),
    AppRoute(
        path = StorePage.PATH,
        isTopTab = true,
        syntheticStack = { args ->
            listOf(
                GenericNavKey(SearchPage.PATH),
                GenericNavKey(StorePage.PATH, args),
            )
        },
        render = { StorePage(viewModel = hiltViewModel<StoreViewModel>()) },
    ),
    AppRoute(
        path = DetailPage.PATH,
        // deep-link 로 상세에 바로 진입해도 뒤로가기 시 Search 로 빠지도록 부모 키를 함께 쌓는다.
        syntheticStack = { args ->
            listOf(
                GenericNavKey(SearchPage.PATH),
                GenericNavKey(DetailPage.PATH, args),
            )
        },
        render = { rawArgs ->
            // String 맵 -> typed Args 디코딩은 이 자리에서 한 번만 하고,
            // Hilt assisted injection 으로 ViewModel 생성자에 그대로 넘긴다.
            val args = remember(rawArgs) { DetailPage.Args.from(rawArgs) }
            DetailPage(
                viewModel = hiltViewModel<DetailViewModel, DetailViewModel.Factory>(
                    creationCallback = { factory -> factory.create(args) },
                )
            )
        },
    ),
)

val appRouteByPath: Map<String, AppRoute> = appRoutes.associateBy { it.path }

val topTabRoutes: List<AppRoute> = appRoutes.filter { it.isTopTab }
