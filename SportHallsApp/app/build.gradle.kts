plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.diploma.sporthallsapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.diploma.sporthallsapp"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Retrofit & Gson за работа със Spring Boot API
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    implementation(libs.logging.interceptor)
    implementation(libs.github.glide)
    annotationProcessor(libs.github.compiler)
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}