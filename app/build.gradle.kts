import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.sadturtleman.androidsampleproject"
    compileSdk {
        version = release(libs.versions.compileSdk.get().toInt())
    }

    defaultConfig {
        applicationId = "com.sadturtleman.androidsampleproject"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        freeCompilerArgs.add("-Xexplicit-backing-fields")
    }
}

dependencies {
    implementation(project(":common:presentation"))
    implementation(project(":common:domain"))
    implementation(project(":common:data"))
    implementation(project(":common:network"))
    implementation(project(":common:entity"))
    implementation(project(":common:di"))
    implementation(project(":common:navigation"))

    implementation(project(":home:presentation"))
    implementation(project(":home:navigation"))
    implementation(project(":home:domain"))
    implementation(project(":home:data"))
    implementation(project(":home:entity"))

    implementation(project(":search:presentation"))
    implementation(project(":search:navigation"))
    implementation(project(":search:domain"))
    implementation(project(":search:data"))
    implementation(project(":search:entity"))

    implementation(project(":detail:presentation"))
    implementation(project(":detail:navigation"))
    implementation(project(":detail:domain"))
    implementation(project(":detail:data"))
    implementation(project(":detail:entity"))

    implementation(project(":store:presentation"))
    implementation(project(":store:navigation"))
    implementation(project(":store:domain"))
    implementation(project(":store:data"))
    implementation(project(":store:entity"))

    implementation(libs.androidx.activity.compose)

    // Navigation 3. 백스택 · 라우팅 테이블이 이 모듈에 있다.
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    // GenericNavKey 백스택 직렬화용
    implementation(libs.kotlinx.serialization.json)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
