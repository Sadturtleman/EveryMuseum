package com.sadturtleman.androidsampleproject.common.data.remote

import com.sadturtleman.androidsampleproject.common.network.EmuseumResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

/**
 * e뮤지엄 오픈API (공공데이터포털 15159017).
 *
 * base url 과 인증키 주입은 :common:network 가 맡는다(`EmuseumNetworkModule`).
 * 여러 화면이 함께 쓰는 오퍼레이션을 둔다. 이 인터페이스가 앱의 공용 코어 API 이고,
 * 이걸 어떤 조건으로 부를지는 각 feature 의 :data 가 자기 저장소에서 정한다
 * (:home:data 의 오늘의 소장품, :search:data 의 검색 · 건수).
 *
 * 상세(`openapi/detail`)는 이 화면만 쓰므로 :detail:data 가 자기 인터페이스로 따로 정의한다.
 */
interface EmuseumApi {

    /**
     * 소장품 목록 · 검색.
     *
     * @param filters 조건 파라미터. 값이 없는 조건은 아예 빼야 하므로 맵으로 받는다.
     *  (빈 문자열을 넘기면 조건이 걸린 것으로 처리되어 결과가 0건이 된다)
     */
    @GET("openapi/list")
    suspend fun getRelicList(@QueryMap filters: Map<String, String>): EmuseumResponse

    /**
     * 분류 코드 목록.
     *
     * @param numOfRows 기본값이 10 이라 넘기지 않으면 코드가 잘린다
     *  (시대 PS06001 은 23 개, 재질 PS08 은 18 개다). 한 상위 코드의 자식은 많아야 수십 개라 한 번에 받는다.
     */
    @GET("openapi/code")
    suspend fun getCodeList(
        @Query("parentCode") parentCode: String,
        @Query("numOfRows") numOfRows: Int,
    ): EmuseumResponse
}
