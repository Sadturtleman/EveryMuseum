package com.sadturtleman.androidsampleproject.common.data.relic

import com.sadturtleman.androidsampleproject.common.entity.relic.RelicQuery
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class RelicMapperTest {

    @Test
    fun `비어 있는 조건은 쿼리에서 빠진다`() {
        val query = RelicQuery(pageNo = 2, numOfRows = 20, name = "  ", nationalityCode = "PS06001018")

        val params = query.toQueryMap()

        assertEquals("2", params["pageNo"])
        assertEquals("20", params["numOfRows"])
        assertEquals("PS06001018", params["nationalityCode"])
        // 빈 값을 넘기면 서버가 조건이 걸린 것으로 보고 0건을 돌려준다.
        assertFalse(params.containsKey("name"))
        assertFalse(params.containsKey("materialCode"))
    }

    @Test
    fun `검색어는 앞뒤 공백을 떼고 넘긴다`() {
        val params = RelicQuery(name = " 백자 ").toQueryMap()

        assertEquals("백자", params["name"])
    }

    @Test
    fun `소장기관 표시는 본관 구분까지 붙인다`() {
        val relic = mapOf(
            "id" to "PS01",
            "museumName2" to "국립중앙박물관",
            "museumName3" to "본관",
        ).toRelicVO()

        assertEquals("국립중앙박물관 · 본관", relic.museumLabel)
    }
}
