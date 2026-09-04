package com.sadturtleman.androidsampleproject.common.presentation.ui.component

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.EveryMuseumTheme
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.MuseumTheme

/**
 * 필터 · 카테고리 토글 (Figma: C04 · Chip).
 *
 * `view_code_list` 로 받은 코드 트리의 한 노드를 표현한다.
 * 하위 코드가 있는 경우 [trailingIcon] 에 [MuseumIcons.ChevronDown] 을 넘긴다.
 *
 * @param selected 선택 시 배경·테두리·글자색이 모두 브랜드 계열로 바뀐다.
 * @param leadingIcon 16dp 로 그려진다. 색은 라벨과 동일하게 맞춘다.
 */
@Composable
fun MuseumChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    @DrawableRes leadingIcon: Int? = null,
    @DrawableRes trailingIcon: Int? = null,
) {
    val colors = MuseumTheme.colors
    val shape = MuseumTheme.shapes.full
    val background = if (selected) colors.bgBrandSubtle else colors.bgSurface
    val border = if (selected) colors.borderBrand else colors.borderDefault
    val content = if (selected) colors.textBrand else colors.textPrimary

    Row(
        modifier = modifier
            .selectable(
                selected = selected,
                enabled = enabled,
                role = Role.Tab,
                onClick = onClick,
            )
            .background(background, shape)
            .border(1.dp, border, shape)
            .padding(
                horizontal = MuseumTheme.spacing.md,
                vertical = MuseumTheme.spacing.sm,
            ),
        horizontalArrangement = Arrangement.spacedBy(MuseumTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) {
            MuseumIcon(
                id = leadingIcon,
                contentDescription = null,
                tint = content,
                size = MuseumTheme.size.iconXs,
            )
        }
        Text(
            text = label,
            style = MuseumTheme.typography.labelM,
            color = content,
        )
        if (trailingIcon != null) {
            MuseumIcon(
                id = trailingIcon,
                contentDescription = null,
                tint = content,
                size = MuseumTheme.size.iconXs,
            )
        }
    }
}

@Preview(name = "Chip · Light", showBackground = true, backgroundColor = 0xFFF7F4EE)
@Preview(
    name = "Chip · Dark",
    showBackground = true,
    backgroundColor = 0xFF14110F,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun MuseumChipPreview() {
    EveryMuseumTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MuseumChip(
                label = "조선",
                selected = false,
                onClick = {},
                leadingIcon = MuseumIcons.Clock,
            )
            MuseumChip(
                label = "조선",
                selected = true,
                onClick = {},
                leadingIcon = MuseumIcons.Clock,
                trailingIcon = MuseumIcons.ChevronDown,
            )
        }
    }
}
