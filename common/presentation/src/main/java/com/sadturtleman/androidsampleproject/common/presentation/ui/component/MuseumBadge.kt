package com.sadturtleman.androidsampleproject.common.presentation.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.EveryMuseumTheme
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.MuseumTheme

/**
 * 뱃지 색조 (Figma: C03 · Badge → Tone).
 */
enum class MuseumBadgeTone {
    /** 국보 · 보물 (designationName1) */
    Gold,

    /** 브랜드 강조 */
    Brand,

    /** 국적 · 시대 (nationalityName2) */
    Accent,

    /** 라이선스 · 기타 (glsv) */
    Neutral,
}

/**
 * 소장품의 짧은 속성 라벨 (Figma: C03 · Badge).
 *
 * 색조만 바꿔 재사용한다. 크기·모양은 모든 색조가 동일하다.
 */
@Composable
fun MuseumBadge(
    label: String,
    modifier: Modifier = Modifier,
    tone: MuseumBadgeTone = MuseumBadgeTone.Gold,
) {
    val colors = MuseumTheme.colors
    val background = when (tone) {
        MuseumBadgeTone.Gold -> colors.bgGoldSubtle
        MuseumBadgeTone.Brand -> colors.bgBrandSubtle
        MuseumBadgeTone.Accent -> colors.bgAccentSubtle
        MuseumBadgeTone.Neutral -> colors.bgSurfaceSunken
    }
    val content = when (tone) {
        MuseumBadgeTone.Gold -> colors.textGold
        MuseumBadgeTone.Brand -> colors.textBrand
        MuseumBadgeTone.Accent -> colors.textAccent
        MuseumBadgeTone.Neutral -> colors.textSecondary
    }

    Row(
        modifier = modifier
            .background(background, MuseumTheme.shapes.full)
            .padding(
                horizontal = MuseumTheme.spacing.sm,
                vertical = MuseumTheme.spacing.xs,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MuseumTheme.typography.labelS,
            color = content,
        )
    }
}

@Preview(name = "Badge · Light", showBackground = true, backgroundColor = 0xFFF7F4EE)
@Preview(
    name = "Badge · Dark",
    showBackground = true,
    backgroundColor = 0xFF14110F,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun MuseumBadgePreview() {
    EveryMuseumTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MuseumBadge("국보", tone = MuseumBadgeTone.Gold)
            MuseumBadge("추천", tone = MuseumBadgeTone.Brand)
            MuseumBadge("조선", tone = MuseumBadgeTone.Accent)
            MuseumBadge("공공누리", tone = MuseumBadgeTone.Neutral)
        }
    }
}
