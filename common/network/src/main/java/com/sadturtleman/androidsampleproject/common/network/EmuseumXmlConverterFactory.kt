package com.sadturtleman.androidsampleproject.common.network

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * 응답 본문을 [EmuseumResponse] 로 바꾸는 Retrofit 컨버터.
 *
 * e뮤지엄은 XML 만 내려주므로 직렬화 컨버터 대신 [EmuseumXmlParser] 를 쓴다.
 * [EmuseumResponse] 외의 타입은 처리하지 않고 다음 컨버터로 넘긴다(null 반환).
 */
internal class EmuseumXmlConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit,
    ): Converter<ResponseBody, *>? {
        if (type != EmuseumResponse::class.java) return null
        return Converter<ResponseBody, EmuseumResponse> { body ->
            body.byteStream().use(EmuseumXmlParser::parse)
        }
    }
}
