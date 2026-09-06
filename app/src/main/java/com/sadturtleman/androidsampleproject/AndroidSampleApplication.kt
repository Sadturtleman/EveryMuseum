package com.sadturtleman.androidsampleproject

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import com.sadturtleman.androidsampleproject.common.presentation.ui.image.museumImageLoader
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AndroidSampleApplication : Application(), SingletonImageLoader.Factory {

    /** 소장품 사진 로더. 구성은 :common:presentation 이 정한다. */
    override fun newImageLoader(context: PlatformContext): ImageLoader =
        museumImageLoader(context)
}
