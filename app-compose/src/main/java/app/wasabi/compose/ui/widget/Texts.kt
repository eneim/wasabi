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

package app.wasabi.compose.ui.widget

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.widget.TextView
import androidx.annotation.StyleRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import app.wasabi.compose.R

@SuppressLint("InflateParams")
@Composable
fun HtmlText(
  text: CharSequence,
  modifier: Modifier = Modifier,
  @StyleRes textAppearance: Int = com.google.android.material.R.style.TextAppearance_MaterialComponents_Body1,
) {
  AndroidView(
    factory = { context ->
      val result = LayoutInflater.from(context)
        .inflate(R.layout.simple_text_view, null) as TextView
      result.setTextAppearance(textAppearance)
      result
    },
    modifier = modifier
  ) { textView ->
    textView.text = text
  }
}
