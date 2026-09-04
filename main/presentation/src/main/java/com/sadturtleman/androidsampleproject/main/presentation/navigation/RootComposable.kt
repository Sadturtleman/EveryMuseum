package com.sadturtleman.androidsampleproject.main.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.sadturtleman.androidsampleproject.common.domain.navigation.Page
import com.sadturtleman.androidsampleproject.common.presentation.helper.LocalNavigationHelper
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.EveryMuseumTheme
import com.sadturtleman.androidsampleproject.search.domain.SearchPage
import com.sadturtleman.androidsampleproject.store.domain.StorePage

/**
 * 앱 최상위 Composable. 백스택 소유 + 공통 크롬(탭 바 등) + [AppNavHost] 배치를 담당한다.
 *
 * @param startStack deep-link 로 진입한 경우의 시작 백스택. 기본값은 Search 단일 스택.
 */
@Composable
fun RootComposable(
    modifier: Modifier = Modifier,
    startStack: List<NavKey> = listOf(GenericNavKey(SearchPage.PATH)),
) {
    EveryMuseumTheme {
        // rememberNavBackStack 은 @Serializable NavKey 를 저장/복원한다 (프로세스 사망 대응).
        val backStack = rememberNavBackStack(*startStack.toTypedArray())
        val navigationHelper = LocalNavigationHelper.current

        val tabs = listOf(
            TopNavTab("검색", SearchPage),
            TopNavTab("스토어", StorePage),
        )
        val currentKey = backStack.lastOrNull() as? GenericNavKey
        val currentRoute = currentKey?.let { appRouteByPath[it.path] }

        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                // 탭 루트 페이지에서만 탭 바를 노출한다 (Detail 에서는 숨김).
                if (currentRoute?.isTopTab == true) {
                    TopTabBar(
                        tabs = tabs,
                        currentPath = currentKey.path,
                        onTabSelected = { page -> navigationHelper.navigateTo(page) },
                    )
                }
            },
        ) { innerPadding ->
            AppNavHost(
                backStack = backStack,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@Composable
private fun TopTabBar(
    tabs: List<TopNavTab>,
    currentPath: String?,
    onTabSelected: (Page) -> Unit,
) {
    val selectedIndex = tabs.indexOfFirst { it.page.toRoute().path == currentPath }
        .coerceAtLeast(0)

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            // 커스텀 topBar 라 상태바 inset 을 직접 소비해야 한다(기본 TopAppBar 와 달리 자동 적용 X).
            // background 뒤에 적용해 배경은 상태바까지 덮고, 탭 내용만 상태바 아래로 내린다.
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                tabs.forEachIndexed { index, tab ->
                    val selected = index == selectedIndex
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { onTabSelected(tab.page) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = tab.label,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            color = if (selected) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        )
                        if (selected) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .height(2.dp)
                                    .background(MaterialTheme.colorScheme.onSurface),
                            )
                        }
                    }
                }
            }
            HorizontalDivider(thickness = 0.5.dp)
        }
    }
}

private data class TopNavTab(
    val label: String,
    val page: Page,
)
