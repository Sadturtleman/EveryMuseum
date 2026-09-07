pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "AndroidSampleProject"
include(":app")

// 공통 레이어. 모든 feature 가 참조한다.
include(":common:presentation")
include(":common:domain")
include(":common:data")
include(":common:network")
include(":common:entity")
include(":common:datastore")
include(":common:di")
include(":common:navigation")

// 화면 진입 시간(TTI) 계측. 어느 feature 에도 속하지 않고 앱 전체를 가로지른다.
// domain 은 순수 코틀린이다 — 무엇을 언제 재는지는 플랫폼과 무관해야 하므로
// 시각(TtiClock) 과 저장(TtiRecordStore) 을 포트로 두고 data 가 안드로이드 구현을 꽂는다.
include(":tti:domain")
include(":tti:data")
include(":tti:presentation")

// 네비게이션 호스트. 각 feature 의 화면을 라우팅 테이블 한 곳에 모은다.
include(":home:navigation")
include(":home:presentation")
include(":home:domain")
include(":home:data")
include(":home:entity")

include(":search:navigation")
include(":search:presentation")
include(":search:domain")
include(":search:data")
include(":search:entity")

include(":detail:navigation")
include(":detail:presentation")
include(":detail:domain")
include(":detail:data")
include(":detail:entity")

include(":store:navigation")
include(":store:presentation")
include(":store:domain")
include(":store:data")
include(":store:entity")
