package com.sadturtleman.androidsampleproject.home.presentation.home

import androidx.lifecycle.viewModelScope
import com.sadturtleman.androidsampleproject.common.domain.code.GetCodesUseCase
import com.sadturtleman.androidsampleproject.common.domain.helper.MessageHelper
import com.sadturtleman.androidsampleproject.common.domain.helper.NavigationHelper
import com.sadturtleman.androidsampleproject.common.domain.saved.GetSavedRelicIdsUseCase
import com.sadturtleman.androidsampleproject.common.domain.saved.ToggleSavedRelicUseCase
import com.sadturtleman.androidsampleproject.common.entity.relic.RelicPageVO
import com.sadturtleman.androidsampleproject.common.entity.saved.SavedRelicVO
import com.sadturtleman.androidsampleproject.common.presentation.helper.showSavedToggleResult
import com.sadturtleman.androidsampleproject.common.presentation.mvi.MviViewModel
import com.sadturtleman.androidsampleproject.common.presentation.ui.model.ArtifactUiModel
import com.sadturtleman.androidsampleproject.common.presentation.ui.model.toArtifactUiModel
import com.sadturtleman.androidsampleproject.common.presentation.ui.model.toSavedRelicVO
import com.sadturtleman.androidsampleproject.detail.domain.DetailPage
import com.sadturtleman.androidsampleproject.home.domain.GetHomeRelicsUseCase
import com.sadturtleman.androidsampleproject.search.domain.SearchPage
import com.sadturtleman.androidsampleproject.search.domain.SearchResultPage
import com.sadturtleman.androidsampleproject.store.domain.StorePage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 홈 화면 ViewModel (Figma: 최종 → 01 · 홈).
 *
 * 상태 변이는 [reduce] 한 곳만 거치고, 외부 진입점은 [onIntent] 하나다.
 *
 * 목록은 `GET /openapi/list`(nationalityCode = 선택된 시대), 시대 칩은
 * `GET /openapi/code?parentCode=PS06001`(한국 시대) 에서 온다. 둘은 서로를 기다리지 않고 함께 나간다.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeRelics: GetHomeRelicsUseCase,
    private val getCodes: GetCodesUseCase,
    getSavedRelicIds: GetSavedRelicIdsUseCase,
    private val toggleSavedRelic: ToggleSavedRelicUseCase,
    private val navigationHelper: NavigationHelper,
    private val messageHelper: MessageHelper,
) : MviViewModel<HomeIntent, HomeUiState, HomeReducerEvent>(HomeUiState.Loading) {

    /** 저장 목록은 화면 밖(상세 · 보관함)에서도 바뀌므로 flow 로 계속 지켜본다. */
    private var savedIds: Set<String> = emptySet()

    /** 다음 조회에 쓸 시대 코드. 로딩 상태를 거치는 동안에도 선택이 유지되어야 해서 여기 둔다. */
    private var selectedEra: String? = null

    /** 시대 칩은 한 번 받으면 바뀌지 않는다. 칩을 옮길 때마다 코드 API 를 다시 부르지 않으려고 들고 있는다. */
    private var eraChips: List<EraChipUiModel> = listOf(ERA_CHIP_ALL)

    private var loadJob: Job? = null

    init {
        observeSavedIds(getSavedRelicIds)
        loadHome(eraCode = null)
    }

    override fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.OpenSearch -> navigationHelper.navigateTo(SearchPage)
            HomeIntent.OpenLibrary -> navigationHelper.navigateTo(StorePage)
            is HomeIntent.SelectEra -> selectEra(intent.era)
            HomeIntent.ClickHero -> openHero()
            HomeIntent.ClickSeeAll ->
                navigationHelper.navigateTo(SearchResultPage.Args(query = ""))

            is HomeIntent.ClickItem -> navigationHelper.navigateTo(DetailPage.Args(id = intent.id))
            is HomeIntent.ToggleSave -> toggleSave(intent.id)
            HomeIntent.Retry -> loadHome(eraCode = selectedEra)
        }
    }

    override fun reduce(state: HomeUiState, event: HomeReducerEvent): HomeUiState = when (event) {
        HomeReducerEvent.LoadStarted -> HomeUiState.Loading

        is HomeReducerEvent.LoadFailed -> HomeUiState.Error(event.message)

        is HomeReducerEvent.Loaded -> HomeUiState.Success(
            hero = event.hero,
            eras = event.eras,
            selectedEraCode = event.selectedEraCode,
            items = event.items.withSaved(savedIds),
        )

        is HomeReducerEvent.EraSelected -> state.mapSuccess { it.copy(selectedEraCode = event.code) }

        is HomeReducerEvent.SavedIdsChanged ->
            state.mapSuccess { it.copy(items = it.items.withSaved(event.ids)) }
    }

    private fun observeSavedIds(getSavedRelicIds: GetSavedRelicIdsUseCase) {
        getSavedRelicIds()
            .distinctUntilChanged()
            .onEach { ids ->
                savedIds = ids
                dispatch(HomeReducerEvent.SavedIdsChanged(ids))
            }
            .launchIn(viewModelScope)
    }

    /**
     * 북마크 토글. 보관함이 네트워크 없이 그려지도록 지금 카드에 보이는 값을 그대로 저장한다.
     */
    private fun toggleSave(id: String) {
        val success = currentState as? HomeUiState.Success ?: return
        val card = success.items.firstOrNull { it.id == id } ?: return
        viewModelScope.launch { toggleAndNotify(card.toSavedRelicVO()) }
    }

    /** 되돌리기까지 같은 경로를 타므로 토글과 알림을 한 자리에 묶는다. */
    private suspend fun toggleAndNotify(relic: SavedRelicVO) {
        val saved = toggleSavedRelic(relic)
        messageHelper.showSavedToggleResult(saved) {
            viewModelScope.launch { toggleSavedRelic(relic) }
        }
    }

    private fun selectEra(era: EraChipUiModel) {
        if (selectedEra == era.code) return
        selectedEra = era.code
        // 칩 선택은 즉시 보이고, 목록은 조회가 끝난 뒤 [HomeReducerEvent.Loaded] 로 교체된다.
        dispatch(HomeReducerEvent.EraSelected(era.code))
        loadHome(eraCode = era.code)
    }

    /**
     * 시대 칩과 오늘의 소장품을 함께 채운다.
     *
     * 칩을 빠르게 옮기면 앞선 조회가 늦게 도착해 목록이 뒤바뀔 수 있어, 새 조회는 앞의 것을 취소한다.
     * 목록 조회가 실패하면 화면 전체가 에러로 간다(칩만 남은 홈은 의미가 없다).
     */
    private fun loadHome(eraCode: String?) {
        loadJob?.cancel()
        dispatch(HomeReducerEvent.LoadStarted)
        loadJob = viewModelScope.launch {
            runCatching {
                coroutineScope {
                    val eras = async { loadEraChips() }
                    val page = async { getHomeRelics(eraCode) }
                    eras.await() to page.await()
                }
            }
                .onSuccess { (eras, page) -> dispatchLoaded(eras, page, eraCode) }
                .onFailure { throwable ->
                    dispatch(
                        HomeReducerEvent.LoadFailed(
                            throwable.message ?: "소장품을 불러오지 못했습니다."
                        )
                    )
                }
        }
    }

    private fun dispatchLoaded(
        eras: List<EraChipUiModel>,
        page: RelicPageVO,
        eraCode: String?,
    ) {
        val eraLabel = eras.firstOrNull { it.code == eraCode }?.label.takeIf { eraCode != null }
        val cards = page.items.map { it.toArtifactUiModel(era = eraLabel) }
        // 히어로는 화면 폭을 채우므로 카드용 200px 이 아니라 700px 이미지를 쓴다.
        val heroImageUrl = page.items.firstOrNull()
            ?.let { it.imgThumUriL ?: it.imgThumUriM ?: it.imgUri }
        dispatch(
            HomeReducerEvent.Loaded(
                hero = cards.firstOrNull()?.toHero(heroImageUrl),
                eras = eras,
                selectedEraCode = eraCode,
                // 첫 카드는 히어로로 올라갔으므로 그리드에서 뺀다.
                items = cards.drop(1),
            )
        )
    }

    /**
     * 시대 칩. 코드 조회가 실패해도 "전체" 칩만 남기고 목록은 그대로 보여준다.
     * (칩이 없다고 홈 전체를 에러로 떨어뜨릴 이유는 없다)
     */
    private suspend fun loadEraChips(): List<EraChipUiModel> {
        if (eraChips.size > 1) return eraChips
        val codes = runCatching { getCodes(GetCodesUseCase.ParentCode.NATIONALITY_KOREA) }
            .getOrDefault(emptyList())
        eraChips = listOf(ERA_CHIP_ALL) +
            codes.map { EraChipUiModel(code = it.code, label = it.label) }
        return eraChips
    }

    private fun openHero() {
        val heroId = (currentState as? HomeUiState.Success)?.hero?.id ?: return
        navigationHelper.navigateTo(DetailPage.Args(id = heroId))
    }

    private companion object {
        val ERA_CHIP_ALL = EraChipUiModel(code = null, label = "전체")
    }
}

/** 성공 상태에서만 변형을 적용한다. 로딩 · 에러 중 들어온 이벤트는 무시된다. */
private inline fun HomeUiState.mapSuccess(
    transform: (HomeUiState.Success) -> HomeUiState,
): HomeUiState = if (this is HomeUiState.Success) transform(this) else this

private fun List<ArtifactUiModel>.withSaved(
    savedIds: Set<String>,
) = map { it.copy(saved = it.id in savedIds) }

/** 첫 카드를 이달의 소장품 히어로로 승격한다. TODO(api): 큐레이션 API 가 생기면 교체. */
private fun ArtifactUiModel.toHero(imageUrl: String?) =
    HomeHeroUiModel(
        id = id,
        imageUrl = imageUrl,
        nameKr = nameKr,
        summary = spec.orEmpty(),
        meta = listOfNotNull(museum, era).filter { it.isNotBlank() }.joinToString(" · "),
        designation = designation,
        label = "이달의 소장품",
    )
