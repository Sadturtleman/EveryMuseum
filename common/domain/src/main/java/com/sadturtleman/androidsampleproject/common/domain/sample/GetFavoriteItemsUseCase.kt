package com.sadturtleman.androidsampleproject.common.domain.sample

import com.sadturtleman.androidsampleproject.common.entity.sample.SampleItemVO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetFavoriteItemsUseCase @Inject constructor(
    private val sampleRepository: SampleRepository,
) {
    operator fun invoke(): Flow<List<SampleItemVO>> =
        sampleRepository.getFavoriteIdsFlow().map { ids ->
            sampleRepository.getItems().filter { it.id in ids }
        }
}
