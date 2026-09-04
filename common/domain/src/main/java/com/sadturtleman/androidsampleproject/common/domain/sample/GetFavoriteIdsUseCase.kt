package com.sadturtleman.androidsampleproject.common.domain.sample

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoriteIdsUseCase @Inject constructor(
    private val sampleRepository: SampleRepository,
) {
    operator fun invoke(): Flow<Set<String>> = sampleRepository.getFavoriteIdsFlow()
}
