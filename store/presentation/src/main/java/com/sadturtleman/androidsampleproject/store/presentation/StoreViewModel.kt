package com.sadturtleman.androidsampleproject.store.presentation

import androidx.lifecycle.ViewModel
import com.sadturtleman.androidsampleproject.common.domain.helper.NavigationHelper
import com.sadturtleman.androidsampleproject.common.domain.sample.GetSampleItemsUseCase
import com.sadturtleman.androidsampleproject.common.entity.sample.SampleItemVO
import com.sadturtleman.androidsampleproject.detail.domain.DetailPage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * TODO(design): Figma EveryMuseum 시안 확정 후 실제 화면 정의로 교체.
 * 현재는 탭 루트 + Detail 이동 경로만 검증하는 더미 목록.
 */
@HiltViewModel
class StoreViewModel @Inject constructor(
    getSampleItems: GetSampleItemsUseCase,
    private val navigationHelper: NavigationHelper,
) : ViewModel() {

    private val _items = MutableStateFlow(getSampleItems())
    val items: StateFlow<List<SampleItemVO>> = _items.asStateFlow()

    fun onClickItem(item: SampleItemVO) {
        navigationHelper.navigateTo(DetailPage.Args(id = item.id))
    }
}
