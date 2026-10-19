plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

/**
 * The release signing key stays off GitHub: the key file lives outside the project, and its
 * password in the macOS Keychain (see "Signing" in the README). Both can be overridden with
 * Gradle properties, e.g. in ~/.gradle/gradle.properties on another machine.
 */
val releaseKeystore = file(
    providers.gradleProperty("cardHolderKeystore").orNull
        ?: "${System.getProperty("user.home")}/Keys/card-holder/card-holder-release.jks"
)
val releaseKeyAlias = providers.gradleProperty("cardHolderKeyAlias").orNull ?: "card-holder"
val releaseKeyPassword: String? by lazy {
    providers.gradleProperty("cardHolderKeyPassword").orNull
        ?: runCatching {
            providers.exec {
                commandLine("security", "find-generic-password", "-s", "card-holder-release", "-w")
                isIgnoreExitValue = true
            }.standardOutput.asText.get().trim().takeIf { it.isNotEmpty() }
        }.getOrNull()
}

android {
    namespace = "com.ahmadabdullah.cardholder"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.ahmadabdullah.cardholder"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Updating from GitHub Releases, for testers. Google Play doesn't allow it: set this to
        // false, and drop REQUEST_INSTALL_PACKAGES from the manifest, for a Play release.
        buildConfigField("boolean", "SELF_UPDATE", "true")
    }

    signingConfigs {
        if (releaseKeystore.exists()) {
            create("release") {
                storeFile = releaseKeystore
                storePassword = releaseKeyPassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
            // Without the key on this machine, release builds come out unsigned and can't be
            // installed; build debug instead, or set up the key as the README describes.
            signingConfig = signingConfigs.findByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        // java.time on Android 7 (API 24–25), which predates it
        isCoreLibraryDesugaringEnabled = true
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.work.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}