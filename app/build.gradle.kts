import com.android.build.gradle.internal.tasks.factory.dependsOn
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.util.Properties
import java.util.regex.Pattern

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.kapt)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.oss.licenses)
}

android {
    namespace = "net.urainter.overlay"
    compileSdk = 34

    defaultConfig {
        applicationId = "net.urainter.overlay"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "0.0.1a"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("config")
    }
    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            applicationIdSuffix = ".debug"
            versionNameSuffix = createDebugVersionNameSuffix()
        }
        release {
            isDebuggable = false
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("config")
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

    // Apply signingConfigs.
    val keystorePropFilePath = "../keystore.properties"
    file(keystorePropFilePath).let { propFile ->
        if (!propFile.canRead()){
            logger.log(LogLevel.ERROR, "$keystorePropFilePath not found")
            return@let
        }

        val props = Properties()
        props.load(FileInputStream(propFile))
        if (!props.containsKey("storeFile") || !props.containsKey("storePassword") ||
            !props.containsKey("keyAlias") || !props.containsKey("keyPassword")) {
            logger.log(LogLevel.ERROR, "$keystorePropFilePath found but some entries are missing")
            return@let
        }

        android.signingConfigs.getByName("config") {
            storeFile = file(props["storeFile"]!!)
            storePassword = props["storePassword"] as String
            keyAlias = props["keyAlias"] as String
            keyPassword = props["keyPassword"] as String
        }.let {
            android.buildTypes.forEach { type -> type.signingConfig = it }
        }
    }
}

fun createDebugVersionNameSuffix(): String {
    val commitHash = ByteArrayOutputStream().also {
        exec {
            isIgnoreExitValue = true
            commandLine("git", "rev-parse", "--short", "HEAD")
            standardOutput = it
        }.exitValue.let { v -> if (v != 0) return "-dev" }
    }.toString().trim()

    val dirtyIndicator = exec {
        isIgnoreExitValue = true
        commandLine("git", "diff", "--shortstat", "--exit-code", "--quiet")
    }.exitValue.let { if (it == 0) "" else " (dirty)" }

    return "-dev ($commitHash)$dirtyIndicator"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.preference.ktx)

    implementation(libs.dagger.hilt.android)
    kapt(libs.dagger.hilt.android.compiler)

    implementation(libs.play.services.oss.licenses)
    implementation(libs.timber)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.webkit)
    implementation(libs.paho.mqtt.android)

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

fun getCurrentBuildType(): Pair<String, String> {
    val taskRequestsStr = gradle.startParameter.taskRequests.toString()
    val pattern: Pattern = if (taskRequestsStr.contains("assemble")) {
        Pattern.compile("assemble(\\w*)(Release|Debug)")
    } else {
        Pattern.compile("bundle(\\w*)(Release|Debug)")
    }

    val matcher = pattern.matcher(taskRequestsStr)
    return if (matcher.find()) {
        matcher.group(1).lowercase() to matcher.group(2).lowercase()
    } else {
        "" to ""
    }
}

project.tasks.preBuild.dependsOn("buildWeb")

task("buildWeb") {
    val (_, buildType) = getCurrentBuildType()
    val npmCommand = when (buildType) {
        "debug" -> "bundle:dev"
        "release" -> "bundle"
        else -> return@task
    }
    doFirst {
        exec {
            workingDir = File(projectDir, "web")
            commandLine("npm", "run", npmCommand)
        }
    }
}
