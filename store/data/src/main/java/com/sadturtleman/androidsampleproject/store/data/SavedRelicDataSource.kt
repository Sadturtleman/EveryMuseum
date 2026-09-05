package com.sadturtleman.androidsampleproject.store.data

import com.sadturtleman.androidsampleproject.store.data.local.SavedRelicDto
import com.sadturtleman.androidsampleproject.store.data.local.SavedRelicKVStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

/**
 * 보관함 로컬 데이터 소스. 저장 포맷(JSON 배열) 을 다루는 유일한 지점이다.
 */
internal class SavedRelicDataSource(
    private val storage: SavedRelicKVStorage,
    private val json: Json,
) {
    fun getSavedRelicsFlow(): Flow<List<SavedRelicDto>> = storage.observeRaw().map { it.decode() }

    /**
     * 있으면 빼고 없으면 넣는다. 넣었으면 true 를 돌려준다.
     *
     * "저장돼 있나" 를 밖에서 먼저 읽고 판단하면 그 사이에 다른 토글이 끼어든다.
     * 판단까지 저장 트랜잭션 안으로 들여와야 연타에도 결과가 어긋나지 않는다.
     */
    suspend fun toggle(item: SavedRelicDto): Boolean {
        var saved = false
        storage.update { raw ->
            val current = raw.decode()
            val exists = current.any { it.id == item.id }
            // 판단이 트랜잭션 안에서 났으므로 결과도 여기서 집어 올린다(밖에서 다시 읽으면 어긋난다).
            saved = !exists
            val updated = if (exists) current.filterNot { it.id == item.id } else current + item
            json.encodeToString(updated)
        }
        return saved
    }

    /**
     * 저장본을 읽는다. 포맷이 깨졌거나 스키마가 바뀌어 못 읽으면 빈 목록으로 시작한다.
     * (보관함이 안 열리는 것보다 낫다)
     */
    private fun String?.decode(): List<SavedRelicDto> {
        if (isNullOrBlank()) return emptyList()
        return runCatching { json.decodeFromString<List<SavedRelicDto>>(this) }
            .getOrDefault(emptyList())
    }
}
