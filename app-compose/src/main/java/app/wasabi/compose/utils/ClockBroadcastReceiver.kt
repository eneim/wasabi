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

package app.wasabi.compose.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * A [BroadcastReceiver] to use with [Intent.ACTION_TIME_TICK] to update [currentTimeMillis] on
 * each [onReceive] callback.
 */
class ClockBroadcastReceiver : BroadcastReceiver() {

  var currentTimeMillis: Long by mutableStateOf(System.currentTimeMillis())
    private set

  override fun onReceive(
    context: Context?,
    intent: Intent?
  ) {
    currentTimeMillis = System.currentTimeMillis()
  }
}
