package com.sadturtleman.androidsampleproject.common.network

/**
 * e뮤지엄 오픈API 응답 한 건을 파싱한 결과.
 *
 * 세 오퍼레이션(list · detail · code)이 모두 같은 껍데기를 쓴다.
 * 본문은 `<data>` 안에 `<item key="..." value="..."/>` 쌍으로 들어오므로,
 * 여기서는 한 행을 `Map<String, String>` 그대로 담고 VO 변환은 매퍼가 맡는다.
 *
 * 상세 응답만 묶음이 셋(`list` · `imageList` · `relationList`)이라 [sections] 로 나눠 담는다.
 */
data class EmuseumResponse(
    val resultCode: String,
    val resultMsg: String,
    val totalCount: Int,
    val pageNo: Int,
    val numOfRows: Int,
    val sections: Map<String, List<Map<String, String>>>,
) {
    val isSuccess: Boolean get() = resultCode == RESULT_CODE_OK

    /** 지정한 묶음의 행들. 없는 묶음이면 빈 목록이다. */
    fun rows(section: String = SECTION_LIST): List<Map<String, String>> =
        sections[section].orEmpty()

    companion object {
        const val RESULT_CODE_OK = "0000"

        const val SECTION_LIST = "list"
        const val SECTION_IMAGE_LIST = "imageList"
        const val SECTION_RELATION_LIST = "relationList"
    }
}

/**
 * 오픈API 가 실패를 알린 경우.
 *
 * HTTP 는 200 인데 본문의 resultCode 만 실패인 경우와,
 * 포털 게이트웨이가 인증키 오류를 XML(`OpenAPI_ServiceResponse`)로 돌려주는 경우를 모두 덮는다.
 */
class EmuseumApiException(
    val code: String,
    override val message: String,
) : RuntimeException("[$code] $message")

/**
 * 본문이 실패를 알린 응답을 예외로 바꾼다.
 *
 * HTTP 200 인데 resultCode 만 실패인 경우가 있어 오퍼레이션마다 같은 검사가 필요하다.
 * 조회하는 data 모듈이 여럿이므로(목록 · 상세) 규칙을 응답 타입 옆에 둔다.
 */
fun EmuseumResponse.requireSuccess(): EmuseumResponse {
    if (isSuccess) return this
    throw EmuseumApiException(
        code = resultCode,
        message = resultMsg.ifBlank { "오픈API 요청에 실패했습니다." },
    )
}
