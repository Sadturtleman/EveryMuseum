package com.sadturtleman.androidsampleproject.common.presentation.ui.image

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade

/**
 * 소장품 사진 로더. Application 이 Coil 싱글턴으로 등록한다.
 *
 * 네트워크 계층까지 여기서 정하는 이유는, 오픈API 용 OkHttpClient 를 재사용하면 안 되기 때문이다 —
 * 그쪽은 요청마다 `serviceKey` 를 붙이는데 이미지 주소에는 이미 이미지 전용 키가 들어 있어
 * 두 번 실리면 이미지가 오지 않는다.
 */
fun museumImageLoader(context: PlatformContext): ImageLoader =
    ImageLoader.Builder(context)
        .components { add(OkHttpNetworkFetcherFactory()) }
        .crossfade(true)
        .build()
