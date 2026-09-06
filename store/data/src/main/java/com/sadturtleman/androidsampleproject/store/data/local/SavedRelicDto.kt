package com.sadturtleman.androidsampleproject.store.data.local

import com.sadturtleman.androidsampleproject.common.entity.saved.SavedRelicVO
import kotlinx.serialization.Serializable

/**
 * 저장소에 실제로 직렬화되는 모양.
 *
 * VO 를 그대로 저장하면 화면에 필요한 필드가 바뀔 때마다 옛 저장본을 못 읽는다.
 * 그래서 저장 포맷은 따로 두고, 나중에 추가되는 필드는 기본값 있는 nullable 로만 늘린다.
 */
@Serializable
data class SavedRelicDto(
    val id: String = "",
    val nameKr: String? = null,
    val museum: String? = null,
    val era: String? = null,
    val designation: String? = null,
    val imageUrl: String? = null,
    val type: String? = null,
    val savedAt: Long = 0L,
)

internal fun SavedRelicDto.toVO(): SavedRelicVO = SavedRelicVO(
    id = id,
    nameKr = nameKr.orEmpty(),
    museum = museum.orEmpty(),
    era = era,
    designation = designation,
    imageUrl = imageUrl,
    type = type,
    savedAt = savedAt,
)

internal fun SavedRelicVO.toDto(): SavedRelicDto = SavedRelicDto(
    id = id,
    nameKr = nameKr,
    museum = museum,
    era = era,
    designation = designation,
    imageUrl = imageUrl,
    type = type,
    savedAt = savedAt,
)
