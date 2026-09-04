package com.sadturtleman.androidsampleproject.search.presentation

import androidx.lifecycle.ViewModel
import com.sadturtleman.androidsampleproject.common.domain.helper.NavigationHelper
import com.sadturtleman.androidsampleproject.common.domain.sample.GetSampleItemsUseCase
import com.sadturtleman.androidsampleproject.common.entity.sample.SampleItemVO
import com.sadturtleman.androidsampleproject.detail.domain.DetailPage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * TODO(design): Figma EveryMuseum 시안 확정 후 UiState / 검색 API 연동으로 교체.
 * 현재는 네비게이션 구조 검증용 더미 목록.
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getSampleItems: GetSampleItemsUseCase,
    private val navigationHelper: NavigationHelper,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _items = MutableStateFlow(getSampleItems())
    val items: StateFlow<List<SampleItemVO>> = _items.asStateFlow()

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
        _items.update {
            getSampleItems().filter { item -> item.title.contains(newQuery, ignoreCase = true) }
        }
    }

    /** typed Args 로 이동. 대상 화면 구현이 아니라 :detail:domain 의 Page 정의에만 의존한다. */
    fun onClickItem(item: SampleItemVO) {
        navigationHelper.navigateTo(DetailPage.Args(id = item.id))
    }
}
