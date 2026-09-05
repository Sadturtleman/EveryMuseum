plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    api(project(":common:entity"))
    api(libs.kotlinx.coroutines.core)

    // RelicRepository 가 PagingData 를 계약에 담는다.
    api(libs.androidx.paging.common)
    implementation(libs.javax.inject)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
