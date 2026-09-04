package com.sadturtleman.androidsampleproject.common.entity.sample

import kotlinx.serialization.Serializable

/**
 * 앱 전역에서 오가는 아이템 VO.
 * entity 모듈은 순수 데이터만 담으며 어떤 안드로이드 API 에도 의존하지 않는다.
 */
@Serializable
data class SampleItemVO(
    val id: String = "",
    val title: String = "",
    val description: String = "",
)
