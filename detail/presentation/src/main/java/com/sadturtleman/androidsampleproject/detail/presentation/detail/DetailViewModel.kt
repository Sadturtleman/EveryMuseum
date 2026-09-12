package com.sadturtleman.androidsampleproject.detail.presentation.detail

import androidx.lifecycle.viewModelScope
import com.sadturtleman.androidsampleproject.common.domain.helper.MessageHelper
import com.sadturtleman.androidsampleproject.common.navigation.NavigationHelper
import com.sadturtleman.androidsampleproject.common.domain.saved.GetSavedRelicIdsUseCase
import com.sadturtleman.androidsampleproject.common.domain.saved.ToggleSavedRelicUseCase
import com.sadturtleman.androidsampleproject.common.entity.relic.RelicDetailVO
import com.sadturtleman.androidsampleproject.common.entity.saved.SavedRelicVO
import com.sadturtleman.androidsampleproject.common.presentation.helper.showSavedToggleResult
import com.sadturtleman.androidsampleproject.common.presentation.mvi.MviViewModel
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.ArtifactType
import com.sadturtleman.androidsampleproject.common.presentation.ui.model.artifactTypeOf
import com.sadturtleman.androidsampleproject.common.presentation.ui.model.toArtifactUiModel
import com.sadturtleman.androidsampleproject.detail.navigation.DetailPage
import com.sadturtleman.androidsampleproject.detail.domain.GetRelicDetailUseCase
import com.sadturtleman.androidsampleproject.logging.domain.BizEvent
import com.sadturtleman.androidsampleproject.logging.domain.BizLogger
import com.sadturtleman.androidsampleproject.search.navigation.SearchResultPage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 소장품 상세 ViewModel (Figma: 최종 → 05 · 소장품 상세).
 *
 * 네비게이션 인자는 화면이 [DetailIntent.Load] 로 넣어 준다. 디코딩은 라우팅 표 한 곳에서만 한다.
 *
 * 표시 값은 넘어온 id 로 `GET /openapi/detail?id=` 을 다시 조회한다.
 * 그래야 백스택 복원 · 딥링크로 들어온 경우에도 같은 데이터를 보게 된다.
 * 없는 id 면 저장소가 예외를 던지고 화면은 에러 상태로 떨어진다.
 */
@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getRelicDetail: GetRelicDetailUseCase,
    private val getSavedRelicIds: GetSavedRelicIdsUseCase,
    private val toggleSavedRelic: ToggleSavedRelicUseCase,
    private val navigationHelper: NavigationHelper,
    private val messageHelper: MessageHelper,
    private val bizLogger: BizLogger,
) : MviViewModel<DetailIntent, DetailUiState, DetailReducerEvent>(DetailUiState.Loading) {

    /** 저장 여부는 보관함 · 다른 화면에서도 바뀌므로 flow 로 계속 지켜본다. */
    private var saved: Boolean = false

    /** [DetailIntent.Load] 로 받은 조회 키. 재시도할 때 다시 쓴다. */
    private var relicId: String? = null

    private var observeJob: Job? = null

    override fun onIntent(intent: DetailIntent) {
        when (intent) {
            is DetailIntent.Load -> load(intent.id)

            DetailIntent.Back -> navigationHelper.navigateToBack()

            DetailIntent.ToggleSave -> toggleSave()

            // TODO(design): 공유 시트가 정해지면 여기서 Intent.ACTION_SEND 를 띄운다.
            DetailIntent.Share -> Unit

            is DetailIntent.SelectImage -> selectImage(intent.image)

            is DetailIntent.ClickRelated ->
                navigationHelper.navigateTo(DetailPage.Args(id = intent.id))

            DetailIntent.ClickRelatedSeeAll ->
                navigationHelper.navigateTo(SearchResultPage.Args(query = ""))

            DetailIntent.Retry -> loadDetail()
        }
    }

    override fun reduce(
        state: DetailUiState,
        event: DetailReducerEvent,
    ): DetailUiState = when (event) {
        DetailReducerEvent.LoadStarted -> DetailUiState.Loading

        is DetailReducerEvent.LoadFailed -> DetailUiState.Error(event.message)

        // 저장 여부는 조회와 별개로 흘러오므로 로딩이 끝나는 시점의 최신 값을 덧씌운다.
        is DetailReducerEvent.Loaded -> event.success.copy(saved = saved)

        is DetailReducerEvent.SavedChanged -> state.mapSuccess { it.copy(saved = event.saved) }

        is DetailReducerEvent.ImageSelected ->
            state.mapSuccess { it.copy(currentImageIndex = event.index) }
    }

    /**
     * 인자를 받아 조회를 시작한다.
     *
     * 두 번째부터는 무시한다 — 백스택 엔트리마다 ViewModel 이 따로 살아 있어
     * 한 인스턴스가 다른 id 를 받는 일은 없고, 회전으로 화면이 다시 구성돼도 재조회하지 않아야 한다.
     */
    private fun load(id: String) {
        if (relicId != null) return
        relicId = id
        // 목록의 클릭이 아니라 여기서 남긴다 — 딥링크나 백스택 복원으로 들어온 것도 같이 잡힌다.
        bizLogger.record(DetailPage.PATH, BizEvent.RelicOpen(relicId = id))
        observeSaved(id)
        loadDetail()
    }

    private fun observeSaved(id: String) {
        observeJob?.cancel()
        observeJob = getSavedRelicIds()
            .map { ids -> id in ids }
            .distinctUntilChanged()
            .onEach { isSaved ->
                saved = isSaved
                dispatch(DetailReducerEvent.SavedChanged(isSaved))
            }
            .launchIn(viewModelScope)
    }

    /**
     * 북마크 토글. 보관함 카드에 그릴 값을 지금 상세 상태에서 만들어 넘긴다.
     * 아직 로딩 중이면(성공 상태가 아니면) 저장할 표시값이 없으므로 무시한다.
     */
    private fun toggleSave() {
        val success = currentState as? DetailUiState.Success ?: return
        val relic = SavedRelicVO(
            id = success.id,
            nameKr = success.nameKr,
            museum = success.credit,
            era = success.era,
            designation = success.designation,
            imageUrl = success.images.getOrNull(success.currentImageIndex)?.url,
            type = success.type.name,
        )
        viewModelScope.launch {
            val saved = toggleSavedRelic(relic)
            // 토글 뒤의 상태를 남긴다. 담은 것과 뺀 것이 한 이벤트로 모인다.
            bizLogger.record(DetailPage.PATH, BizEvent.SaveToggle(relicId = relic.id, saved = saved))
            messageHelper.showSavedToggleResult(saved) {
                viewModelScope.launch { toggleSavedRelic(relic) }
            }
        }
    }

    private fun loadDetail() {
        val id = relicId ?: return
        dispatch(DetailReducerEvent.LoadStarted)
        viewModelScope.launch {
            runCatching { getRelicDetail(id) }
                .onSuccess { detail -> dispatch(DetailReducerEvent.Loaded(detail.toSuccess())) }
                .onFailure { throwable ->
                    dispatch(
                        DetailReducerEvent.LoadFailed(
                            throwable.message ?: "소장품을 불러오지 못했습니다."
                        )
                    )
                }
        }
    }

    private fun selectImage(image: DetailImageUiModel) {
        val success = currentState as? DetailUiState.Success ?: return
        val index = success.images.indexOfFirst { it.id == image.id }
        if (index < 0) return
        dispatch(DetailReducerEvent.ImageSelected(index))
    }

    private companion object {
        const val UNKNOWN_NATIONALITY = "미상"
    }

    /** 상세 응답을 화면이 요구하는 성공 상태로 옮긴다. */
    private fun RelicDetailVO.toSuccess(): DetailUiState.Success {
        val type = artifactTypeOf(relic.materialCode)
        return DetailUiState.Success(
            id = relic.id,
            nameKr = relic.nameKr?.takeIf { it.isNotBlank() }
                ?: relic.nameCn?.takeIf { it.isNotBlank() }
                ?: relic.name.orEmpty(),
            // 한자명이 한글명과 같으면(한자 제목인 소장품) 같은 줄을 두 번 그리지 않는다.
            nameCn = relic.nameCn?.takeIf { it.isNotBlank() && it != relic.nameKr },
            credit = credit(),
            type = type,
            designation = designationName1,
            era = era(),
            images = images(type),
            currentImageIndex = 0,
            metaRows = metaRows(),
            description = description?.takeIf { it.isNotBlank() },
            related = relations.map { it.toArtifactUiModel() },
            saved = saved,
        )
    }

    /** `한국 · 조선  ·  국립중앙박물관 · 본관  ·  소장품번호 1` 형태의 한 줄. */
    private fun RelicDetailVO.credit(): String = listOfNotNull(
        era() ?: UNKNOWN_NATIONALITY,
        relic.museumLabel.takeIf { it.isNotBlank() },
        relic.relicNo?.takeIf { it.isNotBlank() }?.let { "소장품번호 $it" },
    ).joinToString(" · ")

    /** 국적 · 시대. 시대(nationalityName2)까지 오면 그쪽이 더 구체적이다. */
    private fun RelicDetailVO.era(): String? = listOfNotNull(
        nationalityName1?.takeIf { it.isNotBlank() },
        nationalityName2?.takeIf { it.isNotBlank() },
    ).joinToString(" · ").takeIf { it.isNotBlank() }

    /**
     * 공개 이미지. imageList 가 비어 있어도 본문에 원본 이미지가 오는 경우가 있어 그것으로 물러난다.
     * 상세는 큰 화면이라 700px(L) 를 먼저 쓴다.
     */
    private fun RelicDetailVO.images(type: ArtifactType) =
        images.map { image ->
            DetailImageUiModel(
                id = image.imgId.takeIf { it.isNotBlank() } ?: relic.id,
                url = image.imgThumUriL ?: image.imgThumUriM ?: image.imgUri,
                type = type,
            )
        }.ifEmpty {
            val url = relic.imgThumUriL ?: relic.imgThumUriM ?: relic.imgUri
            listOfNotNull(url?.let { DetailImageUiModel(id = relic.id, url = it, type = type) })
        }

    /** 소장품 정보 표. 값이 없는 행은 아예 만들지 않는다(빈 줄을 그리지 않기 위해). */
    private fun RelicDetailVO.metaRows(): List<DetailMetaUiModel> = listOfNotNull(
        metaRow("국적 · 시대", era()),
        metaRow("재질", joinNames(materialName1, materialName2)),
        metaRow("용도 · 기능", joinNames(purposeName1, purposeName2, purposeName3, purposeName4)),
        metaRow("크기", sizeInfo ?: sizeRangeName),
        metaRow("지정", designationName1),
        metaRow("소장기관", relic.museumLabel),
        metaRow("소장품번호", relic.relicNo),
    )

    private fun metaRow(label: String, value: String?): DetailMetaUiModel? =
        value?.takeIf { it.isNotBlank() }?.let { DetailMetaUiModel(label = label, value = it) }

    /** 계층 이름(대분류 → 소분류)을 한 줄로 잇는다. 예: `금속 · 철` */
    private fun joinNames(vararg names: String?): String? = names
        .filter { !it.isNullOrBlank() }
        .joinToString(" · ")
        .takeIf { it.isNotBlank() }
}

private inline fun DetailUiState.mapSuccess(
    transform: (DetailUiState.Success) -> DetailUiState,
): DetailUiState = if (this is DetailUiState.Success) transform(this) else this
