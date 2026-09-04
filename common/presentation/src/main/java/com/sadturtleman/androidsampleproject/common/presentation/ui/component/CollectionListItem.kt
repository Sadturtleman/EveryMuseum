package com.sadturtleman.androidsampleproject.common.presentation.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.EveryMuseumTheme
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.MuseumTheme

/**
 * 리스트형 소장품 행 (Figma: C12 · CollectionListItem).
 *
 * 그리드 카드와 같은 데이터를 밀도 높은 리스트로 본다.
 * imgThumUriS(75px)를 84×84 로 사용하고, 우측 chevron 으로 view_relic_detail 로 진입한다.
 *
 * @param spec sizeInfo 등 한 줄 스펙. null 이면 그리지 않는다.
 */
@Composable
fun CollectionListItem(
    nameKr: String,
    meta: String,
    modifier: Modifier = Modifier,
    spec: String? = null,
    artifactType: ArtifactType = ArtifactType.None,
    onClick: (() -> Unit)? = null,
    image: (@Composable BoxScope.() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickableNoIndication(onClick = onClick) else Modifier,
            )
            .padding(vertical = MuseumTheme.spacing.md),
        horizontalArrangement = Arrangement.spacedBy(MuseumTheme.spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ArtifactThumb(
            modifier = Modifier.size(MuseumTheme.size.listThumb),
            type = artifactType,
            shape = MuseumTheme.shapes.sm,
            image = image,
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(MuseumTheme.spacing.xs),
        ) {
            Text(
                text = nameKr,
                style = MuseumTheme.typography.titleSerifM,
                color = MuseumTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = meta,
                style = MuseumTheme.typography.caption,
                color = MuseumTheme.colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (spec != null) {
                Text(
                    text = spec,
                    style = MuseumTheme.typography.labelS,
                    color = MuseumTheme.colors.textTertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        MuseumIcon(
            id = MuseumIcons.ChevronRight,
            contentDescription = null,
            tint = MuseumTheme.colors.iconSecondary,
            size = MuseumTheme.size.iconSm,
        )
    }
}

@Preview(name = "CollectionListItem · Light", showBackground = true, backgroundColor = 0xFFF7F4EE)
@Preview(
    name = "CollectionListItem · Dark",
    showBackground = true,
    backgroundColor = 0xFF14110F,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun CollectionListItemPreview() {
    EveryMuseumTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            CollectionListItem(
                nameKr = "대당평일백제비 탑본",
                meta = "국립중앙박물관 · 본관",
                spec = "종이 · 세로 20.9cm, 가로 14.9cm",
                artifactType = ArtifactType.Book,
                onClick = {},
            )
            CollectionListItem(
                nameKr = "백자 달항아리",
                meta = "국립중앙박물관 · 신수",
                spec = "백자 · 높이 44cm",
                artifactType = ArtifactType.Pottery,
                onClick = {},
            )
        }
    }
}
