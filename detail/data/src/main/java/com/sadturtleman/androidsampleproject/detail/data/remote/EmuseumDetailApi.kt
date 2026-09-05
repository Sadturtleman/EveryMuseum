package com.sadturtleman.androidsampleproject.detail.data.remote

import com.sadturtleman.androidsampleproject.common.network.EmuseumResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 상세 조회 오퍼레이션. 이 feature 만 쓰므로 여기 둔다.
 *
 * base url 과 인증키 주입은 :common:network 가 맡고(`EmuseumNetworkModule`),
 * 이 인터페이스는 그 Retrofit 위에 얹힌다.
 */
internal interface EmuseumDetailApi {

    /** 소장품 상세. 응답에 list · imageList · relationList 세 묶음이 온다. */
    @GET("openapi/detail")
    suspend fun getRelicDetail(@Query("id") id: String): EmuseumResponse
}
