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

package app.wasabi.compose.screens.hnews

import android.text.format.DateUtils
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemGesturesPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.coerceAtMost
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import app.wasabi.compose.R
import app.wasabi.compose.screens.post.PostCell
import app.wasabi.compose.ui.widget.HtmlText
import app.wasabi.compose.utils.asHtml
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import wasabi.data.model.Comment
import wasabi.service.common.model.KeyedNode

private val keyLine = 8.dp

@Composable
fun HackerNewsItemPage(
  viewModel: HackerNewsItemViewModel,
  currentTimeMillis: Long,
) {
  val state = viewModel.uiState
  Surface(modifier = Modifier.fillMaxSize()) {
    HackerNewsItemPage(state, currentTimeMillis)
  }
}

@Composable
private fun HackerNewsItemPage(
  state: ItemState?,
  currentTimeMillis: Long,
) {
  if (state != null) {
    val post = state.post
    val comments = remember { state.comments }.collectAsLazyPagingItems()

    Column(
      modifier = Modifier
        .fillMaxSize()
        .systemGesturesPadding()
    ) {
      PostCell(
        post = post,
        currentTimeMillis = currentTimeMillis,
      )

      Divider()

      if (comments.itemSnapshotList.isEmpty()) {
        Column {
          repeat(20) {
            ItemComment(
              data = null,
              currentTimeMillis = currentTimeMillis,
            )
          }
        }
      } else {
        LazyColumn {
          items(comments) { comment ->
            ItemComment(comment, currentTimeMillis)
          }
        }
      }
    }
  }
}

@Composable
private fun ItemComment(
  data: KeyedNode? = null,
  currentTimeMillis: Long,
) {
  if (data == null) {
    Column(
      verticalArrangement = Arrangement.spacedBy(keyLine),
      modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(keyLine * 2)
    ) {
      Text(
        text = "Dummy comment text",
        style = MaterialTheme.typography.subtitle2,
        modifier = Modifier.placeholder(
          visible = true,
          highlight = PlaceholderHighlight.shimmer()
        )
      )

      Text(
        text = "Longer dummy comment content, \nhas two lines.",
        style = MaterialTheme.typography.subtitle2,
        modifier = Modifier
          .fillMaxWidth()
          .placeholder(
            visible = true,
            highlight = PlaceholderHighlight.shimmer()
          )
      )
    }
  } else if (data is Comment) {
    val indent = (keyLine * 2 * data.level).coerceAtMost(160.dp)
    Divider(
      startIndent = indent,
      thickness = if (data.level == 0) 2.dp else 1.dp,
    )
    Column(
      verticalArrangement = Arrangement.spacedBy(keyLine),
      modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(
          start = keyLine * 2 + indent,
          top = keyLine * 2,
          bottom = keyLine * 2,
          end = keyLine * 2
        )
    ) {
      val author = data.author.name ?: data.author.id
      val createdRelativeTime = DateUtils.getRelativeTimeSpanString(
        data.createdTimestampMillis,
        currentTimeMillis,
        DateUtils.MINUTE_IN_MILLIS
      )
      Text(
        text = buildAnnotatedString {
          append(author)
          withStyle(SpanStyle(fontWeight = FontWeight.Normal)) {
            if (length > 0 && createdRelativeTime != null) {
              append("・")
            }
            append("$createdRelativeTime")
          }
        },
        style = MaterialTheme.typography.subtitle2,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )

      HtmlText(
        text = data.content.asHtml(context = LocalContext.current),
        textAppearance = R.style.Post_Comment,
      )
    }
  }
}
