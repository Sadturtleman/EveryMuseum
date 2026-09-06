package com.sadturtleman.androidsampleproject.common.network

import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.InputStream
import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilderFactory

/**
 * e뮤지엄 응답 XML 파서.
 *
 * 응답이 XML 전용이고(`_type=json` 을 넣어도 XML 로 온다) 본문이 key/value 쌍이라
 * 직렬화 라이브러리로 매핑할 이득이 없어 DOM 으로 직접 읽는다.
 * `javax.xml` 은 안드로이드와 JVM 양쪽에 있으므로 파서 단위 테스트가 그냥 돌아간다.
 *
 * 정상 응답
 * ```xml
 * <result>
 *   <totalCount>2917362</totalCount><resultCode>0000</resultCode>
 *   <list><data><item key="id" value="PS01..."/></data></list>
 * </result>
 * ```
 * 게이트웨이 오류 응답(인증키 문제 등)
 * ```xml
 * <OpenAPI_ServiceResponse>
 *   <cmmMsgHeader><errMsg>...</errMsg><returnAuthMsg>...</returnAuthMsg>
 *   <returnReasonCode>12</returnReasonCode></cmmMsgHeader>
 * </OpenAPI_ServiceResponse>
 * ```
 */
internal object EmuseumXmlParser {

    fun parse(stream: InputStream): EmuseumResponse {
        val document = newDocumentBuilderFactory()
            .newDocumentBuilder()
            .parse(stream)
        document.documentElement.normalize()

        val root = document.documentElement ?: error("빈 응답입니다.")
        return if (root.tagName == TAG_ERROR_ROOT) root.toErrorResponse() else root.toResponse()
    }

    /**
     * 외부 엔티티를 쓰지 않는 파서 (XXE 차단).
     *
     * 엔티티 확장을 끄는 것이 핵심이고 그건 어느 구현에나 있는 설정이다.
     * 반면 feature 는 구현마다 있고 없고가 달라 — 안드로이드 기본 파서는
     * `disallow-doctype-decl` 을 모르고 [javax.xml.parsers.ParserConfigurationException] 을 던진다 —
     * 있으면 켜고 없으면 넘어간다.
     *
     * `setXIncludeAware` 도 부르지 않는다. 안드로이드 구현은 이 설정을 재정의하지 않아
     * UnsupportedOperationException 을 던지고, 어차피 기본값이 false 다.
     * (JVM 단위 테스트만 돌리면 이런 차이가 드러나지 않는다)
     */
    private fun newDocumentBuilderFactory(): DocumentBuilderFactory =
        DocumentBuilderFactory.newInstance().apply {
            isExpandEntityReferences = false
            ENABLED_FEATURES.forEach { feature -> runCatching { setFeature(feature, true) } }
            DISABLED_FEATURES.forEach { feature -> runCatching { setFeature(feature, false) } }
        }

    private fun Element.toResponse(): EmuseumResponse {
        val sections = childElements()
            .filter { it.tagName in SECTION_TAGS }
            // imageList · relationList 는 안에 <list> 를 한 겹 더 두므로 자손의 <data> 를 모두 모은다.
            .associate { section -> section.tagName to section.dataRows() }

        return EmuseumResponse(
            // 직속 자식만 읽는다. imageList 안에도 <totalCount> 가 있어 전체 검색은 값이 섞인다.
            resultCode = directChildText(TAG_RESULT_CODE).orEmpty(),
            resultMsg = directChildText(TAG_RESULT_MSG).orEmpty(),
            totalCount = directChildText(TAG_TOTAL_COUNT)?.toIntOrNull() ?: 0,
            pageNo = directChildText(TAG_PAGE_NO)?.toIntOrNull() ?: 1,
            numOfRows = directChildText(TAG_NUM_OF_ROWS)?.toIntOrNull() ?: 0,
            sections = sections,
        )
    }

    /** 게이트웨이 오류는 resultCode 자리에 returnReasonCode 를 채워 같은 모양으로 돌려준다. */
    private fun Element.toErrorResponse(): EmuseumResponse {
        val header = childElements().firstOrNull { it.tagName == TAG_ERROR_HEADER }
        return EmuseumResponse(
            resultCode = header?.directChildText(TAG_ERROR_CODE).orEmpty().ifBlank { UNKNOWN_CODE },
            resultMsg = header?.directChildText(TAG_ERROR_AUTH_MSG)
                ?: header?.directChildText(TAG_ERROR_MSG).orEmpty(),
            totalCount = 0,
            pageNo = 1,
            numOfRows = 0,
            sections = emptyMap(),
        )
    }

    /** `<data>` 하나를 key/value 맵 한 행으로 옮긴다. 값이 빈 item 은 버린다. */
    private fun Element.dataRows(): List<Map<String, String>> =
        getElementsByTagName(TAG_DATA).asElements().map { data ->
            data.getElementsByTagName(TAG_ITEM).asElements()
                .mapNotNull { item ->
                    val key = item.getAttribute(ATTR_KEY)
                    val value = item.getAttribute(ATTR_VALUE).trim()
                    if (key.isBlank() || value.isBlank()) null else key to value
                }
                .toMap()
        }

    private fun Element.childElements(): List<Element> =
        childNodes.asElements()

    private fun Element.directChildText(tag: String): String? =
        childElements().firstOrNull { it.tagName == tag }?.textContent?.trim()

    private fun org.w3c.dom.NodeList.asElements(): List<Element> =
        (0 until length).mapNotNull { index ->
            item(index).takeIf { it.nodeType == Node.ELEMENT_NODE } as? Element
        }

    private const val TAG_ERROR_ROOT = "OpenAPI_ServiceResponse"
    private const val TAG_ERROR_HEADER = "cmmMsgHeader"
    private const val TAG_ERROR_MSG = "errMsg"
    private const val TAG_ERROR_AUTH_MSG = "returnAuthMsg"
    private const val TAG_ERROR_CODE = "returnReasonCode"

    private const val TAG_RESULT_CODE = "resultCode"
    private const val TAG_RESULT_MSG = "resultMsg"
    private const val TAG_TOTAL_COUNT = "totalCount"
    private const val TAG_PAGE_NO = "pageNo"
    private const val TAG_NUM_OF_ROWS = "numOfRows"
    private const val TAG_DATA = "data"
    private const val TAG_ITEM = "item"

    private const val ATTR_KEY = "key"
    private const val ATTR_VALUE = "value"

    private const val UNKNOWN_CODE = "UNKNOWN"

    private val ENABLED_FEATURES = listOf(
        "http://apache.org/xml/features/disallow-doctype-decl",
        XMLConstants.FEATURE_SECURE_PROCESSING,
    )

    private val DISABLED_FEATURES = listOf(
        "http://xml.org/sax/features/external-general-entities",
        "http://xml.org/sax/features/external-parameter-entities",
        "http://apache.org/xml/features/nonvalidating/load-external-dtd",
    )

    private val SECTION_TAGS = setOf(
        EmuseumResponse.SECTION_LIST,
        EmuseumResponse.SECTION_IMAGE_LIST,
        EmuseumResponse.SECTION_RELATION_LIST,
    )
}
