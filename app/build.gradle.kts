plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.parcelize)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.practicum.playlistmaker"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.practicum.playlistmaker"
        minSdk = 29
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
        viewBinding = true
        compose = true
    }
}

dependencies {
    implementation (libs.koin.androidx.compose)
    implementation("io.coil-kt.coil3:coil-compose:3.2.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.2.0")
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.material3.android)
    debugImplementation(libs.androidx.compose.uitool)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.activity)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.coroutines)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.glide)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.viewpager2)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.koin)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    implementation(libs.play.services.measurement.api)
    ksp(libs.androidx.room.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}