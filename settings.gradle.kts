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
