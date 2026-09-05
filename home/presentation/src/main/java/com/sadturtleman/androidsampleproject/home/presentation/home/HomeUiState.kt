package com.sadturtleman.androidsampleproject.home.presentation.home

import androidx.compose.runtime.Immutable
import com.sadturtleman.androidsampleproject.common.presentation.mvi.UiState
import com.sadturtleman.androidsampleproject.common.presentation.ui.model.ArtifactUiModel

/**
 * 홈 화면 상태 (Figma: 최종 → 01 · 홈).
 *
 * 데이터 출처는 `GET /openapi/relic/list?numOfRows=10&pageNo=1` 과
 * `GET /openapi/code?parentCode=PS06`(시대 코드) 이다.
 */
sealed interface HomeUiState : UiState {

    /** 최초 진입 · 새로고침 중 */
    data object Loading : HomeUiState

    /** 조회 실패 */
    data class Error(val message: String) : HomeUiState

    /**
     * 조회 성공.
     *
     * @param hero 큐레이션 히어로. 노출할 소장품이 없으면 null 이고 카드를 그리지 않는다.
     * @param eras 시대 칩 목록. `view_code_list`(PS06) 결과에 "전체" 를 앞에 붙인 것이다.
     * @param selectedEraCode 선택된 시대 코드. null 이면 "전체" 가 선택된 것으로 본다.
     * @param items 오늘의 소장품 그리드. 비어 있으면 빈 상태를 그린다.
     */
    data class Success(
        val hero: HomeHeroUiModel?,
        val eras: List<EraChipUiModel>,
        val selectedEraCode: String?,
        val items: List<ArtifactUiModel>,
    ) : HomeUiState {

        val isEmpty: Boolean get() = items.isEmpty()
    }
}

/** 큐레이션 히어로 카드에 들어갈 값. */
@Immutable
data class HomeHeroUiModel(
    val id: String,
    val nameKr: String,
    val summary: String,
    val meta: String,
    /** designationName1. null 이면 금박 뱃지를 그리지 않는다. */
    val designation: String? = null,
    /** "이달의 소장품" 같은 큐레이션 라벨. null 이면 그리지 않는다. */
    val label: String? = null,
    /** 배경 사진(imgThumUriL). null 이면 먹색 그라디언트만 그린다. */
    val imageUrl: String? = null,
)

/** 시대 칩 하나. [code] 가 null 이면 "전체" 를 뜻한다. */
@Immutable
data class EraChipUiModel(
    val code: String?,
    val label: String,
)
