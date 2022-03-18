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

package app.wasabi.compose

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import app.wasabi.compose.navigation.WasabiApp
import app.wasabi.compose.utils.ClockBroadcastReceiver

class MainActivity : ComponentActivity() {

  private val clockBroadcastReceiver = ClockBroadcastReceiver()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    WindowCompat.setDecorFitsSystemWindows(
      /* window = */ window,
      /* decorFitsSystemWindows = */ false
    )

    setContent {
      val currentTimeMillis = clockBroadcastReceiver.currentTimeMillis
      WasabiApp(currentTimeMillis = currentTimeMillis)
    }
  }

  override fun onStart() {
    super.onStart()
    registerReceiver(clockBroadcastReceiver, IntentFilter(Intent.ACTION_TIME_TICK))
  }

  override fun onStop() {
    super.onStop()
    unregisterReceiver(clockBroadcastReceiver)
  }
}
