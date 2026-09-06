package com.sadturtleman.androidsampleproject.search.presentation.filter

import androidx.lifecycle.viewModelScope
import com.sadturtleman.androidsampleproject.common.domain.code.GetCodesUseCase
import com.sadturtleman.androidsampleproject.common.presentation.mvi.MviViewModel
import com.sadturtleman.androidsampleproject.search.domain.CountRelicsUseCase
import com.sadturtleman.androidsampleproject.search.presentation.result.AppliedFilterUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 필터 바텀시트 ViewModel (Figma: 최종 → 04 · 필터).
 *
 * 검색 결과 화면과 같은 백스택 엔트리에 살면서 선택 상태만 들고 있다가,
 * 확인 시점에 [FilterUiState.Success.selectedFilters] 로 결과를 넘긴다.
 * (필터가 스스로 목록을 조회하지는 않는다 — 예상 건수만 센다)
 *
 * 탭의 옵션은 `GET /openapi/code?parentCode=<탭 코드>`, 확인 버튼의 예상 건수는
 * [CountRelicsUseCase] 가 목록 API 에서 totalCount 만 읽어 온다.
 */
@HiltViewModel
class FilterViewModel @Inject constructor(
    private val getCodes: GetCodesUseCase,
    private val countRelics: CountRelicsUseCase,
) : MviViewModel<FilterIntent, FilterUiState, FilterReducerEvent>(
    FilterUiState.Loading(tabs = TABS, selectedTabCode = TABS.first().parentCode),
) {

    /**
     * 지금까지 고른 옵션. 탭 전환 시 로딩 상태를 거치므로 화면 상태가 아니라 여기가 원본이다.
     * (확인 시점에 이 목록이 검색 결과 화면으로 넘어간다)
     */
    private var selectedFilters: List<AppliedFilterUiModel> = emptyList()

    /** 예상 건수를 세는 데 쓰는 질의어. 시트를 띄운 화면이 [FilterIntent.Open] 으로 넣어 준다. */
    private var query: String = ""

    /** 옵션을 한 번이라도 불러왔는지. 시트를 처음 열 때만 초기 조회를 돌리기 위한 표시다. */
    private var loaded = false

    private var optionsJob: Job? = null
    private var countJob: Job? = null

    override fun onIntent(intent: FilterIntent) {
        when (intent) {
            is FilterIntent.Open -> open(intent.query, intent.tabCode)

            is FilterIntent.SelectTab -> loadOptions(intent.tab.parentCode)

            is FilterIntent.ToggleOption -> toggleOption(intent.option)

            FilterIntent.Reset -> {
                selectedFilters = emptyList()
                dispatch(FilterReducerEvent.SelectionChanged(emptyList()))
                refreshResultCount()
            }
            // 시트를 띄운 화면이 가로채는 인텐트라 여기까지 오면 할 일이 없다.
            FilterIntent.Apply -> Unit

            FilterIntent.Retry -> loadOptions(selectedTabCode())
        }
    }

    override fun reduce(
        state: FilterUiState,
        event: FilterReducerEvent,
    ): FilterUiState = when (event) {
        is FilterReducerEvent.LoadStarted -> FilterUiState.Loading(
            tabs = event.tabs,
            selectedTabCode = event.selectedTabCode,
        )

        is FilterReducerEvent.LoadFailed -> FilterUiState.Error(event.message)

        is FilterReducerEvent.OptionsLoaded -> FilterUiState.Success(
            tabs = event.tabs,
            selectedTabCode = event.selectedTabCode,
            options = event.options,
            selectedFilters = event.selectedFilters,
            resultCount = event.resultCount,
        )

        is FilterReducerEvent.SelectionChanged ->
            state.mapSuccess { it.copy(selectedFilters = event.filters) }

        is FilterReducerEvent.ResultCountChanged ->
            state.mapSuccess { it.copy(resultCount = event.count) }
    }

    /**
     * 탭의 옵션과 예상 건수를 함께 채운다.
     * 탭을 빠르게 옮기면 앞선 조회가 늦게 도착해 옵션이 뒤바뀔 수 있어, 새 조회는 앞의 것을 취소한다.
     */
    private fun loadOptions(parentCode: String) {
        optionsJob?.cancel()
        dispatch(FilterReducerEvent.LoadStarted(TABS, parentCode))
        optionsJob = viewModelScope.launch {
            runCatching {
                coroutineScope {
                    val options = async { getCodes(parentCode) }
                    val count = async { resultCount() }
                    options.await() to count.await()
                }
            }
                .onSuccess { (codes, count) ->
                    dispatch(
                        FilterReducerEvent.OptionsLoaded(
                            tabs = TABS,
                            selectedTabCode = parentCode,
                            options = codes.map {
                                FilterOptionUiModel(code = it.code, label = it.label)
                            },
                            selectedFilters = selectedFilters,
                            resultCount = count,
                        )
                    )
                }
                .onFailure { throwable ->
                    dispatch(
                        FilterReducerEvent.LoadFailed(
                            throwable.message ?: "필터 항목을 불러오지 못했습니다."
                        )
                    )
                }
        }
    }

    private fun toggleOption(option: FilterOptionUiModel) {
        selectedFilters = if (selectedFilters.any { it.code == option.code }) {
            selectedFilters.filterNot { it.code == option.code }
        } else {
            selectedFilters + AppliedFilterUiModel(code = option.code, label = option.label)
        }
        dispatch(FilterReducerEvent.SelectionChanged(selectedFilters))
        refreshResultCount()
    }

    /**
     * 시트가 열렸다. 처음이면 옵션을 불러오고, 두 번째부터는 달라진 것만 따라간다.
     *
     * 질의어를 옵션 조회보다 **먼저** 넣는다 — [loadOptions] 가 함께 세는 예상 건수에 그대로 쓰인다.
     * 시트를 여닫을 때마다 코드 API 를 다시 치지는 않는다.
     */
    private fun open(newQuery: String, tabCode: String?) {
        val queryChanged = query != newQuery
        query = newQuery
        val requestedTab = tabCode?.takeIf { code -> TABS.any { it.parentCode == code } }
        when {
            !loaded -> {
                loaded = true
                loadOptions(requestedTab ?: TABS.first().parentCode)
            }
            // 탭이 바뀌면 옵션을 다시 부르고, 그때 예상 건수도 같이 따라온다.
            requestedTab != null && requestedTab != selectedTabCode() -> loadOptions(requestedTab)

            queryChanged -> refreshResultCount()
        }
    }

    /**
     * 예상 건수만 다시 센다.
     * 건수 조회가 실패해도 시트는 그대로 둔다 — 버튼 라벨의 숫자 하나 때문에 필터를 닫을 이유는 없다.
     */
    private fun refreshResultCount() {
        countJob?.cancel()
        countJob = viewModelScope.launch {
            runCatching { resultCount() }
                .onSuccess { dispatch(FilterReducerEvent.ResultCountChanged(it)) }
        }
    }

    private suspend fun resultCount(): Int =
        countRelics(query = query, filterCodes = selectedFilters.map { it.code })

    private fun selectedTabCode(): String = when (val state = currentState) {
        is FilterUiState.Success -> state.selectedTabCode
        is FilterUiState.Loading -> state.selectedTabCode ?: TABS.first().parentCode
        is FilterUiState.Error -> TABS.first().parentCode
    }

    private companion object {
        /**
         * 분류 코드 탭.
         *
         * 시대는 `PS06`(국적) 이 아니라 그 아래 `PS06001`(한국) 을 편다 —
         * PS06 의 직계는 한국 · 아시아 · 유럽 … 대륙이라 시대 칩이 되지 않는다.
         *
         * 시안의 "출토지"(GL05) 는 목록 API 에 대응하는 파라미터가 없어 빠졌다.
         * 대신 걸리는 것이 확인된 크기(PS15) 를 둔다.
         */
        val TABS = listOf(
            FilterTabUiModel(parentCode = GetCodesUseCase.ParentCode.NATIONALITY_KOREA, label = "시대"),
            FilterTabUiModel(parentCode = GetCodesUseCase.ParentCode.MATERIAL, label = "재질"),
            FilterTabUiModel(parentCode = GetCodesUseCase.ParentCode.MUSEUM, label = "소장기관"),
            FilterTabUiModel(parentCode = GetCodesUseCase.ParentCode.SIZE_RANGE, label = "크기"),
        )
    }
}

/** 시트에서 고른 옵션을 검색 결과 화면이 그릴 칩 목록으로 넘긴다. */
internal fun FilterUiState.appliedFilters(): List<AppliedFilterUiModel> =
    (this as? FilterUiState.Success)?.selectedFilters.orEmpty()

private inline fun FilterUiState.mapSuccess(
    transform: (FilterUiState.Success) -> FilterUiState,
): FilterUiState = if (this is FilterUiState.Success) transform(this) else this
