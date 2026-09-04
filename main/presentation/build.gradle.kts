import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.sadturtleman.androidsampleproject.main.presentation"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(project(":main:domain"))
    implementation(project(":main:entity"))
    implementation(project(":common:presentation"))

    // 라우팅 테이블이 각 feature 의 화면을 직접 렌더한다.
    implementation(project(":search:presentation"))
    implementation(project(":detail:presentation"))
    implementation(project(":store:presentation"))

    // 각 feature 의 *Page (path / typed Args 정의) 를 호스트 측 라우터에서 참조한다.
    implementation(project(":search:domain"))
    implementation(project(":detail:domain"))
    implementation(project(":store:domain"))

    implementation(libs.androidx.activity.compose)

    // Navigation 3
    api(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    // GenericNavKey 백스택 직렬화용
    implementation(libs.kotlinx.serialization.json)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
