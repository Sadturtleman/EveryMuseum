import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

val localProperties: Properties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

/**
 * e뮤지엄 오픈API 인증키. 저장소에 올리지 않도록 local.properties 에서 읽는다.
 * (키가 없으면 빈 값으로 빌드되고, 호출 시 포털이 인증 오류 XML 을 돌려준다)
 */
val emuseumServiceKey: String = localProperties
    .getProperty("EMUSEUM_SERVICE_KEY").orEmpty().trim()

/**
 * 오픈API 주소. 인증키와 같이 소스에 두지 않고 local.properties 에서만 받는다.
 * 끝의 '/' 가 빠지면 Retrofit 이 예외를 던지므로 형식을 지켜 적어야 한다.
 */
val emuseumBaseUrl: String = localProperties
    .getProperty("EMUSEUM_BASE_URL").orEmpty().trim()

android {
    namespace = "com.sadturtleman.androidsampleproject.common.network"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        buildConfigField("String", "EMUSEUM_SERVICE_KEY", "\"$emuseumServiceKey\"")
        buildConfigField("String", "EMUSEUM_BASE_URL", "\"$emuseumBaseUrl\"")
    }

    buildFeatures {
        buildConfig = true
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
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    // 통신 스택을 쓰는 data 모듈들이 Retrofit 타입을 그대로 받도록 api 로 노출한다.
    api(libs.retrofit)
    api(libs.okhttp)
    implementation(libs.okhttp.logging)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
}
