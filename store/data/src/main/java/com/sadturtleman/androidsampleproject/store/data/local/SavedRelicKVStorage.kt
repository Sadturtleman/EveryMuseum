package com.sadturtleman.androidsampleproject.store.data.local

import com.sadturtleman.androidsampleproject.common.datastore.DataStorage
import com.sadturtleman.androidsampleproject.common.datastore.PreferenceKey
import kotlinx.coroutines.flow.Flow

/**
 * 보관함 저장소. 저장 목록 전체를 [KEY_SAVED_RELICS] 한 키의 JSON 배열로 둔다.
 *
 * 카드마다 북마크를 연달아 누르면 read-modify-write 가 겹쳐 저장이 유실될 수 있다.
 * 그건 [DataStorage.update] 가 읽기와 쓰기를 한 트랜잭션으로 묶어 막는다.
 *
 * 읽기는 원문 문자열만 흘려보내고 역직렬화는 [SavedRelicDataSource] 가 한다.
 */
internal class SavedRelicKVStorage(
    private val dataStorage: DataStorage,
) {
    /** 현재 값 + 이후 변경. */
    fun observeRaw(): Flow<String?> = dataStorage.observe(KEY_SAVED_RELICS)

    /** 현재 값을 읽어 [transform] 결과로 바꾼다. 사이에 다른 토글이 끼어들지 못한다. */
    suspend fun update(transform: (String?) -> String) =
        dataStorage.update(KEY_SAVED_RELICS, transform)

    private companion object {
        val KEY_SAVED_RELICS = PreferenceKey.StringKey("saved_relics")
    }
}
