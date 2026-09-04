package com.sadturtleman.androidsampleproject.common.presentation.ui.component

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sadturtleman.androidsampleproject.common.presentation.R
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.EveryMuseumTheme
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.museumLinearGradient

/**
 * 유물 유형 (Figma: C02 · Artifact Thumb → Type).
 *
 * 이미지가 없거나 로딩에 실패했을 때 유형별 실루엣으로 목록의 리듬을 유지한다.
 */
enum class ArtifactType(@param:DrawableRes internal val silhouette: Int) {
    /** 도자기 */
    Pottery(R.drawable.ic_artifact_pottery),

    /** 회화 */
    Painting(R.drawable.ic_artifact_painting),

    /** 금속 */
    Metal(R.drawable.ic_artifact_metal),

    /** 석조 */
    Stone(R.drawable.ic_artifact_stone),

    /** 서책 */
    Book(R.drawable.ic_artifact_book),

    /** 유형 미상 */
    None(R.drawable.ic_artifact_none),
}

/**
 * 유물 썸네일 슬롯 (Figma: C02 · Artifact Thumb).
 *
 * eMuseum 의 `imgThumUriS`(75px) / `imgThumUriM`(200px) / `imgThumUriL`(700px) / `imgUri`(원본)가
 * 들어갈 자리다. 이미지 로딩 전이나 실패 시에도 같은 플레이스홀더가 유지되도록,
 * 배경과 실루엣은 항상 그려지고 그 위에 [image] 가 덮인다.
 *
 * `:common:presentation` 은 이미지 로딩 라이브러리에 의존하지 않는다.
 * 실제 이미지는 feature 모듈에서 Coil `AsyncImage` 등을 [image] 슬롯에 넘겨 그린다.
 *
 * ```
 * ArtifactThumb(type = ArtifactType.Pottery, shape = MuseumTheme.shapes.md) {
 *     AsyncImage(model = item.imgThumUriM, contentDescription = null, ...)
 * }
 * ```
 *
 * 배경 그라디언트와 실루엣 색은 사진이 놓일 자리를 나타내는 고정 톤이라
 * 라이트/다크 모드에 관계없이 동일하다. Figma 원본도 이 두 값은 모드별로 분리하지 않았다.
 */
@Composable
fun ArtifactThumb(
    modifier: Modifier = Modifier,
    type: ArtifactType = ArtifactType.None,
    shape: Shape = RectangleShape,
    image: (@Composable BoxScope.() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .clip(shape)
            .museumLinearGradient(
                angleDegrees = THUMB_GRADIENT_ANGLE_DEGREES,
                0.0147f to ThumbGradientStart,
                0.7206f to ThumbGradientEnd,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(type.silhouette),
            contentDescription = null,
            modifier = Modifier.size(SILHOUETTE_SIZE),
        )
        image?.invoke(this)
    }
}

/** 실루엣은 컨테이너보다 클 수 있고, 넘치는 부분은 잘린다. Figma 와 동일한 동작이다. */
private val SILHOUETTE_SIZE = 96.dp

private val ThumbGradientStart = Color(0xFFF2EDE5)
private val ThumbGradientEnd = Color(0xFFE0D8CE)

/** Figma 원본의 `linear-gradient(120.17deg, ...)`. */
private const val THUMB_GRADIENT_ANGLE_DEGREES = 120.1735f

@Preview(name = "ArtifactThumb", showBackground = true, backgroundColor = 0xFFF7F4EE)
@Preview(
    name = "ArtifactThumb · Dark",
    showBackground = true,
    backgroundColor = 0xFF14110F,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun ArtifactThumbPreview() {
    EveryMuseumTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ArtifactType.entries.forEach { type ->
                ArtifactThumb(
                    modifier = Modifier.size(84.dp),
                    type = type,
                )
            }
        }
    }
}
