package com.sadturtleman.androidsampleproject.common.domain.sample

import com.sadturtleman.androidsampleproject.common.entity.sample.SampleItemVO
import kotlinx.coroutines.flow.Flow

/**
 * 네비게이션 구조 시연용 저장소 계약. 구현은 :common:data 에 있다.
 * Home / Favorite / Detail 세 화면이 함께 쓰므로 feature 가 아닌 common 에 둔다.
 */
interface SampleRepository {
    fun getItems(): List<SampleItemVO>

    fun findById(id: String): SampleItemVO?

    fun getFavoriteIdsFlow(): Flow<Set<String>>

    suspend fun toggleFavorite(id: String)
}
