package com.sadturtleman.androidsampleproject.common.entity.code

import kotlinx.serialization.Serializable

/**
 * 분류 코드 한 건 (`GET /openapi/code?parentCode=`).
 *
 * 시대(PS06) · 재질(PS08) · 소장기관(PS01) · 용도(PS09) · 크기(PS15) 목록이 모두 같은 모양이다.
 *
 * @param code 목록 조회 파라미터로 그대로 넘기는 값 (예: PS06001018)
 * @param nameKr 화면에 보이는 이름 (예: 조선)
 * @param level 코드 깊이. 상위 코드일수록 작다.
 */
@Serializable
data class CodeVO(
    val code: String = "",
    val parentCode: String? = null,
    val nameKr: String? = null,
    val nameEn: String? = null,
    val nameCn: String? = null,
    val level: Int = 0,
) {
    /** 화면에 그릴 라벨. 한글명이 비면 코드라도 보여준다. */
    val label: String get() = nameKr?.takeIf { it.isNotBlank() } ?: code
}
