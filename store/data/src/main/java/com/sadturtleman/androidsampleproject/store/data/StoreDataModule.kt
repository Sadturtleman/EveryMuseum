package com.sadturtleman.androidsampleproject.store.data

import com.sadturtleman.androidsampleproject.common.datastore.DataStorage
import com.sadturtleman.androidsampleproject.common.datastore.StorePreferences
import com.sadturtleman.androidsampleproject.common.domain.saved.SavedRelicRepository
import com.sadturtleman.androidsampleproject.store.data.local.SavedRelicKVStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object StoreDataModule {

    /**
     * 저장본 호환용 설정.
     * - ignoreUnknownKeys: 필드가 빠진 새 버전이 옛 저장본을 읽을 수 있게
     * - encodeDefaults: 기본값도 적어 두어 나중에 기본값이 바뀌어도 저장 당시 값이 남게
     */
    private val storageJson: Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    @Provides
    @Singleton
    fun provideSavedRelicKVStorage(
        @StorePreferences dataStorage: DataStorage,
    ): SavedRelicKVStorage = SavedRelicKVStorage(dataStorage)

    @Provides
    @Singleton
    fun provideSavedRelicDataSource(storage: SavedRelicKVStorage): SavedRelicDataSource =
        SavedRelicDataSource(storage, storageJson)

    @Provides
    @Singleton
    fun provideSavedRelicRepository(dataSource: SavedRelicDataSource): SavedRelicRepository =
        SavedRelicRepositoryImpl(dataSource)
}
