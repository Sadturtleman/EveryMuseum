package com.sadturtleman.androidsampleproject.common.presentation.ui.model

import com.sadturtleman.androidsampleproject.common.entity.relic.RelicVO
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.ArtifactType

/**
 * 오픈API 목록 한 건([RelicVO])을 카드 모델로 옮긴다.
 *
 * 홈 · 검색 결과 · 상세(연관 소장품)가 모두 이 변환을 쓴다.
 *
 * @param saved 보관함에 저장된 id 인지. 카드의 북마크 아이콘 상태가 된다.
 * @param era 시대 라벨. 목록 응답에는 시대 "이름" 이 없고 코드만 오므로
 *  (게다가 그 코드도 `PS06001`(한국) 수준이라 시대까지 알 수 없다) 아는 화면이 넣어 준다.
 */
fun RelicVO.toArtifactUiModel(
    saved: Boolean = false,
    era: String? = null,
): ArtifactUiModel = ArtifactUiModel(
    id = id,
    // nameKr 이 비는 행이 있어 한자명 · 원명 순으로 물러난다. 카드 제목이 비면 안 된다.
    nameKr = nameKr?.takeIf { it.isNotBlank() }
        ?: nameCn?.takeIf { it.isNotBlank() }
        ?: name.orEmpty(),
    museum = museumLabel,
    era = era,
    spec = indexWord?.takeIf { it.isNotBlank() }?.replace(",", " · "),
    // 목록 카드는 200px 썸네일이면 충분하다. 없으면 75px 로 물러난다.
    imageUrl = imgThumUriM ?: imgThumUriS,
    type = artifactTypeOf(materialCode),
    saved = saved,
)

/**
 * 재질 코드(PS08 계열)를 카드 실루엣으로 옮긴다. 상세 화면도 같은 규칙을 쓴다.
 *
 * 코드는 `PS08001005`(철) 처럼 깊게 오므로 상위 7자리로 자른다.
 * [ArtifactType.Painting] 은 재질만으로 가려낼 수 없어(그림도 서책도 종이다) 여기서 나오지 않는다.
 */
fun artifactTypeOf(materialCode: String?): ArtifactType = when (materialCode?.take(MATERIAL_CODE_LENGTH)) {
    MATERIAL_METAL -> ArtifactType.Metal
    MATERIAL_CLAY, MATERIAL_CERAMIC -> ArtifactType.Pottery
    MATERIAL_STONE, MATERIAL_GLASS, MATERIAL_MINERAL, MATERIAL_FOSSIL -> ArtifactType.Stone
    MATERIAL_PAPER -> ArtifactType.Book
    else -> ArtifactType.None
}

/** `PS08` + 세 자리. */
private const val MATERIAL_CODE_LENGTH = 7

private const val MATERIAL_METAL = "PS08001"
private const val MATERIAL_CLAY = "PS08002"
private const val MATERIAL_CERAMIC = "PS08003"
private const val MATERIAL_STONE = "PS08004"
private const val MATERIAL_GLASS = "PS08005"
private const val MATERIAL_PAPER = "PS08009"
private const val MATERIAL_MINERAL = "PS08013"
private const val MATERIAL_FOSSIL = "PS08014"
