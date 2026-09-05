package com.sadturtleman.androidsampleproject.common.entity.saved

import kotlinx.serialization.Serializable

/**
 * 보관함에 저장된 소장품 한 건.
 *
 * 목록 API 에는 "id 여러 개로 조회" 가 없어서, 저장 시점의 카드 표시값을 그대로 들고 있는다.
 * 덕분에 보관함은 네트워크 없이 그려지고, 저장한 소장품이 나중에 목록에서 빠져도 남는다.
 *
 * @param type 카드 실루엣 종류의 이름. entity 는 디자인 시스템(ArtifactType)을 참조할 수 없으므로
 *  enum 이름 문자열로 담고, 화면에서 되돌린다. 모르는 값이면 화면이 기본 실루엣을 쓴다.
 * @param savedAt 저장 시각(epoch millis). 보관함의 "최근 저장순" 정렬 기준이다.
 */
@Serializable
data class SavedRelicVO(
    val id: String,
    val nameKr: String,
    val museum: String,
    val era: String? = null,
    val designation: String? = null,
    val imageUrl: String? = null,
    val type: String? = null,
    val savedAt: Long = 0L,
)
