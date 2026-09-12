package com.sadturtleman.androidsampleproject.logging.domain

/**
 * 남길 수 있는 이벤트 전부. 이름과 함께 그 이벤트에 필요한 값도 여기에 담는다.
 *
 * 값을 [BizLogger.record] 의 맵 인자로 받지 않고 이벤트가 들고 있게 한 이유는,
 * 맵으로는 무엇이 필요한지 아무도 강제하지 못하기 때문이다 — 키를 빠뜨려도, 오타를 내도,
 * 엉뚱한 타입을 넣어도 컴파일은 그대로 되고 지표가 빈 뒤에야 드러난다.
 * 생성자로 받으면 빠뜨린 자리에서 컴파일이 멈추고, 어떤 값이 필요한지도 이 파일만 보면 된다.
 *
 * 이름을 부르는 자리에서 짓지 않고 모아 두는 이유도 같다 —
 * 이름은 수집된 뒤 지표의 키가 되므로 오타 하나가 그 이벤트를 통째로 사라지게 만들고,
 * 한 곳에 모여 있어야 같은 행동에 두 이름이 붙는 것을 리뷰에서 바로 본다.
 *
 * sealed 라 이 파일에 없는 이벤트는 아예 남길 수 없다. 새 이벤트는 여기에 한 덩어리를 더한다.
 *
 * [name] 은 수집 쪽과 맞춘 값이라 한번 정하면 바꾸지 않는다 —
 * 바꾸면 그 시점을 기준으로 지표가 둘로 갈린다. 코드에서 부르는 이름만 바꾼다.
 */
sealed interface BizEvent {

    /** 수집 쪽에서 이 이벤트를 가리키는 이름. */
    val name: String

    /**
     * 이 이벤트가 함께 나르는 값.
     *
     * 게으르게 계산하지 않고 만들 때 채운다 — 전송은 IO 로 넘어가 나중에 도는데,
     * 그때 값을 읽으면 그 사이 부른 쪽이 바꾼 것이 따라 나간다.
     */
    val parameters: Map<String, Any?>

    /**
     * 화면에 들어왔다.
     *
     * @param from 직전 화면. 앱을 열자마자 들어온 첫 화면이면 null 이다.
     */
    data class ViewEnter(val from: String?) : BizEvent {
        override val name = "view_enter"
        override val parameters: Map<String, Any?> = mapOf("from" to from)
    }

    /** 소장품 하나를 열었다. */
    data class RelicOpen(val relicId: String) : BizEvent {
        override val name = "relic_open"
        override val parameters: Map<String, Any?> = mapOf("relicId" to relicId)
    }

    /**
     * 보관함에 담거나 뺐다.
     *
     * @param saved 토글 뒤의 상태. 담은 것과 뺀 것을 한 이벤트로 두고 이 값으로 가른다 —
     *   두 이름으로 나누면 "담았다가 바로 뺀" 흐름을 이어 보기 어렵다.
     */
    data class SaveToggle(val relicId: String, val saved: Boolean) : BizEvent {
        override val name = "save_toggle"
        override val parameters: Map<String, Any?> = mapOf("relicId" to relicId, "saved" to saved)
    }

    /** 검색어를 넣고 실행했다. */
    data class SearchSubmit(val query: String) : BizEvent {
        override val name = "search_submit"
        override val parameters: Map<String, Any?> = mapOf("query" to query)
    }

    /** 최근 검색어를 통째로 지웠다. 함께 남길 값이 없다. */
    data object SearchRecentClear : BizEvent {
        override val name = "search_recent_clear"
        override val parameters: Map<String, Any?> = emptyMap()
    }

    /**
     * 검색 결과에 필터를 걸었다.
     *
     * @param tabCode 어느 갈래로 들어와 건 필터인가. 검색 결과에서 시트를 직접 편 경우는 null 이다.
     * @param optionCodes 고른 옵션 전부. 하나씩이 아니라 한 벌로 남겨야 어떤 조합이 쓰이는지 보인다.
     */
    data class FilterApply(val tabCode: String?, val optionCodes: List<String>) : BizEvent {
        override val name = "filter_apply"
        override val parameters: Map<String, Any?> = mapOf(
            "tabCode" to tabCode,
            // 부른 쪽이 들고 있는 리스트가 나중에 바뀌어도 나가는 값은 그대로여야 한다.
            "optionCodes" to optionCodes.toList(),
        )
    }

    /**
     * 홈에서 시대 칩을 골랐다.
     *
     * @param eraCode 고른 시대. 전체 보기면 null 이다.
     */
    data class EraSelect(val eraCode: String?) : BizEvent {
        override val name = "era_select"
        override val parameters: Map<String, Any?> = mapOf("eraCode" to eraCode)
    }

    /**
     * 보관함의 보기 방식을 바꿨다.
     *
     * @param layout 바꾼 뒤의 보기. 화면이 쓰는 타입을 도메인이 알 수 없으므로 이름을 넘긴다.
     */
    data class LayoutToggle(val layout: String) : BizEvent {
        override val name = "layout_toggle"
        override val parameters: Map<String, Any?> = mapOf("layout" to layout)
    }
}
