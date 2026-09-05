package com.sadturtleman.androidsampleproject.common.domain.code

import com.sadturtleman.androidsampleproject.common.entity.code.CodeVO
import javax.inject.Inject

/**
 * 분류 코드 목록 조회.
 *
 * 홈의 시대 칩, 검색의 코드 둘러보기, 필터 시트의 옵션이 모두 이 경로를 쓴다.
 * 자주 쓰는 상위 코드는 [ParentCode] 에 모아 둔다.
 */
class GetCodesUseCase @Inject constructor(
    private val codeRepository: CodeRepository,
) {
    suspend operator fun invoke(parentCode: String): List<CodeVO> =
        codeRepository.getCodes(parentCode)

    /** e뮤지엄 상위 분류 코드. */
    object ParentCode {
        /** 소장기관 */
        const val MUSEUM = "PS01"

        /** 국적 (하위가 한국 · 아시아 · 유럽 … 대륙 단위다) */
        const val NATIONALITY = "PS06"

        /** 한국 시대. 구석기 ~ 2000년 이후가 코드 오름차순으로 온다 — 홈의 시대 칩이 이걸 쓴다. */
        const val NATIONALITY_KOREA = "PS06001"

        /** 재질 */
        const val MATERIAL = "PS08"

        /** 용도 · 기능 */
        const val PURPOSE = "PS09"

        /** 출토지 */
        const val PROVENANCE = "GL05"

        /** 크기 */
        const val SIZE_RANGE = "PS15"
    }
}
