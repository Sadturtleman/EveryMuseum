package com.sadturtleman.androidsampleproject.common.network

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 실제 응답에서 잘라낸 XML 로 파서를 검증한다.
 * DOM(`javax.xml`)을 쓰므로 안드로이드 없이 JVM 테스트로 돈다.
 */
class EmuseumXmlParserTest {

    @Test
    fun `목록 응답의 헤더와 key value 행을 읽는다`() {
        val response = EmuseumXmlParser.parse(LIST_XML.byteInputStream())

        assertTrue(response.isSuccess)
        assertEquals(2917362, response.totalCount)
        assertEquals(1, response.pageNo)
        assertEquals(2, response.numOfRows)

        val rows = response.rows()
        assertEquals(2, rows.size)
        assertEquals("PS0100100100100000100000", rows[0]["id"])
        assertEquals("三穴砲", rows[0]["nameKr"])
        assertEquals("국립중앙박물관", rows[0]["museumName2"])
        // 값이 빈 item 은 담지 않는다.
        assertFalse(rows[1].containsKey("nameEn"))
    }

    @Test
    fun `상세 응답은 본문 이미지 연관을 각각 담고 중첩 totalCount 에 속지 않는다`() {
        val response = EmuseumXmlParser.parse(DETAIL_XML.byteInputStream())

        // imageList 안에도 totalCount 가 있지만 루트의 직속 자식만 읽어야 한다.
        assertEquals(1, response.totalCount)
        assertEquals("포(砲)", response.rows().single()["nameKr"])

        val images = response.rows(EmuseumResponse.SECTION_IMAGE_LIST)
        assertEquals(1, images.size)
        assertEquals("1", images.single()["imgOrder"])

        val relations = response.rows(EmuseumResponse.SECTION_RELATION_LIST)
        assertEquals(1, relations.size)
        assertEquals("PS0100100100100000100000", relations.single()["id"])
    }

    @Test
    fun `게이트웨이 오류 XML 은 실패 응답으로 바뀐다`() {
        val response = EmuseumXmlParser.parse(ERROR_XML.byteInputStream())

        assertFalse(response.isSuccess)
        assertEquals("12", response.resultCode)
        assertEquals("해당 오픈API 서비스가 없거나 폐기됨", response.resultMsg)
        assertTrue(response.rows().isEmpty())
    }

    private companion object {
        val LIST_XML = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <result>
              <numOfRows>2</numOfRows><pageNo>1</pageNo>
              <totalCount>2917362</totalCount>
              <resultCode>0000</resultCode><resultMsg>정상적으로 처리되었습니다.</resultMsg>
              <list>
                <data>
                  <item key="id" value="PS0100100100100000100000"/>
                  <item key="nameKr" value="三穴砲"/>
                  <item key="museumName2" value="국립중앙박물관"/>
                  <item key="museumName3" value="본관"/>
                </data>
                <data>
                  <item key="id" value="PS0100100100100000300000"/>
                  <item key="nameKr" value="포(砲)"/>
                  <item key="nameEn" value=" "/>
                </data>
              </list>
            </result>
        """.trimIndent()

        val DETAIL_XML = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <result>
              <totalCount>1</totalCount>
              <resultCode>0000</resultCode><resultMsg>정상적으로 처리되었습니다.</resultMsg>
              <list>
                <data>
                  <item key="id" value="PS0100100100100000300000"/>
                  <item key="nameKr" value="포(砲)"/>
                  <item key="nationalityName2" value="조선"/>
                  <item key="sizeInfo" value="길이 134.0cm"/>
                </data>
              </list>
              <imageList>
                <totalCount>1</totalCount>
                <list>
                  <data>
                    <item key="imgId" value="PS0100100100100000300000"/>
                    <item key="imgOrder" value="1"/>
                  </data>
                </list>
              </imageList>
              <relationList>
                <totalCount>1</totalCount>
                <list>
                  <data>
                    <item key="id" value="PS0100100100100000100000"/>
                    <item key="nameKr" value="三穴砲"/>
                  </data>
                </list>
              </relationList>
            </result>
        """.trimIndent()

        val ERROR_XML = """
            <?xml version="1.0" encoding="UTF-8"?>
            <OpenAPI_ServiceResponse>
              <cmmMsgHeader>
                <errMsg>NO_OPENAPI_SERVICE_ERROR</errMsg>
                <returnAuthMsg>해당 오픈API 서비스가 없거나 폐기됨</returnAuthMsg>
                <returnReasonCode>12</returnReasonCode>
              </cmmMsgHeader>
            </OpenAPI_ServiceResponse>
        """.trimIndent()
    }
}
