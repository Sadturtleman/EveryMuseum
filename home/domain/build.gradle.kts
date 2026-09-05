plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":home:entity"))
    api(project(":common:domain"))
    implementation(libs.javax.inject)
}
