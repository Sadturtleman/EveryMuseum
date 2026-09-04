package com.sadturtleman.androidsampleproject.common.presentation.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.EveryMuseumTheme
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.MuseumTheme

/**
 * 필터 코드 옵션 한 행 (Figma: C10 · FilterOptionRow).
 *
 * 필터 바텀시트에서 `view_code_list` 결과 한 건(code + nameKr)을 나타낸다.
 * 선택된 code 가 `view_relic_list` 파라미터로 전달된다.
 */
@Composable
fun MuseumFilterOptionRow(
    label: String,
    code: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = MuseumTheme.colors
    val shape = MuseumTheme.shapes.sm

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (selected) colors.bgBrandSubtle else Color.Transparent)
            .selectable(
                selected = selected,
                enabled = enabled,
                role = Role.RadioButton,
                onClick = onClick,
            )
            .padding(MuseumTheme.spacing.lg),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(MuseumTheme.spacing.xxs)) {
            Text(
                text = label,
                style = MuseumTheme.typography.bodyM,
                color = if (selected) colors.textBrand else colors.textPrimary,
            )
            Text(
                text = code,
                style = MuseumTheme.typography.labelS,
                color = colors.textTertiary,
            )
        }
        if (selected) {
            MuseumIcon(
                id = MuseumIcons.Check,
                contentDescription = null,
                tint = colors.iconBrand,
                size = MuseumTheme.size.iconSm,
            )
        }
    }
}

@Preview(name = "FilterOptionRow · Light", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Preview(
    name = "FilterOptionRow · Dark",
    showBackground = true,
    backgroundColor = 0xFF1E1A17,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun MuseumFilterOptionRowPreview() {
    EveryMuseumTheme {
        Column(modifier = Modifier.padding(8.dp)) {
            MuseumFilterOptionRow(
                label = "조선",
                code = "PS06001018",
                selected = false,
                onClick = {},
            )
            MuseumFilterOptionRow(
                label = "고려",
                code = "PS06001017",
                selected = true,
                onClick = {},
            )
        }
    }
}
