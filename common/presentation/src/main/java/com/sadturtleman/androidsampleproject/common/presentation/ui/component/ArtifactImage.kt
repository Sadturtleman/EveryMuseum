package com.sadturtleman.androidsampleproject.common.presentation.ui.component

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage

/**
 * [ArtifactThumb] 의 이미지 슬롯에 넣는 소장품 사진.
 *
 * 로딩 전 · 실패 · 주소 없음 세 경우 모두 아무것도 그리지 않아,
 * 아래에 깔린 유형별 실루엣이 그대로 보인다 (목록의 리듬이 유지된다).
 *
 * ```
 * ArtifactThumb(type = item.type, shape = MuseumTheme.shapes.md) {
 *     ArtifactImage(url = item.imageUrl, contentDescription = item.nameKr)
 * }
 * ```
 */
@Composable
fun BoxScope.ArtifactImage(
    url: String?,
    contentDescription: String? = null,
) {
    if (url.isNullOrBlank()) return
    AsyncImage(
        model = url,
        contentDescription = contentDescription,
        modifier = Modifier.matchParentSize(),
        contentScale = ContentScale.Crop,
    )
}
