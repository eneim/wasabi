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

package app.wasabi.compose.screens.post

import android.content.res.Resources
import android.text.format.DateUtils
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.text.parseAsHtml
import app.wasabi.compose.R
import app.wasabi.compose.R.style
import app.wasabi.compose.ui.widget.HtmlText
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import wasabi.data.model.Post

@Composable
fun PostCell(
  post: Post?,
  currentTimeMillis: Long,
  modifier: Modifier = Modifier,
  onClick: (Post) -> Unit = { /* no-op*/ },
) {
  val resources = LocalContext.current.resources
  val keyLine = 8.dp

  val (postInfo, htmlTitle, _) = remember(post, currentTimeMillis) {
    if (post == null) {
      // Make dummy text to better render the placeholder.
      Triple(
        AnnotatedString("Dummy creation info."),
        "Dummy title.\nA second line.",
        "Dummy footer"
      )
    } else {
      val author = "@${post.author}"
      val postScore = resources.getScore(post)
      val itemCreatedRelativeTime = DateUtils.getRelativeTimeSpanString(
        post.createdMs,
        currentTimeMillis,
        DateUtils.MINUTE_IN_MILLIS
      )

      Triple(
        buildAnnotatedString {
          withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
            append(author)
          }
          if (length > 0 && itemCreatedRelativeTime != null) {
            append("・")
          }
          append("$itemCreatedRelativeTime")
        },
        post.title.parseAsHtml(),
        postScore
      )
    }
  }

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .wrapContentHeight()
      .clickable { post?.let(onClick) }
  ) {
    Column(
      modifier = Modifier.padding(keyLine * 2),
      verticalArrangement = Arrangement.spacedBy(keyLine),
    ) {
      Text(
        text = postInfo,
        style = MaterialTheme.typography.subtitle1,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.placeholder(
          visible = post == null,
          highlight = PlaceholderHighlight.shimmer()
        )
      )

      HtmlText(
        text = htmlTitle,
        textAppearance = style.Post_Title,
        modifier = Modifier
          .fillMaxWidth()
          .wrapContentHeight()
          .placeholder(
            visible = post == null,
            highlight = PlaceholderHighlight.shimmer()
          )
      )
    }
  }
}

internal fun Resources.getScore(post: Post): String =
  getQuantityString(R.plurals.score_count, post.score, post.score)
