package com.sadturtleman.androidsampleproject.detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sadturtleman.androidsampleproject.common.domain.helper.NavigationHelper
import com.sadturtleman.androidsampleproject.common.domain.sample.GetFavoriteIdsUseCase
import com.sadturtleman.androidsampleproject.common.domain.sample.GetSampleItemUseCase
import com.sadturtleman.androidsampleproject.common.domain.sample.ToggleFavoriteUseCase
import com.sadturtleman.androidsampleproject.common.entity.sample.SampleItemVO
import com.sadturtleman.androidsampleproject.detail.domain.DetailPage
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * 네비게이션 인자를 받는 ViewModel 의 표준 형태.
 *
 * Hilt 의 assisted injection 으로 [DetailPage.Args] 를 생성자에서 그대로 받는다.
 * (SavedStateHandle 에서 문자열을 다시 파싱하지 않으므로 디코딩 지점이 라우팅 테이블 한 곳으로 모인다.)
 *
 * NavDisplay 의 rememberViewModelStoreNavEntryDecorator 덕분에 백스택 엔트리 단위로 살아 있다가,
 * 해당 엔트리가 pop 될 때 onCleared 된다.
 *
 * TODO(design): Figma EveryMuseum 시안 확정 후 상세 UiState / API 연동으로 교체.
 */
@HiltViewModel(assistedFactory = DetailViewModel.Factory::class)
class DetailViewModel @AssistedInject constructor(
    @Assisted private val args: DetailPage.Args,
    getSampleItem: GetSampleItemUseCase,
    getFavoriteIds: GetFavoriteIdsUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val navigationHelper: NavigationHelper,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(args: DetailPage.Args): DetailViewModel
    }

    /** 표시에 필요한 값은 id 로 다시 조회한다. 딥링크로 들어와 없는 id 면 null. */
    val item: SampleItemVO? = getSampleItem(args.id)

    val favoriteIds: StateFlow<Set<String>> = getFavoriteIds()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            initialValue = emptySet(),
        )

    fun onClickFavorite() {
        val id = item?.id ?: return
        viewModelScope.launch { toggleFavorite(id) }
    }

    fun onClickBack() {
        navigationHelper.navigateToBack()
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
