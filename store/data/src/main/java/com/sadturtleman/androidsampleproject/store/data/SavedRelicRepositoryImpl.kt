package com.sadturtleman.androidsampleproject.store.data

import com.sadturtleman.androidsampleproject.common.domain.saved.SavedRelicRepository
import com.sadturtleman.androidsampleproject.common.entity.saved.SavedRelicVO
import com.sadturtleman.androidsampleproject.store.data.local.toDto
import com.sadturtleman.androidsampleproject.store.data.local.toVO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 보관함 저장소 구현.
 *
 * 저장 시각 내림차순(최근 저장이 앞)으로 정렬해 내보낸다.
 * 화면이 "이름순" 을 고르면 그때 다시 정렬한다.
 */
internal class SavedRelicRepositoryImpl(
    private val dataSource: SavedRelicDataSource,
) : SavedRelicRepository {

    override fun getSavedRelicsFlow(): Flow<List<SavedRelicVO>> =
        dataSource.getSavedRelicsFlow().map { items ->
            items.map { it.toVO() }.sortedByDescending { it.savedAt }
        }

    override suspend fun toggle(relic: SavedRelicVO): Boolean = dataSource.toggle(relic.toDto())
}
