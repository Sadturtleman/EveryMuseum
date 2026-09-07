package com.sadturtleman.androidsampleproject.common.presentation.ui.component

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.sadturtleman.androidsampleproject.tti.domain.TtiTimeline
import com.sadturtleman.androidsampleproject.tti.presentation.TtiEmptySpanEffect
import com.sadturtleman.androidsampleproject.tti.presentation.TtiSpanEffect

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
 *
 * @param measureTti 이 사진의 로딩을 화면의 [TtiTimeline.BIG_PART_LOADING] 구간으로 잰다.
 *   화면을 대표하는 큰 사진 한 장에만 켠다 — 목록의 썸네일마다 켜면 먼저 끝난 한 장이 구간을 닫아
 *   나머지가 아직 내려오는 중인데도 다 그려진 것으로 기록된다.
 */
@Composable
fun BoxScope.ArtifactImage(
    url: String?,
    contentDescription: String? = null,
    measureTti: Boolean = false,
) {
    if (url.isNullOrBlank()) {
        // 잴 사진이 아예 없다는 것도 남겨야 한다 — 비워 두면 그 화면의 기록이 완성되지 않는다.
        if (measureTti) TtiEmptySpanEffect(TtiTimeline.BIG_PART_LOADING)
        return
    }

    // 주소가 바뀌면 다시 로딩부터 시작한다.
    var loading by remember(url) { mutableStateOf(true) }
    if (measureTti) {
        TtiSpanEffect(TtiTimeline.BIG_PART_LOADING, running = loading)
    }

    AsyncImage(
        model = url,
        contentDescription = contentDescription,
        modifier = Modifier.matchParentSize(),
        contentScale = ContentScale.Crop,
        onLoading = { loading = true },
        onSuccess = { loading = false },
        // 실패도 "더 기다릴 것이 없다" 는 뜻이라 구간을 닫는다.
        onError = { loading = false },
    )
}
