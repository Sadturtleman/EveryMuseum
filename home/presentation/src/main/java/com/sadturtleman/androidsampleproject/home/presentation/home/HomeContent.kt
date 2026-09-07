package com.sadturtleman.androidsampleproject.home.presentation.home

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.ArtifactImage
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.CollectionCard
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumBadge
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumBadgeTone
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumChip
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumEmptyView
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumIcons
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumSearchBar
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumSectionHeader
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.debouncedClickable
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.MuseumColors
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.MuseumTheme
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.museumLinearGradient
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.museumShadow

/**
 * 홈 본문 (Figma: 01 · 홈 → scroll).
 *
 * 상태 분기 없이 성공 상태만 그리는 순수 UI 다. 분기는 [HomeView] 가 담당한다.
 */
@Composable
internal fun HomeContent(
    state: HomeUiState.Success,
    onIntent: (HomeIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val rows = state.items.chunked(GRID_COLUMNS)

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            start = MuseumTheme.spacing.lg,
            end = MuseumTheme.spacing.lg,
            top = MuseumTheme.spacing.sm,
            bottom = MuseumTheme.spacing.lg,
        ),
    ) {
        item(key = "searchBar") {
            // 홈의 검색 바는 입력을 받지 않고 검색 화면으로 넘기는 진입점이다.
            MuseumSearchBar(
                value = "",
                onValueChange = {},
                onClick = { onIntent(HomeIntent.OpenSearch) },
            )
        }

        if (state.hero != null) {
            item(key = "hero") {
                HomeHeroCard(
                    hero = state.hero,
                    onClick = { onIntent(HomeIntent.ClickHero) },
                    modifier = Modifier.padding(top = SECTION_GAP),
                )
            }
        }

        item(key = "eras") {
            Column(modifier = Modifier.padding(top = SECTION_GAP)) {
                MuseumSectionHeader(title = "시대로 둘러보기")
                Row(
                    modifier = Modifier
                        .padding(top = ERA_HEADER_GAP)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(MuseumTheme.spacing.sm),
                ) {
                    state.eras.forEach { era ->
                        MuseumChip(
                            label = era.label,
                            selected = era.code == state.selectedEraCode,
                            onClick = { onIntent(HomeIntent.SelectEra(era)) },
                        )
                    }
                }
            }
        }

        item(key = "todayHeader") {
            MuseumSectionHeader(
                title = "오늘의 소장품",
                onActionClick = { onIntent(HomeIntent.ClickSeeAll) },
                modifier = Modifier.padding(top = SECTION_GAP),
            )
        }

        if (rows.isEmpty()) {
            item(key = "empty") {
                // 히어로·시대 칩은 그대로 두고 그리드 자리에만 빈 상태를 그린다.
                MuseumEmptyView(
                    title = "표시할 소장품이 없습니다",
                    description = "다른 시대를 선택해보세요.",
                    icon = MuseumIcons.Grid,
                    modifier = Modifier.padding(top = HEADER_TO_GRID_GAP),
                )
            }
        }

        itemsIndexed(rows, key = { _, row -> row.first().id }) { index, row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = if (index == 0) HEADER_TO_GRID_GAP else GRID_ROW_GAP),
                horizontalArrangement = Arrangement.spacedBy(GRID_COLUMN_GAP),
            ) {
                row.forEach { item ->
                    CollectionCard(
                        modifier = Modifier.weight(1f),
                        nameKr = item.nameKr,
                        meta = item.museum,
                        artifactType = item.type,
                        designationName1 = item.designation,
                        nationalityName2 = item.era,
                        onClick = { onIntent(HomeIntent.ClickItem(item.id)) },
                        onSaveClick = { onIntent(HomeIntent.ToggleSave(item.id)) },
                        saved = item.saved,
                        image = { ArtifactImage(item.imageUrl, item.nameKr) },
                    )
                }
                // 마지막 줄이 홀수면 남은 칸을 비워 카드 폭을 유지한다.
                repeat(GRID_COLUMNS - row.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * 큐레이션 히어로 카드 (Figma: 01 · 홈 → 큐레이션 히어로).
 *
 * 소장품 사진 위에 먹색 그라디언트를 얹고 그 위에 글자를 올린다.
 * 사진이 밝아도 글자가 읽히도록 그라디언트가 스크림 역할을 하므로,
 * 글자색은 라이트/다크 모드와 무관하게 `color/text/on-dark` 계열을 쓴다.
 *
 * 사진이 없으면(주소가 비었거나 로딩 전) 그라디언트만 남아 원래의 먹색 카드가 된다.
 */
@Composable
private fun HomeHeroCard(
    hero: HomeHeroUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MuseumTheme.colors
    val shape = MuseumTheme.shapes.lg

    Box(
        modifier = modifier
            .fillMaxWidth()
            .museumShadow(MuseumTheme.elevation.level2, shape)
            .clip(shape)
            .debouncedClickable(onClick = onClick),
    ) {
        // 제목이 바로 아래 글자로 읽히므로 사진에는 설명을 달지 않는다.
        // 화면을 대표하는 사진 한 장이라 여기서만 큰 덩어리 로딩을 잰다.
        ArtifactImage(url = hero.imageUrl, measureTti = true)

        Box(
            modifier = Modifier
                .matchParentSize()
                // 사진이 없을 때는 이 그라디언트가 카드 배경 그 자체라 불투명하게 둔다.
                .alpha(if (hero.imageUrl.isNullOrBlank()) 1f else HERO_SCRIM_ALPHA)
                .museumLinearGradient(
                    angleDegrees = HERO_GRADIENT_ANGLE,
                    0.034f to HeroGradientStart,
                    0.7143f to HeroGradientEnd,
                ),
        )

        HeroTexts(
            hero = hero,
            colors = colors,
            modifier = Modifier.padding(
                horizontal = HERO_PADDING_HORIZONTAL,
                vertical = HERO_PADDING_VERTICAL,
            ),
        )
    }
}

/** 히어로의 글자 묶음. 사진 · 스크림 위에 얹힌다. */
@Composable
private fun HeroTexts(
    hero: HomeHeroUiModel,
    colors: MuseumColors,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(HERO_GAP),
    ) {
        if (hero.designation != null || hero.label != null) {
            Row(horizontalArrangement = Arrangement.spacedBy(MuseumTheme.spacing.xs)) {
                hero.designation?.let {
                    MuseumBadge(label = it, tone = MuseumBadgeTone.Gold)
                }
                hero.label?.let {
                    MuseumBadge(label = it, tone = MuseumBadgeTone.Neutral)
                }
            }
        }
        Text(
            text = hero.nameKr,
            style = MuseumTheme.typography.titleSerifL.copy(
                fontWeight = FontWeight.Bold,
                fontSize = HERO_TITLE_SIZE,
                lineHeight = HERO_TITLE_LINE_HEIGHT,
                letterSpacing = HERO_TITLE_LETTER_SPACING,
            ),
            color = colors.textOnDark,
        )
        Text(
            text = hero.summary,
            style = MuseumTheme.typography.bodyS,
            color = colors.textOnDarkMuted,
        )
        Text(
            text = hero.meta,
            style = MuseumTheme.typography.labelS,
            color = colors.textOnDarkMuted,
        )
    }
}

private const val GRID_COLUMNS = 2

/** scroll 의 flex gap. 검색 바 · 히어로 · 시대 · 오늘의 소장품 사이 간격이다. */
private val SECTION_GAP = 28.dp
private val ERA_HEADER_GAP = 10.dp
private val HEADER_TO_GRID_GAP = 12.dp
private val GRID_ROW_GAP = 20.dp
private val GRID_COLUMN_GAP = 12.dp

// 히어로는 타입 스케일 · 간격 스케일에 없는 1회성 값이 섞여 있어 여기서만 정의한다.
private val HERO_PADDING_HORIZONTAL = 20.dp
private val HERO_PADDING_VERTICAL = 22.dp
private val HERO_GAP = 10.dp
private val HERO_TITLE_SIZE = 25.sp
private val HERO_TITLE_LINE_HEIGHT = 35.sp
private val HERO_TITLE_LETTER_SPACING = (-0.02f).em

/** Figma: `linear-gradient(138.49deg, rgb(30,26,23) 3.4%, rgb(75,64,55) 71.43%)` */
private const val HERO_GRADIENT_ANGLE = 138.4906f

/** 사진 위에 얹을 때의 스크림 농도. 유물이 비치면서 글자는 읽히는 지점. */
private const val HERO_SCRIM_ALPHA = 0.72f
private val HeroGradientStart = Color(0xFF1E1A17)
private val HeroGradientEnd = Color(0xFF4B4037)
