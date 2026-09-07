package com.sadturtleman.androidsampleproject

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import com.sadturtleman.androidsampleproject.common.domain.helper.MessageHelper
import com.sadturtleman.androidsampleproject.common.navigation.NavigationHelper
import com.sadturtleman.androidsampleproject.common.presentation.helper.LocalMessageHelper
import com.sadturtleman.androidsampleproject.common.presentation.helper.LocalNavigationHelper
import com.sadturtleman.androidsampleproject.tti.domain.TtiRecorder
import com.sadturtleman.androidsampleproject.tti.presentation.LocalTtiRecorder
import com.sadturtleman.androidsampleproject.deeplink.resolveNewIntentRoute
import com.sadturtleman.androidsampleproject.deeplink.resolveStartStack
import com.sadturtleman.androidsampleproject.navigation.RootComposable
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /**
     * @Singleton 으로 제공되는 단일 인스턴스. Composable 트리에는 [LocalNavigationHelper] 로,
     * ViewModel / UseCase 에는 생성자 주입으로 같은 인스턴스가 전달된다.
     */
    @Inject
    lateinit var navigationHelper: NavigationHelper

    /** 메시지도 같은 단일 인스턴스를 Composable 트리와 ViewModel 양쪽이 공유한다. */
    @Inject
    lateinit var messageHelper: MessageHelper

    /** TTI 기록기. 화면 계측은 전부 컴포지션에서 일어나므로 트리에 꽂아 준다. */
    @Inject
    lateinit var ttiRecorder: TtiRecorder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // deep-link 진입 시 Intent.data 에서 시작 백스택을 구성한다.
        val startStack = resolveStartStack(intent?.data)

        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(
                LocalNavigationHelper provides navigationHelper,
                LocalMessageHelper provides messageHelper,
                LocalTtiRecorder provides ttiRecorder,
            ) {
                RootComposable(startStack = startStack)
            }
        }
    }

    /**
     * launchMode 가 singleTop 이므로, 앱 실행 중 들어오는 새 deep-link 는 여기로 온다.
     * URI 를 NavRoute 로 변환만 하고 실제 dispatch 는 NavigationHelper 통합 플로우가 담당한다.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        val uri = intent.data ?: return
        val route = resolveNewIntentRoute(uri) ?: return
        navigationHelper.navigateByRoute(route)
    }
}
