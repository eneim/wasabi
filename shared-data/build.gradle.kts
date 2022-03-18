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
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
  id("com.google.devtools.ksp") version ("1.6.10-1.0.4")
}

// Check the root build.gradle.kts for common configurations (compileSdk, etc).
android {
  namespace = "wasabi.data"

  defaultConfig {
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")

    javaCompileOptions {
      annotationProcessorOptions {
        arguments += mapOf(
          "room.schemaLocation" to "$projectDir/schemas",
          "room.incremental" to "true",
          "room.expandProjection" to "true"
        )
      }
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
}

dependencies {
  implementation(project(":base"))
  implementation(project(":service-common"))
  implementation(project(":service-hnews"))
  implementation(project(":service-qiita"))
  implementation(libs.androidx.core)

  implementation(libs.androidx.room.common)
  implementation(libs.androidx.room.runtime)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.migration)
  implementation(libs.androidx.room.paging)
  ksp(libs.androidx.room.compiler)

  implementation(libs.retrofit)
  implementation(libs.retrofit.converter.moshi)
  implementation(libs.okhttp.core)
  implementation(libs.okhttp.logging.interceptor)
  implementation(libs.moshi.core)
  implementation(libs.moshi.adapters)

  implementation(libs.square.logcat)

  testImplementation(libs.test.junit.core)
}
