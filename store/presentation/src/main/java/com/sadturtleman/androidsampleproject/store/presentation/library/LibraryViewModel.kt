package com.sadturtleman.androidsampleproject.store.presentation.library

import androidx.lifecycle.viewModelScope
import com.sadturtleman.androidsampleproject.common.domain.helper.MessageHelper
import com.sadturtleman.androidsampleproject.common.navigation.NavigationHelper
import com.sadturtleman.androidsampleproject.common.domain.saved.ToggleSavedRelicUseCase
import com.sadturtleman.androidsampleproject.detail.navigation.DetailPage
import com.sadturtleman.androidsampleproject.logging.domain.BizEvent
import com.sadturtleman.androidsampleproject.logging.domain.BizLogger
import com.sadturtleman.androidsampleproject.store.navigation.StorePage
import com.sadturtleman.androidsampleproject.search.navigation.SearchPage
import com.sadturtleman.androidsampleproject.store.domain.GetSavedRelicsUseCase
import com.sadturtleman.androidsampleproject.common.presentation.helper.showSavedToggleResult
import com.sadturtleman.androidsampleproject.common.presentation.mvi.MviViewModel
import com.sadturtleman.androidsampleproject.common.presentation.ui.model.toArtifactUiModel
import com.sadturtleman.androidsampleproject.common.presentation.ui.model.toSavedRelicVO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 보관함 ViewModel (Figma: 최종 → 06 · 보관함).
 *
 * 화면의 원본은 저장 목록 flow 하나다. 다른 화면에서 저장/해제해도 같은 flow 로 흘러들어오므로
 * 별도 새로고침 경로를 두지 않는다.
 *
 * 목록은 저장 시점의 표시값(:store:data 의 SharedPreferences 저장본)이라 네트워크를 타지 않는다.
 */
@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val getSavedRelics: GetSavedRelicsUseCase,
    private val toggleSavedRelic: ToggleSavedRelicUseCase,
    private val navigationHelper: NavigationHelper,
    private val messageHelper: MessageHelper,
    private val bizLogger: BizLogger,
) : MviViewModel<LibraryIntent, LibraryUiState, LibraryReducerEvent>(LibraryUiState.Loading) {

    private var sortIndex: Int = 0

    /** 목록 ↔ 그리드는 조회와 무관한 사용자 선택이라, 갱신으로 로딩을 거쳐도 유지되도록 여기 둔다. */
    private var layout: LibraryLayout = LibraryLayout.List
    private var observeJob: Job? = null

    init {
        observeSavedItems()
    }

    override fun onIntent(intent: LibraryIntent) {
        when (intent) {
            LibraryIntent.ToggleLayout -> toggleLayout()

            LibraryIntent.ToggleSort -> {
                sortIndex = (sortIndex + 1) % SORTS.size
                observeSavedItems()
            }

            is LibraryIntent.ClickItem ->
                navigationHelper.navigateTo(DetailPage.Args(id = intent.id))

            is LibraryIntent.ToggleSave -> toggleSave(intent.id)

            LibraryIntent.Explore -> navigationHelper.navigateTo(SearchPage)

            LibraryIntent.Retry -> observeSavedItems()

        }
    }

    override fun reduce(
        state: LibraryUiState,
        event: LibraryReducerEvent,
    ): LibraryUiState = when (event) {
        LibraryReducerEvent.LoadStarted -> LibraryUiState.Loading

        is LibraryReducerEvent.LoadFailed -> LibraryUiState.Error(event.message)

        is LibraryReducerEvent.Loaded -> LibraryUiState.Success(
            items = event.items,
            sort = event.sort,
            layout = event.layout,
        )

        is LibraryReducerEvent.LayoutChanged -> state.mapSuccess { it.copy(layout = event.layout) }
    }

    private fun toggleLayout() {
        layout = when (layout) {
            LibraryLayout.List -> LibraryLayout.Grid
            LibraryLayout.Grid -> LibraryLayout.List
        }
        bizLogger.record(StorePage.PATH, BizEvent.LayoutToggle(layout = layout.name))
        dispatch(LibraryReducerEvent.LayoutChanged(layout))
    }

    /** 저장 목록 구독을 (재)시작한다. 정렬을 바꿀 때도 같은 경로를 다시 탄다. */
    private fun observeSavedItems() {
        observeJob?.cancel()
        dispatch(LibraryReducerEvent.LoadStarted)
        observeJob = getSavedRelics()
            .onEach { items ->
                // 저장소가 최근 저장순으로 내보내므로 그 순서가 기본값이다.
                val cards = items.map { it.toArtifactUiModel() }
                dispatch(
                    LibraryReducerEvent.Loaded(
                        items = if (SORTS[sortIndex] == SORT_NAME) {
                            cards.sortedBy { it.nameKr }
                        } else {
                            cards
                        },
                        sort = SORTS[sortIndex],
                        layout = layout,
                    )
                )
            }
            .catch { throwable ->
                dispatch(
                    LibraryReducerEvent.LoadFailed(
                        throwable.message ?: "보관함을 불러오지 못했습니다."
                    )
                )
            }
            .launchIn(viewModelScope)
    }

    /**
     * 북마크 토글. 보관함 목록은 저장된 것만 있으므로 결과는 언제나 "해제" 이고,
     * 목록에서 바로 빠진다(저장 목록 flow 가 다시 흘러온다).
     */
    private fun toggleSave(id: String) {
        val success = currentState as? LibraryUiState.Success ?: return
        val relic = success.items.firstOrNull { it.id == id }?.toSavedRelicVO() ?: return
        viewModelScope.launch {
            val saved = toggleSavedRelic(relic)
            bizLogger.record(StorePage.PATH, BizEvent.SaveToggle(relicId = relic.id, saved = saved))
            messageHelper.showSavedToggleResult(saved) {
                viewModelScope.launch { toggleSavedRelic(relic) }
            }
        }
    }

    private companion object {
        const val SORT_RECENT = "최근 저장순"
        const val SORT_NAME = "이름순"

        val SORTS = listOf(SORT_RECENT, SORT_NAME)
    }
}

private inline fun LibraryUiState.mapSuccess(
    transform: (LibraryUiState.Success) -> LibraryUiState,
): LibraryUiState = if (this is LibraryUiState.Success) transform(this) else this
