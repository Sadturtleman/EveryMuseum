package com.sadturtleman.androidsampleproject.detail.data

import com.sadturtleman.androidsampleproject.common.data.relic.toRelicImageVO
import com.sadturtleman.androidsampleproject.common.data.relic.toRelicVO
import com.sadturtleman.androidsampleproject.common.entity.relic.RelicDetailVO
import com.sadturtleman.androidsampleproject.common.network.EmuseumResponse

/**
 * 상세 응답 -> VO 변환.
 *
 * 본문(`list`)과 연관 소장품(`relationList`)의 행은 목록 응답과 같은 모양이라
 * :common:data 의 공용 행 매퍼를 그대로 쓴다. 상세에만 오는 이름 필드만 여기서 읽는다.
 */
internal fun EmuseumResponse.toRelicDetailVO(): RelicDetailVO {
    val body = rows().firstOrNull().orEmpty()
    return RelicDetailVO(
        relic = body.toRelicVO(),
        nationalityName1 = body["nationalityName1"],
        nationalityName2 = body["nationalityName2"],
        materialName1 = body["materialName1"],
        materialName2 = body["materialName2"],
        purposeName1 = body["purposeName1"],
        purposeName2 = body["purposeName2"],
        purposeName3 = body["purposeName3"],
        purposeName4 = body["purposeName4"],
        sizeRangeName = body["sizeRangeName"],
        sizeInfo = body["sizeInfo"],
        designationName1 = body["designationName1"],
        description = body["desc"],
        images = rows(EmuseumResponse.SECTION_IMAGE_LIST)
            .map { it.toRelicImageVO() }
            .sortedBy { it.imgOrder },
        relations = rows(EmuseumResponse.SECTION_RELATION_LIST).map { it.toRelicVO() },
    )
}
