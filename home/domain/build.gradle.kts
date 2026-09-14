plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":home:entity"))
    api(project(":common:domain"))

    // 조회 조건(페이지 크기)을 플래그가 정한다. 순수 코틀린이라 도메인에서 그대로 쓴다.
    implementation(project(":featureflag:domain"))
    implementation(libs.javax.inject)
}
