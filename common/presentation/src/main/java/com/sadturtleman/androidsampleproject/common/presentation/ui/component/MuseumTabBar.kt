package com.sadturtleman.androidsampleproject.common.presentation.ui.component

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sadturtleman.androidsampleproject.common.presentation.R
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.EveryMuseumTheme
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.MuseumTheme

/**
 * 하단 탭 (Figma: C16 · TabBar → Active).
 */
enum class MuseumTab(
    @param:StringRes internal val labelRes: Int,
    @param:DrawableRes internal val icon: Int,
) {
    Home(R.string.museum_tab_home, R.drawable.ic_home),
    Search(R.string.museum_tab_search, R.drawable.ic_search),
    Library(R.string.museum_tab_library, R.drawable.ic_bookmark),
    Profile(R.string.museum_tab_profile, R.drawable.ic_user),
}

/**
 * 하단 내비게이션 (Figma: C16 · TabBar).
 *
 * 활성 탭은 `color/icon/brand` + `color/text/brand`, 비활성은 secondary 계열이다.
 * 높이는 64dp 이며, 홈 인디케이터 영역은 이 컴포넌트가 그리지 않는다.
 * `Scaffold` 의 bottomBar 로 쓰거나 `Modifier.navigationBarsPadding()` 을 함께 적용한다.
 */
@Composable
fun MuseumTabBar(
    selected: MuseumTab,
    onSelect: (MuseumTab) -> Unit,
    modifier: Modifier = Modifier,
    tabs: List<MuseumTab> = MuseumTab.entries,
) {
    val borderColor = MuseumTheme.colors.borderSubtle

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(MuseumTheme.size.tabBarHeight)
            .background(MuseumTheme.colors.bgSurface)
            .drawBehind {
                val stroke = 1.dp.toPx()
                drawLine(
                    color = borderColor,
                    start = Offset(0f, stroke / 2f),
                    end = Offset(size.width, stroke / 2f),
                    strokeWidth = stroke,
                )
            }
            .padding(vertical = MuseumTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        tabs.forEach { tab ->
            MuseumTabItem(
                tab = tab,
                selected = tab == selected,
                onClick = { onSelect(tab) },
            )
        }
    }
}

@Composable
private fun RowScope.MuseumTabItem(
    tab: MuseumTab,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = MuseumTheme.colors
    val label = stringResource(tab.labelRes)

    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .selectable(
                selected = selected,
                role = Role.Tab,
                onClick = rememberDebouncedClick(onClick = onClick),
            ),
        verticalArrangement = Arrangement.spacedBy(
            MuseumTheme.spacing.xs,
            Alignment.CenterVertically,
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MuseumIcon(
            id = tab.icon,
            contentDescription = null,
            tint = if (selected) colors.iconBrand else colors.iconSecondary,
        )
        Text(
            text = label,
            style = if (selected) {
                MuseumTheme.typography.labelS.copy(fontWeight = FontWeight.Bold)
            } else {
                MuseumTheme.typography.labelS
            },
            color = if (selected) colors.textBrand else colors.textTertiary,
        )
    }
}

@Preview(name = "TabBar · Light", showBackground = true, backgroundColor = 0xFFF7F4EE)
@Preview(
    name = "TabBar · Dark",
    showBackground = true,
    backgroundColor = 0xFF14110F,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun MuseumTabBarPreview() {
    EveryMuseumTheme {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            MuseumTabBar(selected = MuseumTab.Home, onSelect = {})
            MuseumTabBar(selected = MuseumTab.Library, onSelect = {})
        }
    }
}
