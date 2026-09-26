plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose)
  // À retirer si Room n'est plus utilisé :
  alias(libs.plugins.google.devtools.ksp)
}

android {
  namespace = "com.example"
  compileSdk = 35

  defaultConfig {
    applicationId = "com.example.ecobudget"
    minSdk = 24
    targetSdk = 35
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  signingConfigs {
    create("release") {
      val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/my-upload-key.jks"
      storeFile = file(keystorePath)
      storePassword = System.getenv("STORE_PASSWORD")
      keyAlias = "upload"
      keyPassword = System.getenv("KEY_PASSWORD")
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("release")
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions {
    jvmTarget = "17"
  }

  buildFeatures {
    compose = true
    buildConfig = true
  }

  testOptions {
    unitTests {
      isIncludeAndroidResources = true
    }
  }

  dependenciesInfo {
    includeInApk = false
    includeInBundle = true
  }
}

dependencies {
  // Compose BOM & UI
  // Socle commun Kotlin Multiplatform (modèle, données, ViewModel, ressources)
  implementation(project(":shared"))

  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)

  // Android Core & Lifecycle
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)

  // Coroutines
  // kotlinx-coroutines-core est désormais fourni par le module :shared (dépendance api)
  implementation(libs.kotlinx.coroutines.android)

  // Base locale (à retirer si vous n'utilisez pas Room)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  "ksp"(libs.androidx.room.compiler)

  // Tests unitaires
  testImplementation(libs.junit)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)

  // Tests instrumentés & Debug
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.tooling)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
}