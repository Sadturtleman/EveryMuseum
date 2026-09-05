package com.sadturtleman.androidsampleproject.detail.domain

import com.sadturtleman.androidsampleproject.common.domain.navigation.NavRoute
import com.sadturtleman.androidsampleproject.common.domain.navigation.Page

/**
 * typed argument 를 가지는 페이지의 표준 형태.
 *
 * - 호출부(다른 feature)는 `navigationHelper.navigateTo(DetailPage.Args(id))` 로 타입 안전하게 이동한다.
 *   이때 참조하는 것은 :detail:domain 뿐이라 화면 구현과의 컴파일 의존이 생기지 않는다.
 * - 호스트(:app 의 AppRouteRegistry)는 [Args.from] 으로 String 맵을 다시 typed Args 로 복원한다.
 * - deep-link 의 query parameter 도 같은 String 맵으로 들어오므로 두 경로가 하나로 합쳐진다.
 *
 * 인자는 조회 키인 [Args.id] 하나로 유지한다. 표시에 필요한 나머지 값은 화면이 id 로 다시 조회하며,
 * 그래야 백스택 복원/딥링크로 들어온 경우에도 같은 데이터를 보게 된다.
 */
object DetailPage {
    const val PATH = "/detail"

    private const val KEY_ID = "id"

    data class Args(
        val id: String = "",
    ) : Page {
        override fun toRoute(): NavRoute = NavRoute(PATH, mapOf(KEY_ID to id))

        companion object {
            /** NavRoute.args 로부터 typed Args 복원. 값이 없으면 빈 id 로 두고 화면이 빈 상태를 처리한다. */
            fun from(args: Map<String, String>): Args = Args(id = args[KEY_ID].orEmpty())
        }
    }
}
