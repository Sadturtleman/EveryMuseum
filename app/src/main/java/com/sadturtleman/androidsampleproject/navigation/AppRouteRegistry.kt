package com.sadturtleman.androidsampleproject.navigation

import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumTab
import com.sadturtleman.androidsampleproject.detail.domain.DetailPage
import com.sadturtleman.androidsampleproject.detail.presentation.detail.DetailPage
import com.sadturtleman.androidsampleproject.detail.presentation.detail.DetailViewModel
import com.sadturtleman.androidsampleproject.home.domain.HomePage
import com.sadturtleman.androidsampleproject.home.presentation.home.HomePage
import com.sadturtleman.androidsampleproject.home.presentation.home.HomeViewModel
import com.sadturtleman.androidsampleproject.search.domain.SearchPage
import com.sadturtleman.androidsampleproject.search.domain.SearchResultPage
import com.sadturtleman.androidsampleproject.search.presentation.filter.FilterViewModel
import com.sadturtleman.androidsampleproject.search.presentation.result.SearchResultPage
import com.sadturtleman.androidsampleproject.search.presentation.result.SearchResultViewModel
import com.sadturtleman.androidsampleproject.search.presentation.search.SearchPage
import com.sadturtleman.androidsampleproject.search.presentation.search.SearchViewModel
import com.sadturtleman.androidsampleproject.store.domain.StorePage
import com.sadturtleman.androidsampleproject.store.presentation.library.LibraryPage
import com.sadturtleman.androidsampleproject.store.presentation.library.LibraryViewModel

/**
 * 앱의 모든 페이지 메타데이터 + 렌더러 모음.
 * 새 화면 추가 시 본 리스트에 한 줄을 더한다. (AppNavHost 코드는 손대지 않는다.)
 *
 * feature 간 화면 참조가 여기 한 곳으로만 모이기 때문에, feature 모듈끼리는
 * 서로의 presentation 을 참조하지 않고 :*:domain 의 Page 정의만 알면 된다.
 *
 * 모든 feature:presentation 을 참조하는 유일한 모듈이 되어야 하므로 조립 지점인 :app 에 둔다.
 * (feature 모듈 하나가 이 역할을 겸하면 aggregator 가 둘이 된다)
 *
 * 각 render 는 ViewModel 을 붙이는 한 줄이고, 화면 그리기는 feature 의 *Page 컴포저블이 맡는다.
 */
val appRoutes: List<AppRoute> = listOf(
    AppRoute(
        path = HomePage.PATH,
        isTopTab = true,
        tab = MuseumTab.Home,
        render = { HomePage(viewModel = hiltViewModel<HomeViewModel>()) },
    ),
    AppRoute(
        path = SearchPage.PATH,
        isTopTab = true,
        tab = MuseumTab.Search,
        syntheticStack = { args ->
            listOf(
                GenericNavKey(HomePage.PATH),
                GenericNavKey(SearchPage.PATH, args),
            )
        },
        render = { SearchPage(viewModel = hiltViewModel<SearchViewModel>()) },
    ),
    AppRoute(
        path = SearchResultPage.PATH,
        // 탭 루트는 아니지만 검색 탭 안에 머문다.
        tab = MuseumTab.Search,
        syntheticStack = { args ->
            listOf(
                GenericNavKey(HomePage.PATH),
                GenericNavKey(SearchPage.PATH),
                GenericNavKey(SearchResultPage.PATH, args),
            )
        },
        render = { rawArgs ->
            val args = remember(rawArgs) { SearchResultPage.Args.from(rawArgs) }
            SearchResultPage(
                viewModel = hiltViewModel<SearchResultViewModel, SearchResultViewModel.Factory>(
                    creationCallback = { factory -> factory.create(args) },
                ),
                // 필터 시트는 결과 화면과 같은 백스택 엔트리에 산다(엔트리가 pop 되면 함께 정리된다).
                filterViewModel = hiltViewModel<FilterViewModel>(),
            )
        },
    ),
    AppRoute(
        path = StorePage.PATH,
        isTopTab = true,
        tab = MuseumTab.Library,
        syntheticStack = { args ->
            listOf(
                GenericNavKey(HomePage.PATH),
                GenericNavKey(StorePage.PATH, args),
            )
        },
        render = { LibraryPage(viewModel = hiltViewModel<LibraryViewModel>()) },
    ),
    AppRoute(
        path = DetailPage.PATH,
        // deep-link 로 상세에 바로 진입해도 뒤로가기 시 홈으로 빠지도록 부모 키를 함께 쌓는다.
        syntheticStack = { args ->
            listOf(
                GenericNavKey(HomePage.PATH),
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

/** 탭 → 그 탭의 루트 페이지. 탭을 눌렀을 때 이동할 대상을 찾는 데 쓴다. */
val tabRootRouteByTab: Map<MuseumTab, AppRoute> = appRoutes
    .filter { it.isTopTab }
    .associateBy { route -> checkNotNull(route.tab) { "탭 루트에는 tab 이 있어야 한다: ${route.path}" } }
