package com.sadturtleman.androidsampleproject.common.presentation.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.EveryMuseumTheme
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.MuseumTheme

/**
 * 상세 화면 스펙 테이블의 한 행 (Figma: C09 · MetaRow).
 *
 * `view_relic_detail` 의 nationalityName1/2, materialName1/2, purposeName1~4,
 * sizeInfo, placeLandName1/2, museumName2/3, designationInfo 를 한 줄씩 표기한다.
 *
 * 행 아래에 `color/border/subtle` 구분선이 그려진다. 마지막 행에서 선을 없애려면
 * [showDivider] 를 false 로 넘긴다.
 */
@Composable
fun MuseumMetaRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true,
) {
    val dividerColor = MuseumTheme.colors.borderSubtle

    Row(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                if (!showDivider) return@drawBehind
                val stroke = 1.dp.toPx()
                drawLine(
                    color = dividerColor,
                    start = Offset(0f, size.height - stroke / 2f),
                    end = Offset(size.width, size.height - stroke / 2f),
                    strokeWidth = stroke,
                )
            }
            .padding(vertical = MuseumTheme.spacing.md),
        horizontalArrangement = Arrangement.spacedBy(MuseumTheme.spacing.lg),
    ) {
        Text(
            text = label,
            modifier = Modifier.width(META_LABEL_WIDTH),
            style = MuseumTheme.typography.labelM,
            color = MuseumTheme.colors.textTertiary,
        )
        Text(
            text = value,
            modifier = Modifier.weight(1f),
            style = MuseumTheme.typography.bodyS,
            color = MuseumTheme.colors.textPrimary,
        )
    }
}

/** Figma 에 고정폭으로 잡혀 있는 라벨 열 너비. */
private val META_LABEL_WIDTH = 96.dp

@Preview(name = "MetaRow · Light", showBackground = true, backgroundColor = 0xFFF7F4EE)
@Preview(
    name = "MetaRow · Dark",
    showBackground = true,
    backgroundColor = 0xFF14110F,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun MuseumMetaRowPreview() {
    EveryMuseumTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            MuseumMetaRow(label = "국적 / 시대", value = "한국 · 조선")
            MuseumMetaRow(label = "재질", value = "종이")
            MuseumMetaRow(
                label = "크기",
                value = "세로 20.9cm, 가로 14.9cm",
                showDivider = false,
            )
        }
    }
}
