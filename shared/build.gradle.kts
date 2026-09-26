import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

/*
 * Module partagé EcoBudget (Kotlin Multiplatform).
 *
 * Contient le socle commun consommé par l'application Android native (:app) et
 * exportable vers iOS sous forme de framework "Shared" :
 *  - model       : entités du domaine (Transaction, Category, YearMonth)
 *  - data        : contrat TransactionRepository + FakeTransactionRepository
 *  - viewmodel   : EcoBudgetUiState + EcoBudgetViewModel
 *  - resources   : catalogue de libellés (composeResources/values/strings.xml)
 */
plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.android.library)
  alias(libs.plugins.compose.multiplatform)
  alias(libs.plugins.kotlin.compose)
}

kotlin {
  // Cible Android : produit une bibliothèque AAR consommée par :app.
  androidTarget {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_17)
    }
  }

  // Cibles iOS : appareil réel (arm64) + simulateurs (Intel x64 et Apple Silicon arm64).
  listOf(
    iosX64(),
    iosArm64(),
    iosSimulatorArm64()
  ).forEach { iosTarget ->
    iosTarget.binaries.framework {
      baseName = "shared"
      isStatic = true
    }
  }

  sourceSets {
    commonMain.dependencies {
      // "api" : ces types apparaissent dans l'API publique du module (Flow, ViewModel,
      // StringResource/stringResource) et doivent donc être visibles depuis :app.
      implementation(compose.runtime)   // @Immutable + moteur requis par le compilateur Compose
      api(compose.components.resources)
      api(libs.kotlinx.coroutines.core)
      api(libs.jetbrains.lifecycle.viewmodel)

      // Remplace java.util.Calendar (construction de dates, fuseau horaire).
      implementation(libs.kotlinx.datetime)
    }

    androidMain.dependencies {
      // Fournit Dispatchers.Main (utilisé par viewModelScope) sur Android.
      implementation(libs.kotlinx.coroutines.android)
    }

    commonTest.dependencies {
      implementation(libs.kotlin.test)
      implementation(libs.kotlinx.coroutines.test)
    }
  }
}

android {
  namespace = "com.example.shared"
  compileSdk = 35

  defaultConfig {
    minSdk = 24
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
}

// Catalogue de ressources partagé : génère la classe Res publique
// (com.example.shared.resources.Res) accessible depuis le module :app.
compose.resources {
  publicResClass = true
  packageOfResClass = "com.example.shared.resources"
  generateResClass = always
}
