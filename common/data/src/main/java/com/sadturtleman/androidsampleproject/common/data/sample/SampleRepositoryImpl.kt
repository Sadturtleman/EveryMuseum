package com.sadturtleman.androidsampleproject.common.data.sample

import com.sadturtleman.androidsampleproject.common.domain.sample.SampleRepository
import com.sadturtleman.androidsampleproject.common.entity.sample.SampleItemVO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 인메모리 구현. 실제 프로젝트에서는 이 자리에 Remote/Local DataSource 조합이 들어간다.
 */
@Singleton
class SampleRepositoryImpl @Inject constructor() : SampleRepository {

    private val items: List<SampleItemVO> = List(ITEM_COUNT) { index ->
        SampleItemVO(
            id = "item-$index",
            title = "샘플 아이템 ${index + 1}",
            description = "Navigation3 백스택 동작 확인용 더미 데이터 ${index + 1}",
        )
    }

    private val favoriteIds = MutableStateFlow<Set<String>>(emptySet())

    override fun getItems(): List<SampleItemVO> = items

    override fun findById(id: String): SampleItemVO? = items.firstOrNull { it.id == id }

    override fun getFavoriteIdsFlow(): Flow<Set<String>> = favoriteIds.asStateFlow()

    override suspend fun toggleFavorite(id: String) {
        favoriteIds.update { current -> if (id in current) current - id else current + id }
    }

    private companion object {
        const val ITEM_COUNT = 12
    }
}
