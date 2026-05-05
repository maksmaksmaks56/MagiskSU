import org.gradle.kotlin.dsl.implementation
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "su.makskok.magisksu"
    compileSdk {
        version = release(36)
    }

    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
            isUniversalApk = true
        }
    }
    signingConfigs {
        create("release") {
            storeFile = file("key.jks")
            storePassword = "10oip6"
            keyAlias = "makskok"
            keyPassword = "10oip6"
        }
    }


    buildTypes {
        getByName("debug") {
            isMinifyEnabled = true
            isShrinkResources = true

            applicationIdSuffix = ".debug"

            isDebuggable = true
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true

            signingConfig = signingConfigs.getByName("release")

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }




    defaultConfig {
        applicationId = "su.makskok.magisksu"
        minSdk = 26
        //noinspection OldTargetApi
        targetSdk = 36
        versionCode = 201
        versionName = "2.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    buildToolsVersion = "37.0.0"
    ndkVersion = "30.0.14904198 rc1"
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.text)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.monitor)
    implementation(libs.androidx.uiautomator.shell)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui-text:1.4.0")
    implementation("com.github.topjohnwu.libsu:core:6.0.0")

    // The core module that provides APIs to a shell
    implementation("com.github.topjohnwu.libsu:core:6.0.0")

    // Optional: APIs for creating root services. Depends on ":core"
    implementation("com.github.topjohnwu.libsu:service:6.0.0")

    // Optional: Provides remote file system support
    implementation("com.github.topjohnwu.libsu:nio:6.0.0")

}
