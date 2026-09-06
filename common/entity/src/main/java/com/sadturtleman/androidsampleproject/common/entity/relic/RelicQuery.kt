package com.sadturtleman.androidsampleproject.common.entity.relic

/**
 * 소장품 목록 조회 조건 (`GET /openapi/list`).
 *
 * 실제 응답으로 동작을 확인한 파라미터만 담는다.
 * (`museumCode1` · `museumCode2` · `designationCode` 는 값을 넣어도 건수가 줄지 않아 제외했다)
 *
 * @param name 소장품명 부분 일치. 검색 화면의 질의어가 여기로 간다.
 * @param indexWord 색인어 완전 일치
 * @param nationalityCode 국적/시대 코드 (PS06 계열)
 * @param materialCode 재질 코드 (PS08 계열)
 * @param museumCode 소장기관 코드 (PS01 계열, 최하위 코드여야 걸린다)
 * @param purposeCode 용도/기능 코드 (PS09 계열)
 * @param sizeRangeCode 크기 코드 (PS15 계열)
 */
data class RelicQuery(
    val pageNo: Int = FIRST_PAGE,
    val numOfRows: Int = DEFAULT_PAGE_SIZE,
    val name: String? = null,
    val indexWord: String? = null,
    val nationalityCode: String? = null,
    val materialCode: String? = null,
    val museumCode: String? = null,
    val purposeCode: String? = null,
    val sizeRangeCode: String? = null,
) {
    companion object {
        const val FIRST_PAGE = 1
        const val DEFAULT_PAGE_SIZE = 20
    }
}
