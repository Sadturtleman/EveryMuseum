package com.sadturtleman.androidsampleproject.common.domain.sample

import com.sadturtleman.androidsampleproject.common.entity.sample.SampleItemVO
import javax.inject.Inject

class GetSampleItemsUseCase @Inject constructor(
    private val sampleRepository: SampleRepository,
) {
    operator fun invoke(): List<SampleItemVO> = sampleRepository.getItems()
}
