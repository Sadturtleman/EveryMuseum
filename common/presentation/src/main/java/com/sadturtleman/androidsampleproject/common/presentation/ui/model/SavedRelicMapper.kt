package com.sadturtleman.androidsampleproject.common.presentation.ui.model

import com.sadturtleman.androidsampleproject.common.entity.saved.SavedRelicVO
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.ArtifactType

/*
 * 보관함 저장본 <-> 카드 모델 변환.
 *
 * 홈 · 검색 결과 · 상세에서 저장하고 보관함에서 다시 그리므로, 두 방향 모두 한 자리에 둔다.
 */

/** 지금 화면에 보이는 카드를 그대로 저장본으로 옮긴다(보관함이 네트워크 없이 그려지는 이유). */
fun ArtifactUiModel.toSavedRelicVO(): SavedRelicVO = SavedRelicVO(
    id = id,
    nameKr = nameKr,
    museum = museum,
    era = era,
    designation = designation,
    imageUrl = imageUrl,
    type = type.name,
)

/** 보관함 목록의 카드는 항상 저장된 상태다. */
fun SavedRelicVO.toArtifactUiModel(): ArtifactUiModel = ArtifactUiModel(
    id = id,
    nameKr = nameKr,
    museum = museum,
    era = era,
    designation = designation,
    imageUrl = imageUrl,
    type = type.toArtifactType(),
    saved = true,
)

/** 저장본이 옛 버전이거나 모르는 값이면 기본 실루엣으로 되돌린다. */
private fun String?.toArtifactType(): ArtifactType =
    ArtifactType.entries.firstOrNull { it.name == this } ?: ArtifactType.None
