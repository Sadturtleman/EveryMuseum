package com.sadturtleman.androidsampleproject.detail.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.ArtifactImage
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.ArtifactThumb
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.ArtifactType
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumBadge
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumBadgeTone
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumButton
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumButtonSize
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumButtonStyle
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumMetaRow
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumSectionHeader
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.RelatedCard
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.debouncedClickable
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.MuseumTheme
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.museumLinearGradient

/**
 * 소장품 상세 본문 (Figma: 05 · 소장품 상세).
 *
 * 히어로부터 공공누리 안내까지 한 번에 스크롤된다.
 * 비어 있는 블록(공개 이미지 · 소장품 정보 · 설명 · 연관 소장품)은 통째로 생략한다.
 */
@Composable
internal fun DetailContent(
    state: DetailUiState.Success,
    onIntent: (DetailIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .background(MuseumTheme.colors.bgCanvas),
    ) {
        item(key = "hero") {
            DetailHero(
                type = state.type,
                imageUrl = state.images.getOrNull(state.currentImageIndex)?.url,
                contentDescription = state.nameKr,
                imageCount = state.images.size,
                currentIndex = state.currentImageIndex,
            )
        }

        item(key = "title") {
            DetailTitleBlock(
                state = state,
                onSaveClick = { onIntent(DetailIntent.ToggleSave) },
                onShareClick = { onIntent(DetailIntent.Share) },
            )
        }

        if (state.images.isNotEmpty()) {
            item(key = "images") {
                DetailSection(title = "공개 이미지 ${state.images.size}") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = MuseumTheme.spacing.lg),
                        horizontalArrangement = Arrangement.spacedBy(MuseumTheme.spacing.sm),
                    ) {
                        state.images.forEach { image ->
                            ArtifactThumb(
                                modifier = Modifier
                                    .size(IMAGE_THUMB_SIZE)
                                    .clip(MuseumTheme.shapes.sm)
                                    .debouncedClickable { onIntent(DetailIntent.SelectImage(image)) },
                                type = image.type,
                                shape = MuseumTheme.shapes.sm,
                                image = { ArtifactImage(image.url) },
                            )
                        }
                    }
                }
            }
        }

        if (state.metaRows.isNotEmpty()) {
            item(key = "meta") {
                DetailSection(title = "소장품 정보") {
                    Column(modifier = Modifier.padding(horizontal = MuseumTheme.spacing.lg)) {
                        state.metaRows.forEachIndexed { index, row ->
                            MuseumMetaRow(
                                label = row.label,
                                value = row.value,
                                showDivider = index != state.metaRows.lastIndex,
                            )
                        }
                    }
                }
            }
        }

        if (!state.description.isNullOrBlank()) {
            item(key = "description") {
                DetailSection(title = "설명") {
                    Text(
                        text = state.description,
                        modifier = Modifier.padding(horizontal = MuseumTheme.spacing.lg),
                        style = MuseumTheme.typography.bodySerif,
                        color = MuseumTheme.colors.textPrimary,
                    )
                }
            }
        }

        if (state.related.isNotEmpty()) {
            item(key = "related") {
                DetailSection(
                    header = {
                        MuseumSectionHeader(
                            title = "연관 소장품",
                            onActionClick = { onIntent(DetailIntent.ClickRelatedSeeAll) },
                            modifier = Modifier.padding(horizontal = MuseumTheme.spacing.lg),
                        )
                    },
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = MuseumTheme.spacing.lg),
                        horizontalArrangement = Arrangement.spacedBy(MuseumTheme.spacing.md),
                    ) {
                        state.related.forEach { item ->
                            RelatedCard(
                                reltRelicName = item.nameKr,
                                reltMuseumFullName = item.museum,
                                artifactType = item.type,
                                onClick = { onIntent(DetailIntent.ClickRelated(item.id)) },
                                image = { ArtifactImage(item.imageUrl, item.nameKr) },
                            )
                        }
                    }
                }
            }
        }

        if (state.licenseTitle != null && state.licenseDescription != null) {
            item(key = "license") {
                DetailLicenseBlock(
                    title = state.licenseTitle,
                    description = state.licenseDescription,
                )
            }
        }
    }
}

/**
 * 히어로 (Figma: 05 · 소장품 상세 → hero · imgUri).
 *
 * 상단 그늘은 그 위에 얹히는 투명 앱바의 아이콘이 밝은 이미지 위에서도 보이게 한다.
 */
@Composable
private fun DetailHero(
    type: ArtifactType,
    imageUrl: String?,
    contentDescription: String?,
    imageCount: Int,
    currentIndex: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(HERO_HEIGHT)
            .museumLinearGradient(
                angleDegrees = HERO_GRADIENT_ANGLE,
                0.0147f to HeroGradientStart,
                0.7206f to HeroGradientEnd,
            ),
    ) {
        ArtifactThumb(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = HERO_SILHOUETTE_OFFSET_Y)
                .size(HERO_SILHOUETTE_SIZE),
            type = type,
            // 화면을 대표하는 사진 한 장이라 여기서만 큰 덩어리 로딩을 잰다.
            image = { ArtifactImage(imageUrl, contentDescription, measureTti = true) },
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(HERO_SHADE_HEIGHT)
                .background(
                    Brush.verticalGradient(
                        listOf(HeroShadeTop, Color.Transparent),
                    ),
                ),
        )

        if (imageCount > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(MuseumTheme.spacing.lg)
                    .clip(MuseumTheme.shapes.full)
                    .background(MuseumTheme.colors.bgScrim)
                    .padding(
                        horizontal = MuseumTheme.spacing.md,
                        vertical = COUNTER_PADDING_VERTICAL,
                    ),
            ) {
                Text(
                    text = "${currentIndex + 1} / $imageCount",
                    style = MuseumTheme.typography.labelS,
                    color = MuseumTheme.colors.textInverse,
                )
            }
        }
    }
}

/** 뱃지 · 이름 · 크레딧 · 액션 (Figma: 05 · 소장품 상세 → title block). */
@Composable
private fun DetailTitleBlock(
    state: DetailUiState.Success,
    onSaveClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MuseumTheme.colors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.bgSurface)
            .padding(
                horizontal = MuseumTheme.spacing.lg,
                vertical = MuseumTheme.spacing.xl,
            ),
        verticalArrangement = Arrangement.spacedBy(MuseumTheme.spacing.sm),
    ) {
        if (state.designation != null || state.era != null || state.license != null) {
            Row(horizontalArrangement = Arrangement.spacedBy(MuseumTheme.spacing.xs)) {
                state.designation?.let { MuseumBadge(label = it, tone = MuseumBadgeTone.Gold) }
                state.era?.let { MuseumBadge(label = it, tone = MuseumBadgeTone.Accent) }
                state.license?.let { MuseumBadge(label = it, tone = MuseumBadgeTone.Neutral) }
            }
        }

        Text(
            text = state.nameKr,
            style = MuseumTheme.typography.displaySerif.copy(
                fontSize = DETAIL_TITLE_SIZE,
                lineHeight = DETAIL_TITLE_LINE_HEIGHT,
                letterSpacing = DETAIL_TITLE_LETTER_SPACING,
            ),
            color = colors.textPrimary,
        )

        if (!state.nameCn.isNullOrBlank()) {
            Text(
                text = state.nameCn,
                style = MuseumTheme.typography.bodySerif,
                color = colors.textTertiary,
            )
        }

        Text(
            text = state.credit,
            style = MuseumTheme.typography.labelM,
            color = colors.textSecondary,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = MuseumTheme.spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(MuseumTheme.spacing.sm),
        ) {
            MuseumButton(
                label = if (state.saved) "보관함에서 빼기" else "보관함에 저장",
                onClick = onSaveClick,
                modifier = Modifier.weight(1f),
                style = MuseumButtonStyle.Primary,
                size = MuseumButtonSize.M,
            )
            MuseumButton(
                label = "공유",
                onClick = onShareClick,
                style = MuseumButtonStyle.Secondary,
                size = MuseumButtonSize.M,
            )
        }
    }
}

/**
 * 흰 표면 위의 한 블록. 위쪽에 캔버스 색 간격을 두어 블록 사이를 나눈다.
 *
 * @param title 기본 헤더. 전체보기 액션이 필요하면 [header] 로 직접 넘긴다.
 */
@Composable
private fun DetailSection(
    modifier: Modifier = Modifier,
    title: String? = null,
    header: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(SECTION_GAP),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MuseumTheme.colors.bgSurface)
                .padding(
                    top = MuseumTheme.spacing.xl,
                    bottom = SECTION_PADDING_BOTTOM,
                ),
        ) {
            when {
                header != null -> header()
                title != null -> Text(
                    text = title,
                    modifier = Modifier.padding(horizontal = MuseumTheme.spacing.lg),
                    style = MuseumTheme.typography.titleM,
                    color = MuseumTheme.colors.textPrimary,
                )
            }
            Box(modifier = Modifier.height(SECTION_TITLE_GAP))
            content()
        }
    }
}

/** 공공누리 안내 (Figma: 05 · 소장품 상세 → 공공누리 (glsv)). */
@Composable
private fun DetailLicenseBlock(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(SECTION_GAP),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MuseumTheme.colors.bgSurface)
                .padding(horizontal = MuseumTheme.spacing.lg)
                .padding(top = LICENSE_PADDING_TOP, bottom = LICENSE_PADDING_BOTTOM),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MuseumTheme.shapes.sm)
                    .background(MuseumTheme.colors.bgSurfaceSunken)
                    .padding(horizontal = MuseumTheme.spacing.lg)
                    .padding(top = LICENSE_BOX_PADDING_TOP, bottom = MuseumTheme.spacing.lg),
                verticalArrangement = Arrangement.spacedBy(LICENSE_BOX_GAP),
            ) {
                Text(
                    text = title,
                    style = MuseumTheme.typography.labelM,
                    color = MuseumTheme.colors.textPrimary,
                )
                Text(
                    text = description,
                    style = MuseumTheme.typography.caption,
                    color = MuseumTheme.colors.textSecondary,
                )
            }
        }
    }
}

// 히어로
private val HERO_HEIGHT = 430.dp
private val HERO_SILHOUETTE_SIZE = 240.dp

/** 시안에서 실루엣이 세로 중앙보다 16dp 아래에 놓여 있다. */
private val HERO_SILHOUETTE_OFFSET_Y = 16.dp
private val HERO_SHADE_HEIGHT = 180.dp
private val COUNTER_PADDING_VERTICAL = 6.dp

/** Figma: `linear-gradient(117.98deg, rgb(242,237,229) 1.47%, rgb(218,209,198) 72.06%)` */
private const val HERO_GRADIENT_ANGLE = 117.9848f
private val HeroGradientStart = Color(0xFFF2EDE5)
private val HeroGradientEnd = Color(0xFFDAD1C6)
private val HeroShadeTop = Color(0x6614110F)

// 제목 (Display/Serif 32 와 Title/Serif L 24 사이의 1회성 값)
private val DETAIL_TITLE_SIZE = 28.sp
private val DETAIL_TITLE_LINE_HEIGHT = 39.2.sp
private val DETAIL_TITLE_LETTER_SPACING = (-0.02f).em

// 블록
private val SECTION_GAP = 12.dp
private val SECTION_TITLE_GAP = 12.dp
private val SECTION_PADDING_BOTTOM = 24.dp
private val IMAGE_THUMB_SIZE = 104.dp
private val LICENSE_PADDING_TOP = 4.dp
private val LICENSE_PADDING_BOTTOM = 32.dp
private val LICENSE_BOX_PADDING_TOP = 14.dp
private val LICENSE_BOX_GAP = 6.dp
