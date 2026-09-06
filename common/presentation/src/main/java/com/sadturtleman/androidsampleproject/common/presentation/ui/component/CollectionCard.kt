package com.sadturtleman.androidsampleproject.common.presentation.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
 * 그리드 소장품 카드 (Figma: C11 · CollectionCard).
 *
 * `view_relic_list` 한 건을 2열 그리드로 표시한다.
 * 데이터: id, imgThumUriM, nameKr, museumName2, nationalityName2, designationName1.
 *
 * 미디어 영역은 Figma 컴포넌트 설명대로 4:5 비율을 유지한다.
 * (원본 인스턴스는 174×208 로 고정되어 있지만, 2열 그리드에서 열 너비가 기기마다 달라지므로
 * 고정 높이 대신 비율로 옮겼다.)
 *
 * @param designationName1 국보 · 보물 지정명. null 이면 Gold 뱃지를 그리지 않는다.
 * @param nationalityName2 시대. null 이면 Accent 뱃지를 그리지 않는다.
 * @param onSaveClick null 이면 이미지 위 저장 버튼을 그리지 않는다.
 */
@Composable
fun CollectionCard(
    nameKr: String,
    meta: String,
    modifier: Modifier = Modifier,
    artifactType: ArtifactType = ArtifactType.None,
    designationName1: String? = null,
    nationalityName2: String? = null,
    onClick: (() -> Unit)? = null,
    onSaveClick: (() -> Unit)? = null,
    saved: Boolean = false,
    image: (@Composable BoxScope.() -> Unit)? = null,
) {
    val spacing = MuseumTheme.spacing

    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickableNoIndication(onClick = onClick) else Modifier),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            ArtifactThumb(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(MEDIA_ASPECT_RATIO),
                type = artifactType,
                shape = MuseumTheme.shapes.md,
                image = image,
            )
            if (onSaveClick != null) {
                val buttonSize = MuseumTheme.size.cardOverlayButton
                MuseumIconButton(
                    icon = MuseumIcons.Bookmark,
                    contentDescription = if (saved) "보관함에서 빼기" else "보관함에 저장",
                    onClick = onSaveClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        // 넓힌 터치 영역만큼 아이콘이 안쪽으로 밀리므로, 그만큼 빼서
                        // 시안대로 모서리에서 spacing.sm 떨어진 자리에 그린다.
                        .padding(spacing.sm - museumIconButtonTouchInset(buttonSize)),
                    style = MuseumIconButtonStyle.Overlay,
                    buttonSize = buttonSize,
                    // 저장되면 브랜드색으로 채운다(아이콘이 한 종류라 색으로만 구분한다).
                    container = MuseumTheme.colors.bgBrand.takeIf { saved },
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(INFO_GAP)) {
            if (designationName1 != null || nationalityName2 != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.xs)) {
                    designationName1?.let {
                        MuseumBadge(label = it, tone = MuseumBadgeTone.Gold)
                    }
                    nationalityName2?.let {
                        MuseumBadge(label = it, tone = MuseumBadgeTone.Accent)
                    }
                }
            }
            Text(
                text = nameKr,
                style = MuseumTheme.typography.titleSerifM,
                color = MuseumTheme.colors.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = meta,
                style = MuseumTheme.typography.caption,
                color = MuseumTheme.colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/** Figma 컴포넌트 설명이 지정한 미디어 비율 (가로:세로 = 4:5). */
private const val MEDIA_ASPECT_RATIO = 4f / 5f

/** 뱃지 · 유물명 · 소장처 사이 간격. 스케일에 없는 6dp 로 원본에 고정되어 있다. */
private val INFO_GAP = 6.dp

@Preview(name = "CollectionCard · Light", showBackground = true, backgroundColor = 0xFFF7F4EE)
@Preview(
    name = "CollectionCard · Dark",
    showBackground = true,
    backgroundColor = 0xFF14110F,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun CollectionCardPreview() {
    EveryMuseumTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            CollectionCard(
                modifier = Modifier.width(174.dp),
                nameKr = "사직단국왕친향도병풍",
                meta = "국립중앙박물관",
                artifactType = ArtifactType.Painting,
                designationName1 = "국보",
                nationalityName2 = "조선",
                onSaveClick = {},
            )
        }
    }
}
