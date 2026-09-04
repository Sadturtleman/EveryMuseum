package com.sadturtleman.androidsampleproject.main.presentation.navigation

import android.util.Log
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.sadturtleman.androidsampleproject.common.domain.navigation.NavRoute
import com.sadturtleman.androidsampleproject.common.domain.navigation.NavSignal
import com.sadturtleman.androidsampleproject.common.presentation.helper.LocalNavigationHelper
import com.sadturtleman.androidsampleproject.search.domain.SearchPage

/**
 * 백스택을 실제로 조작하는 유일한 지점.
 *
 * 각 feature 는 NavigationHelper 로 신호만 보내고, 여기서 신호를 받아 [NavBackStack] 을 갱신한다.
 */
@Composable
fun AppNavHost(
    backStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier,
) {
    val navigationHelper = LocalNavigationHelper.current

    LaunchedEffect(Unit) {
        navigationHelper.navigationFlow.collect { signal ->
            when (signal) {
                is NavSignal.GoToDestPage -> handleNavRoute(signal.route, backStack)
                NavSignal.Back -> backStack.removeLastOrNull()
            }
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        modifier = modifier,
        transitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
        popTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
        predictivePopTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
        entryDecorators = listOf(
            // 각 엔트리의 rememberSaveable 상태를 백스택 단위로 보존.
            rememberSaveableStateHolderNavEntryDecorator(),
            // 각 엔트리에 독립적인 ViewModelStore 를 부여. pop 시 onCleared 가 호출된다.
            rememberViewModelStoreNavEntryDecorator(),
        ),
        // 단일 GenericNavKey 디스패처. 실제 화면 결정은 [appRouteByPath] 가 담당한다.
        entryProvider = entryProvider {
            entry<GenericNavKey> { navKey ->
                val route = appRouteByPath[navKey.path]
                if (route == null) {
                    // 등록되지 않은 path 가 백스택 복원 등으로 들어온 경우의 안전망.
                    // 컴포지션 중 부수효과가 반복 발생하지 않도록 LaunchedEffect 로 감싼다.
                    LaunchedEffect(navKey.path) {
                        Log.w(TAG, "Unknown path on render: ${navKey.path}")
                        navigationHelper.navigateTo(SearchPage)
                    }
                    return@entry
                }
                route.render(navKey.args)
            }
        },
    )
}

private const val TAG = "[Navigation]"
private const val EMPTY_BACKSTACK = -1

/**
 * NavRoute 한 건을 받아 백스택에 push.
 * Search 로의 이동은 "스택 정리 후 단일 Search 유지" 시맨틱을 가진다.
 * 미등록 path 는 무시 + 경고 로그.
 */
fun handleNavRoute(route: NavRoute, backStack: NavBackStack<NavKey>) {
    if (appRouteByPath[route.path] == null) {
        Log.w(TAG, "Unhandled NavRoute: ${route.path}")
        return
    }
    val navKey = GenericNavKey.of(route)
    if (route.path == SearchPage.PATH) {
        navigateToSearchStack(backStack)
    } else {
        // 같은 키가 연속으로 쌓이는 중복 push 방지(더블 탭 등).
        if (backStack.lastOrNull() != navKey) {
            backStack.add(navKey)
            Log.d(TAG, "navigateTo: $navKey")
        }
    }
}

private fun navigateToSearchStack(backStack: NavBackStack<NavKey>) {
    val searchIndex = backStack.indexOfFirst { it is GenericNavKey && it.path == SearchPage.PATH }
    if (searchIndex == EMPTY_BACKSTACK) {
        // Search 가 없는 상태에서 진입 -> 스택을 비우고 Search 단일 스택으로 시작.
        backStack.clear()
        backStack.add(GenericNavKey(SearchPage.PATH))
        Log.d(TAG, "navigateTo: Search (fresh stack)")
    } else {
        // 이미 스택에 Search 가 있으면 그 위를 걷어내 Search 로 되돌아간다.
        while (backStack.size > searchIndex + 1) {
            backStack.removeAt(backStack.lastIndex)
        }
        Log.d(TAG, "BackNavigate To: Search")
    }
}
