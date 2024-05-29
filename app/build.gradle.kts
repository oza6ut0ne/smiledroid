plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.kapt)
    alias(libs.plugins.dagger.hilt.android)
}

android {
    namespace = "net.urainter.overlay"
    compileSdk = 34

    defaultConfig {
        applicationId = "net.urainter.overlay"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    implementation(libs.dagger.hilt.android)
    kapt(libs.dagger.hilt.android.compiler)

    implementation(libs.timber)

    debugImplementation(libs.leakcanary.android)
    debugImplementation(libs.hyperion.core)
    debugImplementation(libs.hyperion.attr)
    debugImplementation(libs.hyperion.build.config)
    debugImplementation(libs.hyperion.crash)
    debugImplementation(libs.hyperion.disk)
    debugImplementation(libs.hyperion.geiger.counter)
    debugImplementation(libs.hyperion.measurement)
    debugImplementation(libs.hyperion.phoenix)
    debugImplementation(libs.hyperion.recorder)
    debugImplementation(libs.hyperion.shared.preferences)
    debugImplementation(libs.hyperion.timber)
    debugImplementation(libs.flipper)
    debugImplementation(libs.flipper.leakcanary2.plugin)
    debugImplementation(libs.soloader)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

kapt {
    correctErrorTypes = true
}
