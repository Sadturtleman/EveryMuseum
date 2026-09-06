package com.sadturtleman.androidsampleproject.search.presentation.filter

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumButton
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumButtonSize
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumButtonStyle
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumEmptyView
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumErrorView
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumIcons
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumLoadingView
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.debouncedClickable
import com.sadturtleman.androidsampleproject.common.presentation.ui.preview.PREVIEW_DEVICE
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.EveryMuseumTheme
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.MuseumRadius
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.MuseumTheme
import com.sadturtleman.androidsampleproject.search.presentation.result.AppliedFilterUiModel

/**
 * 필터 바텀시트 (Figma: 최종 → 04 · 필터).
 *
 * 시트 표면 자체를 그린다. 뒤에 깔리는 스크림과 드래그 동작은 이 컴포저블을 띄우는 쪽
 * (`ModalBottomSheet` 등)이 담당한다.
 *
 * 입력은 [FilterUiState] 하나, 출력은 [FilterIntent] 하나다.
 */
@Composable
internal fun FilterView(
    state: FilterUiState,
    onIntent: (FilterIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MuseumTheme.colors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = MuseumRadius.Xl, topEnd = MuseumRadius.Xl))
            .background(colors.bgSurface),
    ) {
        SheetHandle()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SHEET_PADDING_HORIZONTAL)
                .padding(top = MuseumTheme.spacing.sm, bottom = TITLE_PADDING_BOTTOM),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "필터",
                style = MuseumTheme.typography.titleL,
                color = colors.textPrimary,
            )
            Text(
                text = "초기화",
                modifier = Modifier.debouncedClickable { onIntent(FilterIntent.Reset) },
                style = MuseumTheme.typography.labelM,
                color = colors.textTertiary,
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            when (state) {
                is FilterUiState.Loading -> Column(modifier = Modifier.fillMaxSize()) {
                    // 탭은 이미 알고 있으므로 유지한 채 옵션 자리만 로딩으로 둔다.
                    FilterTabRow(
                        tabs = state.tabs,
                        selectedTabCode = state.selectedTabCode,
                        onTabSelect = { tab -> onIntent(FilterIntent.SelectTab(tab)) },
                    )
                    Box(modifier = Modifier.weight(1f)) { MuseumLoadingView() }
                }

                is FilterUiState.Error -> MuseumErrorView(
                    message = state.message,
                    onRetry = { onIntent(FilterIntent.Retry) },
                )

                is FilterUiState.Success -> if (state.isEmpty) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        FilterTabRow(
                            tabs = state.tabs,
                            selectedTabCode = state.selectedTabCode,
                            onTabSelect = { tab -> onIntent(FilterIntent.SelectTab(tab)) },
                        )
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center,
                        ) {
                            MuseumEmptyView(
                                title = "선택할 코드가 없습니다",
                                description = "다른 분류를 선택해보세요.",
                                icon = MuseumIcons.Layers,
                            )
                        }
                    }
                } else {
                    FilterContent(
                        state = state,
                        onIntent = onIntent,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }

        FilterActionBar(
            resultCount = (state as? FilterUiState.Success)?.resultCount,
            onReset = { onIntent(FilterIntent.Reset) },
            onApply = { onIntent(FilterIntent.Apply) },
        )
    }
}

/** 시트 상단 드래그 핸들 (Figma: 04 · 필터 → handle). */
@Composable
private fun SheetHandle(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(HANDLE_ROW_HEIGHT),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Box(
            modifier = Modifier
                .size(width = HANDLE_WIDTH, height = HANDLE_HEIGHT)
                .clip(MuseumTheme.shapes.full)
                .background(MuseumTheme.colors.borderDefault),
        )
    }
}

/** 초기화 · 결과 보기 (Figma: 04 · 필터 → action bar). */
@Composable
private fun FilterActionBar(
    resultCount: Int?,
    onReset: () -> Unit,
    onApply: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = MuseumTheme.colors.borderSubtle

    Row(
        modifier = modifier
            .fillMaxWidth()
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
            .padding(horizontal = MuseumTheme.spacing.lg)
            .padding(top = MuseumTheme.spacing.md, bottom = ACTION_BAR_PADDING_BOTTOM),
        horizontalArrangement = Arrangement.spacedBy(ACTION_BAR_GAP),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MuseumButton(
            label = "초기화",
            onClick = onReset,
            style = MuseumButtonStyle.Secondary,
            size = MuseumButtonSize.L,
        )
        MuseumButton(
            label = if (resultCount != null) "${resultCount}건 결과 보기" else "결과 보기",
            onClick = onApply,
            modifier = Modifier.weight(1f),
            style = MuseumButtonStyle.Primary,
            size = MuseumButtonSize.L,
            enabled = resultCount != null,
        )
    }
}

private val SHEET_PADDING_HORIZONTAL = 20.dp
private val TITLE_PADDING_BOTTOM = 14.dp
private val HANDLE_ROW_HEIGHT = 20.dp
private val HANDLE_WIDTH = 40.dp
private val HANDLE_HEIGHT = 4.dp
private val ACTION_BAR_GAP = 10.dp

/** 홈 인디케이터 안전영역을 감안한 하단 여백. */
private val ACTION_BAR_PADDING_BOTTOM = 28.dp

// ---------------------------------------------------------------------------
// Preview
// ---------------------------------------------------------------------------

/** 시트만 따로 보면 맥락이 없어 스크림 위에 얹어 시안 프레임과 같은 모양으로 보여준다. */
@Composable
private fun FilterViewPreviewHost(state: FilterUiState) {
    EveryMuseumTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MuseumTheme.colors.bgCanvas),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MuseumTheme.colors.bgScrim.copy(alpha = SCRIM_ALPHA)),
            )
            FilterView(
                state = state,
                onIntent = {},
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxHeight(SHEET_HEIGHT_FRACTION),
            )
        }
    }
}

private const val SCRIM_ALPHA = 0.5f

/** 시안의 시트 높이(646 / 852)에 맞춘 비율. */
private const val SHEET_HEIGHT_FRACTION = 0.758f

@Preview(name = "필터 · 성공 · Light", device = PREVIEW_DEVICE, showBackground = true)
@Composable
private fun FilterViewSuccessPreview() {
    FilterViewPreviewHost(FilterPreviewData.success)
}

@Preview(
    name = "필터 · 성공 · Dark",
    device = PREVIEW_DEVICE,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun FilterViewSuccessDarkPreview() {
    FilterViewPreviewHost(FilterPreviewData.success)
}

@Preview(name = "필터 · 선택 없음", device = PREVIEW_DEVICE, showBackground = true)
@Composable
private fun FilterViewNoSelectionPreview() {
    FilterViewPreviewHost(FilterPreviewData.success.copy(selectedFilters = emptyList()))
}

@Preview(name = "필터 · 로딩", device = PREVIEW_DEVICE, showBackground = true)
@Composable
private fun FilterViewLoadingPreview() {
    FilterViewPreviewHost(
        FilterUiState.Loading(
            tabs = FilterPreviewData.tabs,
            selectedTabCode = "PS06",
        ),
    )
}

@Preview(name = "필터 · 에러", device = PREVIEW_DEVICE, showBackground = true)
@Composable
private fun FilterViewErrorPreview() {
    FilterViewPreviewHost(FilterUiState.Error("코드 목록을 불러오지 못했습니다."))
}

@Preview(name = "필터 · 옵션 없음", device = PREVIEW_DEVICE, showBackground = true)
@Composable
private fun FilterViewEmptyPreview() {
    FilterViewPreviewHost(
        FilterPreviewData.success.copy(options = emptyList(), selectedFilters = emptyList()),
    )
}

private object FilterPreviewData {

    val tabs = listOf(
        FilterTabUiModel(parentCode = "PS06", label = "국적 · 시대"),
        FilterTabUiModel(parentCode = "PS01", label = "소장기관"),
        FilterTabUiModel(parentCode = "PS08", label = "재질"),
        FilterTabUiModel(parentCode = "PS09", label = "용도"),
        FilterTabUiModel(parentCode = "PS15", label = "크기"),
        FilterTabUiModel(parentCode = "GL05", label = "출토지"),
        FilterTabUiModel(parentCode = "PS12", label = "지정문화재"),
    )

    val success = FilterUiState.Success(
        tabs = tabs,
        selectedTabCode = "PS06",
        options = listOf(
            FilterOptionUiModel(code = "PS06001010", label = "한국 · 신라"),
            FilterOptionUiModel(code = "PS06001009", label = "한국 · 백제"),
            FilterOptionUiModel(code = "PS06001008", label = "한국 · 고구려"),
            FilterOptionUiModel(code = "PS06001011", label = "한국 · 통일신라"),
            FilterOptionUiModel(code = "PS06001015", label = "한국 · 고려"),
            FilterOptionUiModel(code = "PS06001018", label = "한국 · 조선"),
            FilterOptionUiModel(code = "PS06001020", label = "한국 · 대한제국"),
        ),
        selectedFilters = listOf(AppliedFilterUiModel(code = "PS06001009", label = "한국 · 백제")),
        resultCount = 93,
    )
}
