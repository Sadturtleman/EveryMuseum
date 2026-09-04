package com.sadturtleman.androidsampleproject.common.presentation.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.EveryMuseumTheme
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.MuseumTheme

/**
 * 연관 소장품 타일 (Figma: C13 · RelatedCard).
 *
 * view_relic_detail 의 relationList 항목이며 상세 하단에서 가로 스크롤된다.
 * 데이터: reltId, reltImgThumUriM, reltRelicName, reltMuseumFullName.
 */
@Composable
fun RelatedCard(
    reltRelicName: String,
    reltMuseumFullName: String,
    modifier: Modifier = Modifier,
    cardWidth: Dp = RELATED_CARD_WIDTH,
    artifactType: ArtifactType = ArtifactType.None,
    onClick: (() -> Unit)? = null,
    image: (@Composable BoxScope.() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .width(cardWidth)
            .then(
                if (onClick != null) Modifier.clickableNoIndication(onClick = onClick) else Modifier,
            ),
        verticalArrangement = Arrangement.spacedBy(MuseumTheme.spacing.sm),
    ) {
        ArtifactThumb(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            type = artifactType,
            shape = MuseumTheme.shapes.sm,
            image = image,
        )
        Text(
            text = reltRelicName,
            style = MuseumTheme.typography.labelL,
            color = MuseumTheme.colors.textPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = reltMuseumFullName,
            style = MuseumTheme.typography.labelS,
            color = MuseumTheme.colors.textTertiary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** Figma 원본 타일 너비. 가로 스크롤이라 고정폭을 유지한다. */
private val RELATED_CARD_WIDTH = 140.dp

@Preview(name = "RelatedCard · Light", showBackground = true, backgroundColor = 0xFFF7F4EE)
@Preview(
    name = "RelatedCard · Dark",
    showBackground = true,
    backgroundColor = 0xFF14110F,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun RelatedCardPreview() {
    EveryMuseumTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            RelatedCard(
                reltRelicName = "영조대왕경현당수작도기",
                reltMuseumFullName = "국립중앙박물관 · 신수",
                artifactType = ArtifactType.Pottery,
            )
            RelatedCard(
                reltRelicName = "두드린무늬항아리",
                reltMuseumFullName = "국립중앙박물관",
                artifactType = ArtifactType.Metal,
            )
        }
    }
}
