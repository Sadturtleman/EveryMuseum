plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":main:entity"))
    implementation(project(":common:domain"))
    implementation(libs.javax.inject)
}
