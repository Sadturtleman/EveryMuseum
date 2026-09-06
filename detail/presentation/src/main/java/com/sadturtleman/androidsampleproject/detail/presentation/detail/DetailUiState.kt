package com.sadturtleman.androidsampleproject.detail.presentation.detail

import androidx.compose.runtime.Immutable
import com.sadturtleman.androidsampleproject.common.presentation.mvi.UiState
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.ArtifactType
import com.sadturtleman.androidsampleproject.common.presentation.ui.model.ArtifactUiModel

/**
 * 소장품 상세 상태 (Figma: 최종 → 05 · 소장품 상세).
 *
 * 데이터 출처는 `GET /openapi/relic/detail?id=...` 이며
 * 응답의 list · imageList · relationList 세 묶음을 모두 쓴다.
 */
sealed interface DetailUiState : UiState {

    /** 상세 조회 중 */
    data object Loading : DetailUiState

    /** 조회 실패. deep-link 로 들어온 잘못된 id 도 여기로 온다. */
    data class Error(val message: String) : DetailUiState

    /**
     * 조회 성공.
     *
     * @param nameCn 한자명. 없으면 null 이라 줄을 그리지 않는다.
     * @param credit `미상 · 국립중앙박물관 신수  |  소장품번호 012356-00000` 형태의 한 줄
     * @param images imageList 썸네일. 비면 공개 이미지 블록을 그리지 않는다.
     * @param metaRows 소장품 정보 표. 비면 해당 블록을 그리지 않는다.
     * @param description desc. 없으면 설명 블록을 그리지 않는다.
     * @param related relationList. 비면 연관 소장품 블록을 그리지 않는다.
     */
    data class Success(
        val id: String,
        val nameKr: String,
        val nameCn: String?,
        val credit: String,
        val type: ArtifactType,
        val designation: String? = null,
        val era: String? = null,
        val license: String? = null,
        val images: List<DetailImageUiModel> = emptyList(),
        val currentImageIndex: Int = 0,
        val metaRows: List<DetailMetaUiModel> = emptyList(),
        val description: String? = null,
        val related: List<ArtifactUiModel> = emptyList(),
        val licenseTitle: String? = null,
        val licenseDescription: String? = null,
        val saved: Boolean = false,
    ) : DetailUiState
}

/**
 * 공개 이미지 한 장.
 *
 * @param url imgThumUriM. 이미지 로딩 라이브러리를 붙이기 전이라 아직 그리지는 않는다.
 */
@Immutable
data class DetailImageUiModel(
    val id: String,
    val url: String? = null,
    val type: ArtifactType = ArtifactType.None,
)

/** 소장품 정보 표의 한 행. */
@Immutable
data class DetailMetaUiModel(
    val label: String,
    val value: String,
)
