package com.sadturtleman.androidsampleproject.common.network

import okhttp3.Interceptor
import okhttp3.Response

/**
 * 모든 요청에 공공데이터포털 인증키를 붙인다.
 *
 * 발급된 키는 이미 퍼센트 인코딩된 문자열(`8%2Fkka...%3D%3D`)이다.
 * `addQueryParameter` 를 쓰면 `%` 가 다시 인코딩되어(`%252F`) 인증에 실패하므로
 * 반드시 [okhttp3.HttpUrl.Builder.addEncodedQueryParameter] 로 넣어야 한다.
 */
internal class ServiceKeyInterceptor(
    private val serviceKey: String,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.newBuilder()
            .addEncodedQueryParameter(QUERY_SERVICE_KEY, serviceKey)
            .build()
        return chain.proceed(request.newBuilder().url(url).build())
    }

    private companion object {
        const val QUERY_SERVICE_KEY = "serviceKey"
    }
}
