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
  val keyLine = 4.dp

  val (creationInfo, htmlTitle, stats) = remember(post, currentTimeMillis) {
    if (post == null) {
      // Make dummy text to better render the placeholder.
      PostCellData(
        creationInfo = AnnotatedString("Dummy creation info."),
        htmlTitle = "Dummy title.\nA second line.",
        stats = AnnotatedString("Dummy stat info"),
      )
    } else {
      val createdRelativeTime = DateUtils.getRelativeTimeSpanString(
        post.createdMs,
        currentTimeMillis,
        DateUtils.MINUTE_IN_MILLIS,
        DateUtils.FORMAT_ABBREV_RELATIVE,
      )
      val website = post.website

      PostCellData(
        creationInfo = buildAnnotatedString {
          append("$createdRelativeTime")
          if (website != null && website.isNotBlank()) {
            append("・")
            append(website)
          }
        },
        htmlTitle = post.title.parseAsHtml(),
        stats = buildAnnotatedString {
          append(post.author.orEmpty())
          withStyle(SpanStyle(fontWeight = FontWeight.Normal)) {
            if (length > 0) {
              append("・")
              append(resources.getStat(post))
            }
          }
        }
      )
    }
  }

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .wrapContentHeight()
      .let {
        if (post != null) it.clickable { onClick(post) } else it
      },
  ) {
    Column(
      modifier = Modifier.padding(keyLine * 4),
      verticalArrangement = Arrangement.spacedBy(keyLine),
    ) {
      Text(
        text = creationInfo,
        style = MaterialTheme.typography.subtitle2,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.placeholder(
          visible = post == null,
          highlight = PlaceholderHighlight.shimmer()
        )
      )

      HtmlText(
        text = htmlTitle,
        textAppearance = R.style.Post_Title,
        modifier = Modifier
          .fillMaxWidth()
          .wrapContentHeight()
          .placeholder(
            visible = post == null,
            highlight = PlaceholderHighlight.shimmer()
          )
      )

      Text(
        text = stats,
        style = MaterialTheme.typography.subtitle2,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.placeholder(
          visible = post == null,
          highlight = PlaceholderHighlight.shimmer()
        )
      )
    }
  }
}

private data class PostCellData(
  val creationInfo: AnnotatedString,
  val htmlTitle: CharSequence,
  val stats: AnnotatedString,
)

private fun Resources.getStat(post: Post): AnnotatedString {
  val point = getQuantityString(R.plurals.stat_point, post.point, post.point)
    .takeIf { post.point > 0 }
  val comments = getQuantityString(R.plurals.stat_comment, post.commentsCount, post.commentsCount)

  return buildAnnotatedString {
    if (point != null) append(point)
    if (length > 0) append("・")
    append(comments)
  }
}
