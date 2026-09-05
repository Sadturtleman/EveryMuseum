package com.sadturtleman.androidsampleproject.home.presentation.home

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.ArtifactType
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumErrorView
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumHomeTopAppBar
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumLoadingView
import com.sadturtleman.androidsampleproject.common.presentation.ui.model.ArtifactUiModel
import com.sadturtleman.androidsampleproject.common.presentation.ui.preview.PREVIEW_DEVICE
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.EveryMuseumTheme
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.MuseumTheme

/**
 * 홈 라우트의 진입점.
 *
 * ViewModel 을 붙이는 얇은 껍데기다. 상태 [HomeViewModel.uiState] 를 읽어 내려보내고
 * 인텐트를 [HomeViewModel.onIntent] 로 올려보내는 두 배선이 전부다.
 * 화면 그리기는 preview 가능한 [HomeView] 가 전담한다.
 */
@Composable
fun HomePage(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    HomeView(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}

/**
 * 홈 화면 (Figma: 최종 → 01 · 홈).
 *
 * 화면 크롬(상단 앱바 · 하단 탭바)과 [HomeUiState] 분기를 담당하고,
 * 성공 상태의 본문은 [HomeContent] 에 맡긴다.
 *
 * MVI 규약대로 입력은 [HomeUiState] 하나, 출력은 [HomeIntent] 하나로 받는다.
 * 인텐트가 늘어도 이 시그니처는 그대로다.
 *
 * 하단 탭바는 Figma 프레임 그대로 이 화면이 그린다. Navigation 3 호스트가 탭 전환을
 * 가져갈 때는 `RootComposable` 로 올리고 여기서는 제거하면 된다.
 */
@Composable
internal fun HomeView(
    state: HomeUiState,
    onIntent: (HomeIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MuseumTheme.colors.bgCanvas),
    ) {
        MuseumHomeTopAppBar(
            onSearchClick = { onIntent(HomeIntent.OpenSearch) },
            onBookmarkClick = { onIntent(HomeIntent.OpenLibrary) },
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            when (state) {
                HomeUiState.Loading -> MuseumLoadingView()

                is HomeUiState.Error -> MuseumErrorView(
                    message = state.message,
                    onRetry = { onIntent(HomeIntent.Retry) },
                )

                is HomeUiState.Success -> HomeContent(
                    state = state,
                    onIntent = onIntent,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Preview
// ---------------------------------------------------------------------------

@Composable
private fun HomeViewPreviewHost(state: HomeUiState) {
    EveryMuseumTheme {
        HomeView(state = state, onIntent = {})
    }
}

@Preview(name = "홈 · 성공 · Light", device = PREVIEW_DEVICE, showBackground = true)
@Composable
private fun HomeViewSuccessPreview() {
    HomeViewPreviewHost(HomePreviewData.success)
}

@Preview(
    name = "홈 · 성공 · Dark",
    device = PREVIEW_DEVICE,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun HomeViewSuccessDarkPreview() {
    HomeViewPreviewHost(HomePreviewData.success)
}

@Preview(name = "홈 · 로딩", device = PREVIEW_DEVICE, showBackground = true)
@Composable
private fun HomeViewLoadingPreview() {
    HomeViewPreviewHost(HomeUiState.Loading)
}

@Preview(name = "홈 · 에러", device = PREVIEW_DEVICE, showBackground = true)
@Composable
private fun HomeViewErrorPreview() {
    HomeViewPreviewHost(HomeUiState.Error("네트워크에 연결할 수 없습니다."))
}

@Preview(name = "홈 · 소장품 없음", device = PREVIEW_DEVICE, showBackground = true)
@Composable
private fun HomeViewEmptyPreview() {
    HomeViewPreviewHost(HomePreviewData.success.copy(items = emptyList()))
}

@Preview(name = "홈 · 히어로 없음", device = PREVIEW_DEVICE, showBackground = true)
@Composable
private fun HomeViewNoHeroPreview() {
    HomeViewPreviewHost(HomePreviewData.success.copy(hero = null))
}

private object HomePreviewData {

    val success = HomeUiState.Success(
        hero = HomeHeroUiModel(
            id = "hero-1",
            nameKr = "사직단국왕친향도병풍",
            summary = "왕이 사직단에 나와 제사를 지내는 모습을 여덟 폭에 담은 의궤도 병풍입니다.",
            meta = "국립중앙박물관 · 조선",
            designation = "국보",
            label = "이달의 소장품",
        ),
        eras = listOf(
            EraChipUiModel(code = null, label = "전체"),
            EraChipUiModel(code = "PS06001011", label = "신라"),
            EraChipUiModel(code = "PS06001012", label = "백제"),
            EraChipUiModel(code = "PS06001013", label = "고구려"),
            EraChipUiModel(code = "PS06001017", label = "고려"),
            EraChipUiModel(code = "PS06001018", label = "조선"),
        ),
        selectedEraCode = null,
        items = listOf(
            ArtifactUiModel(
                id = "1",
                nameKr = "금동미륵보살반가사유상",
                museum = "국립중앙박물관",
                era = "삼국",
                designation = "국보",
                type = ArtifactType.Metal,
            ),
            ArtifactUiModel(
                id = "2",
                nameKr = "백자 달항아리",
                museum = "국립중앙박물관",
                era = "조선",
                designation = "보물",
                type = ArtifactType.Pottery,
            ),
            ArtifactUiModel(
                id = "3",
                nameKr = "두드린무늬항아리",
                museum = "국립중앙박물관 · 신수",
                era = "백제",
                type = ArtifactType.Pottery,
            ),
            ArtifactUiModel(
                id = "4",
                nameKr = "대당평일백제비 탑본",
                museum = "국립중앙박물관 · 본관",
                era = "통일신라",
                type = ArtifactType.Book,
            ),
        ),
    )
}
