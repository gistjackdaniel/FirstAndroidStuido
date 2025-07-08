plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.DaejeonPass"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.DaejeongPass"
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
    implementation(libs.androidx.material3)// build.gradle.kts (Module :app)
    // ... 다른 기본 라이브러리들 ...

    // Material Design 3 컴포넌트 (탭, 아이콘 등 UI 요소 제공)
    implementation("androidx.compose.material3:material3:1.3.0")

    // Jetpack Compose Navigation (화면 간 이동 관리)
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Material 아이콘 사용을 위한 라이브러리 (선택 사항이지만, 탭 아이콘에 유용)
    implementation("androidx.compose.material:material-icons-core:1.7.0")
    implementation("androidx.compose.material:material-icons-extended:1.7.0")
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.appcompat)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("androidx.datastore:datastore-preferences:1.1.7")
    implementation("androidx.datastore:datastore:1.1.7")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("io.coil-kt:coil-compose:2.6.0")

    implementation("com.google.protobuf:protobuf-javalite:3.18.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.1")
}