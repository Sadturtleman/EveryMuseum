package com.sadturtleman.androidsampleproject.common.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.sadturtleman.androidsampleproject.common.di.IoDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DataStoreModule {

    /** 보관함 저장 파일. 피처가 늘면 여기에 파일과 한정자를 한 쌍씩 더한다. */
    @Provides
    @Singleton
    @StorePreferences
    fun provideStoreDataStorage(
        @ApplicationContext context: Context,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): DataStorage = PreferencesDataStorage(
        createDataStore(context, ioDispatcher, name = "store_preferences"),
    )

    /**
     * 파일 하나에 대한 DataStore 를 만든다.
     *
     * 읽기 · 쓰기는 여기 넘긴 스코프에서 돌아간다. IO 디스패처를 주는 이유이고,
     * 앱이 사는 동안 유지돼야 하므로 자식 실패가 스코프를 죽이지 않게 SupervisorJob 을 쓴다.
     */
    private fun createDataStore(
        context: Context,
        ioDispatcher: CoroutineDispatcher,
        name: String,
    ): DataStore<Preferences> = PreferenceDataStoreFactory.create(
        scope = CoroutineScope(ioDispatcher + SupervisorJob()),
        produceFile = { context.preferencesDataStoreFile(name) },
    )
}
