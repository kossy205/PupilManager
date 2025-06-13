import java.util.Properties
import java.io.FileInputStream

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id ("kotlin-kapt")
    id ("dagger.hilt.android.plugin")
}

android {
    namespace = "com.kosiso.pupilmanager"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.kosiso.pupilmanager"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        manifestPlaceholders["MAPS_API_KEY"] = localProperties.getProperty("MAPS_API_KEY") ?: ""
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {

        release {
            buildConfigField("String", "MAPS_API_KEY", "\"${localProperties.getProperty("MAPS_API_KEY") ?: ""}\"")
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro")
        }
        getByName("debug") {
            buildConfigField("String", "MAPS_API_KEY", "\"${localProperties.getProperty("MAPS_API_KEY") ?: ""}\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // compose navigation
    implementation(libs.androidx.navigation.compose)

    // compose constraint Layout
    implementation (libs.androidx.constraintlayout.compose)

    // ViewModel support in Compose
    implementation (libs.androidx.lifecycle.viewmodel.compose)

    // Kotlin Coroutines for ViewModel and StateFlow
    implementation (libs.kotlinx.coroutines.android)

    // Hilt dependencies
    implementation (libs.hilt.android)
    kapt (libs.hilt.compiler)

    // ViewModel with Hilt integration
    implementation (libs.androidx.lifecycle.viewmodel.compose.v260)

    // lifecycle service
    implementation(libs.androidx.lifecycle.service)

    implementation (libs.androidx.runtime.livedata)

    // Room
    implementation (libs.androidx.room.runtime)
    kapt (libs.androidx.room.compiler)

    implementation (libs.gson)

    // Coroutines support for Room
    implementation (libs.androidx.room.ktx)

    // Retrofit
    implementation (libs.retrofit)
    implementation (libs.converter.gson)


    // google maps
    implementation ("com.google.maps.android:maps-compose:6.2.0")
    implementation ("com.google.android.gms:play-services-maps:19.0.0")
}