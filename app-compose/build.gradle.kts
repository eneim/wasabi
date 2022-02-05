/*
 * Copyright (c) 2022. Nam Nguyen, nam@ene.im
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
}

// Check the root build.gradle.kts for common configurations (compileSdk, etc).
android {
  defaultConfig {
    applicationId = "app.wasabi.compose"
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    vectorDrawables {
      useSupportLibrary = true
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
    }

    debug {
      applicationIdSuffix = ".dev"
    }
  }

  buildFeatures {
    viewBinding = true
    compose = true
  }

  composeOptions {
    kotlinCompilerExtensionVersion = libs.versions.jetpack.compose.compiler.get()
  }

  packagingOptions {
    resources.excludes.add("/META-INF/{AL2.0,LGPL2.1}")
  }
}

dependencies {
  implementation(project(":base"))
  implementation(project(":shared-data"))
  implementation(project(":service-common"))
  implementation(project(":service-hnews"))
  implementation(project(":service-qiita"))

  implementation(libs.androidx.core)
  implementation(libs.androidx.lifecycle.runtime)
  implementation(libs.androidx.datastore.preferences.core)

  implementation(libs.androidx.room.paging)

  implementation(libs.compose.ui)
  implementation(libs.compose.ui.preview)
  implementation(libs.compose.material)
  implementation(libs.compose.activity)
  implementation(libs.compose.viewmodel)
  implementation(libs.compose.paging)

  implementation(libs.accompanist.insets)
  implementation(libs.accompanist.systemuicontroller)
  implementation(libs.accompanist.pager)
  implementation(libs.accompanist.pager.indicators)
  implementation(libs.accompanist.webview)

  implementation(libs.coil)
  implementation(libs.coil.compose)

  implementation(libs.square.logcat)

  testImplementation(libs.test.junit.core)
  androidTestImplementation(libs.test.androidx.junit)
  androidTestImplementation(libs.test.androidx.espresso.core)
  androidTestImplementation(libs.test.compose.ui.junit)
  debugImplementation(libs.test.compose.ui.tool)
}
