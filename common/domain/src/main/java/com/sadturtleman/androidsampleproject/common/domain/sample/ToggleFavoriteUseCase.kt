package com.sadturtleman.androidsampleproject.common.domain.sample

import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val sampleRepository: SampleRepository,
) {
    suspend operator fun invoke(id: String) = sampleRepository.toggleFavorite(id)
}
