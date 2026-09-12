package com.sadturtleman.androidsampleproject

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import com.sadturtleman.androidsampleproject.common.presentation.ui.image.museumImageLoader
import com.sadturtleman.androidsampleproject.logging.domain.BizLogger
import com.sadturtleman.androidsampleproject.tti.domain.TtiRecorder
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class AndroidSampleApplication :
    Application(),
    SingletonImageLoader.Factory,
    DefaultLifecycleObserver {

    /** TTI 기록기. 앱과 수명을 같이하므로 여는 것도 닫는 것도 여기서 한다. */
    @Inject
    lateinit var ttiRecorder: TtiRecorder

    /** 비즈니스 이벤트 기록기. 이것도 앱과 수명을 같이한다. */
    @Inject
    lateinit var bizLogger: BizLogger

    override fun onCreate() {
        super<Application>.onCreate()
        // 지난 실행이 쏘지 못하고 죽은 기록도 이 시점에 함께 나간다.
        ttiRecorder.init()
        // 이 실행의 사용자 UUID 도 여기서 만들어진다. 로그인 지점에서 makeUUID 로 갈아 끼운다.
        bizLogger.init()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    /** 앱이 다시 앞으로 나왔다. [onStop] 에서 접어 둔 기록기를 다시 연다. */
    override fun onStart(owner: LifecycleOwner) {
        ttiRecorder.init()
        bizLogger.init()
    }

    /**
     * 앱이 백그라운드로 내려갔다.
     *
     * 안드로이드는 프로세스 종료를 알려주지 않으므로 이것이 우리가 받는 마지막 신호다.
     * 여기서 남은 완성 기록을 모두 쏜다. 그마저 못 하고 죽으면 다음 [onCreate] 가 주워 간다.
     */
    override fun onStop(owner: LifecycleOwner) {
        ttiRecorder.destroy()
        // 아직 나가지 못한 이벤트가 끝나기를 기다린 뒤 접는다.
        bizLogger.destroy()
    }

    /** 소장품 사진 로더. 구성은 :common:presentation 이 정한다. */
    override fun newImageLoader(context: PlatformContext): ImageLoader =
        museumImageLoader(context)
}
