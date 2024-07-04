import kotlin.random.Random

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.devtools)
    alias(libs.plugins.about.libraries)
    id("kotlin-parcelize")
    kotlin("plugin.serialization") version "1.9.23"
}

val (majorVersion, minorVersion, patchVersion, devVersion) = "${project.version}.0".replace("-dev","").split(".")
android {
    namespace = "app.revanced.manager"
    compileSdk = 34
    buildToolsVersion = "34.0.0"

    defaultConfig {
        applicationId = "app.revanced.manager"
        minSdk = 26
        targetSdk = 34
        versionName = project.version.toString()
        versionCode = (majorVersion.toInt() * 100000000) + (minorVersion.toInt() * 100000) + (patchVersion.toInt() * 100) + devVersion.toInt()
        resourceConfigurations.addAll(listOf(
            "en",
        ))
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            resValue("string", "app_name", "ReVanced Manager (dev)")

            buildConfigField("long", "BUILD_ID", "${Random.nextLong()}L")
        }

        release {
            if (System.getenv("signingKey") != null) {
                signingConfigs {
                    create("release") {
                        storeFile = file(System.getenv("signingKey"))
                        storePassword = System.getenv("keyStorePassword")
                        keyAlias = System.getenv("keyAlias")
                        keyPassword = System.getenv("keyPassword")
                    }
                }
                signingConfig = signingConfigs.getByName("release")
            } else {
                applicationIdSuffix = ".debug"
                resValue("string", "app_name", "ReVanced Manager Debug")
                signingConfig = signingConfigs.getByName("debug")
            }
            if (!project.hasProperty("noProguard")) {
                isMinifyEnabled = true
                isShrinkResources = true
                proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            }
            var suffix = "v${project.version}"
            if (project.hasProperty("suffix")) {
                suffix = "${project.property("suffix")}"
            }
            applicationVariants.all {
                this.outputs
                    .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
                    .forEach { output ->
                        output.outputFileName = "revanced-manager-${suffix}.apk"
                    }
            }

            buildConfigField("long", "BUILD_ID", "0L")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

    packaging {
        resources.excludes.addAll(listOf(
            "/prebuilt/**",
            "META-INF/DEPENDENCIES",
            "META-INF/**.version",
            "DebugProbesKt.bin",
            "kotlin-tooling-metadata.json",
            "org/bouncycastle/pqc/**.properties",
            "org/bouncycastle/x509/**.properties",
        ))
        jniLibs {
            useLegacyPackaging = true
        }
    }

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures.compose = true
    buildFeatures.aidl = true
    buildFeatures.buildConfig=true

    composeOptions.kotlinCompilerExtensionVersion = "1.5.10"
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
}

kotlin {
    jvmToolchain(17)
}

tasks.register("publish") {
    group = "Build"
    description = "Assemble main outputs for all the variants."
    dependsOn("assembleRelease")
}

dependencies {

    // AndroidX Core
    implementation(libs.androidx.ktx)
    implementation(libs.runtime.ktx)
    implementation(libs.runtime.compose)
    implementation(libs.splash.screen)
    implementation(libs.compose.activity)
    implementation(libs.paging.common.ktx)
    implementation(libs.work.runtime.ktx)
    implementation(libs.preferences.datastore)

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.preview)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.livedata)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.material3)

    // Accompanist
    implementation(libs.accompanist.drawablepainter)

    // Placeholder
    implementation(libs.placeholder.material3)

    // HTML Scraper
    implementation(libs.skrapeit.dsl)
    implementation(libs.skrapeit.parser)

    // Coil (async image loading, network image)
    implementation(libs.coil.compose)
    implementation(libs.coil.appiconloader)

    // KotlinX
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.collection.immutable)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    annotationProcessor(libs.room.compiler)
    ksp(libs.room.compiler)

    // ReVanced
    implementation(libs.revanced.patcher)
    implementation(libs.revanced.library)

    // Native processes
    implementation(libs.kotlin.process)

    // HiddenAPI
    compileOnly(libs.hidden.api.stub)

    // LibSU
    implementation(libs.libsu.core)
    implementation(libs.libsu.service)
    implementation(libs.libsu.nio)

    // Koin
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.koin.workmanager)

    // Compose Navigation
    implementation(libs.reimagined.navigation)

    // Licenses
    implementation(libs.about.libraries)

    // Ktor
    implementation(libs.ktor.core)
    implementation(libs.ktor.logging)
    implementation(libs.ktor.okhttp)
    implementation(libs.ktor.content.negotiation)
    implementation(libs.ktor.serialization)

    // Markdown
    implementation(libs.markdown.renderer)

    // Fading Edges
    implementation(libs.fading.edges)

    // Scrollbars
    implementation(libs.scrollbars)

    // Reorderable lists
    implementation(libs.reorderable)

    // Compose Icons
    implementation(libs.compose.icons.fontawesome)
}
