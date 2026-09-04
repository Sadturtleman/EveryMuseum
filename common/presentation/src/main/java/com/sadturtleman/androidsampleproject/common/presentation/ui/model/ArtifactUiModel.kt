package com.sadturtleman.androidsampleproject.common.presentation.ui.model

import androidx.compose.runtime.Immutable
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.ArtifactType

/**
 * 소장품 카드 · 리스트 행이 화면에 그릴 값.
 *
 * 홈 · 검색 결과 · 보관함 · 상세(연관 소장품)가 모두 같은 카드를 쓰므로
 * feature 마다 같은 모델을 반복 정의하지 않도록 디자인 시스템 옆에 둔다.
 *
 * eMuseum `view_relic_list` 응답 필드와의 대응:
 * | 필드 | API |
 * |---|---|
 * | [id] | id |
 * | [nameKr] | nameKr |
 * | [museum] | museumName2 (· museumName3) |
 * | [era] | nationalityName2 |
 * | [designation] | designationName1 |
 * | [spec] | sizeInfo · materialName1 |
 * | [imageUrl] | imgThumUriS / imgThumUriM |
 */
@Immutable
data class ArtifactUiModel(
    val id: String,
    val nameKr: String,
    val museum: String,
    val era: String? = null,
    val designation: String? = null,
    val spec: String? = null,
    val imageUrl: String? = null,
    val type: ArtifactType = ArtifactType.None,
    val saved: Boolean = false,
)
