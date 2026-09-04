package com.sadturtleman.androidsampleproject.detail.presentation.detail

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.ArtifactType
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumErrorView
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumLoadingView
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumOverlayTopAppBar
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumTopAppBar
import com.sadturtleman.androidsampleproject.common.presentation.ui.model.ArtifactUiModel
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.EveryMuseumTheme
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.MuseumTheme

/**
 * 소장품 상세 화면 (Figma: 최종 → 05 · 소장품 상세).
 *
 * [DetailUiState] 분기와 상단 앱바를 담당하고, 본문은 [DetailContent] 에 맡긴다.
 * 아직 ViewModel 을 연결하지 않은 view-only 단계라 상태와 콜백을 모두 인자로 받는다.
 *
 * 투명 앱바는 시안에서 히어로 프레임 안에 있지만, 여기서는 스크롤 위에 고정해 두었다.
 * 본문이 1500dp 넘게 길어 함께 스크롤되면 뒤로가기가 화면 밖으로 사라지기 때문이다.
 * 로딩 · 에러 상태에는 히어로가 없으므로 배경이 있는 기본 앱바를 쓴다.
 */
@Composable
fun DetailView(
    state: DetailUiState,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onShareClick: () -> Unit,
    onImageClick: (DetailImageUiModel) -> Unit,
    onRelatedClick: (ArtifactUiModel) -> Unit,
    onRelatedSeeAllClick: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MuseumTheme.colors.bgCanvas),
    ) {
        when (state) {
            DetailUiState.Loading -> {
                MuseumTopAppBar(title = "", onBackClick = onBackClick)
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    MuseumLoadingView()
                }
            }

            is DetailUiState.Error -> {
                MuseumTopAppBar(title = "", onBackClick = onBackClick)
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    MuseumErrorView(message = state.message, onRetry = onRetry)
                }
            }

            is DetailUiState.Success -> {
                DetailContent(
                    state = state,
                    onSaveClick = onSaveClick,
                    onShareClick = onShareClick,
                    onImageClick = onImageClick,
                    onRelatedClick = onRelatedClick,
                    onRelatedSeeAllClick = onRelatedSeeAllClick,
                    modifier = Modifier.fillMaxSize(),
                )
                MuseumOverlayTopAppBar(
                    onBackClick = onBackClick,
                    onShareClick = onShareClick,
                    onBookmarkClick = onSaveClick,
                    modifier = Modifier.align(Alignment.TopStart),
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Preview
// ---------------------------------------------------------------------------

@Composable
private fun DetailViewPreviewHost(state: DetailUiState) {
    EveryMuseumTheme {
        DetailView(
            state = state,
            onBackClick = {},
            onSaveClick = {},
            onShareClick = {},
            onImageClick = {},
            onRelatedClick = {},
            onRelatedSeeAllClick = {},
            onRetry = {},
        )
    }
}

@Preview(name = "상세 · 성공 · Light", device = PREVIEW_DEVICE, showBackground = true, heightDp = 1560)
@Composable
private fun DetailViewSuccessPreview() {
    DetailViewPreviewHost(DetailPreviewData.success)
}

@Preview(
    name = "상세 · 성공 · Dark",
    device = PREVIEW_DEVICE,
    showBackground = true,
    heightDp = 1560,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun DetailViewSuccessDarkPreview() {
    DetailViewPreviewHost(DetailPreviewData.success)
}

@Preview(name = "상세 · 최소 정보", device = PREVIEW_DEVICE, showBackground = true)
@Composable
private fun DetailViewMinimalPreview() {
    DetailViewPreviewHost(
        DetailPreviewData.success.copy(
            nameCn = null,
            images = emptyList(),
            metaRows = emptyList(),
            description = null,
            related = emptyList(),
            designation = null,
            license = null,
        ),
    )
}

@Preview(name = "상세 · 저장됨", device = PREVIEW_DEVICE, showBackground = true)
@Composable
private fun DetailViewSavedPreview() {
    DetailViewPreviewHost(DetailPreviewData.success.copy(saved = true))
}

@Preview(name = "상세 · 로딩", device = PREVIEW_DEVICE, showBackground = true)
@Composable
private fun DetailViewLoadingPreview() {
    DetailViewPreviewHost(DetailUiState.Loading)
}

@Preview(name = "상세 · 에러", device = PREVIEW_DEVICE, showBackground = true)
@Composable
private fun DetailViewErrorPreview() {
    DetailViewPreviewHost(DetailUiState.Error("존재하지 않는 소장품입니다."))
}

/** iPhone 16 시안(393×852)에 대응하는 안드로이드 기준 기기. */
private const val PREVIEW_DEVICE = "spec:width=393dp,height=852dp,dpi=440"

private object DetailPreviewData {

    val success = DetailUiState.Success(
        id = "PS0100100101101235600000",
        nameKr = "사직단국왕친향도병풍",
        nameCn = "社稷壇國王親享圖屛風",
        credit = "미상 · 국립중앙박물관 신수  |  소장품번호 012356-00000",
        type = ArtifactType.Painting,
        designation = "국보",
        era = "조선",
        license = "공공누리 제1유형",
        images = listOf(
            DetailImageUiModel(id = "img-1", type = ArtifactType.Painting),
            DetailImageUiModel(id = "img-2", type = ArtifactType.Painting),
            DetailImageUiModel(id = "img-3", type = ArtifactType.Painting),
        ),
        currentImageIndex = 0,
        metaRows = listOf(
            DetailMetaUiModel("국적 / 시대", "한국 · 조선"),
            DetailMetaUiModel("재질", "사직 · 견"),
            DetailMetaUiModel("용도 · 기능", "문화예술 · 사회 · 회화 · 일반회화"),
            DetailMetaUiModel("크기", "세로 127cm, 가로 50cm (1~5 m)"),
            DetailMetaUiModel("출토지", "서울특별시 중구"),
            DetailMetaUiModel("지정문화재", "국보"),
            DetailMetaUiModel("소장처", "국립중앙박물관 · 신수"),
            DetailMetaUiModel("명칭", "사직단국왕친향도병"),
        ),
        description = "궁중의 예법과 규모 등의 자세한 일의 경과나 경비 등을 그림과 글로 기록하여 꾸민 병풍을 " +
            "'의궤도 병풍'이라 한다. 이 병풍은 모두 여덟 폭으로 이루어져 있으며, 왕이 친히 사직단에 " +
            "나와 제사를 지내는 모습을 자세하게 묘사하고 있다.",
        related = listOf(
            ArtifactUiModel(
                id = "r1",
                nameKr = "영조대왕경현당수작도기",
                museum = "국립중앙박물관 · 신수",
                type = ArtifactType.Pottery,
            ),
            ArtifactUiModel(
                id = "r2",
                nameKr = "두드린무늬항아리",
                museum = "국립중앙박물관",
                type = ArtifactType.Metal,
            ),
            ArtifactUiModel(
                id = "r3",
                nameKr = "대당평일백제비 탑본",
                museum = "국립중앙박물관 · 본관",
                type = ArtifactType.Book,
            ),
        ),
        licenseTitle = "공공누리 제1유형 : 출처표시",
        licenseDescription = "국립중앙박물관이 제공하는 소장품 정보·이미지는 출처 표시 조건 하에 " +
            "상업적 이용 및 변형이 가능합니다. (e-museum Open API · 일 1회 갱신)",
        saved = false,
    )
}
