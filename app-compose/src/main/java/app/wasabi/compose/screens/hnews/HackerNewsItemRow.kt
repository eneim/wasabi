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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import app.wasabi.compose.R
import app.wasabi.compose.ui.widget.HtmlText
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import wasabi.service.hnews.entities.HackerNewsItem

private val keyLine = 8.dp

@Composable
fun HackerNewsListItem(
  item: HackerNewsItem?,
  currentTimeMillis: Long = System.currentTimeMillis(),
  onClick: (HackerNewsItem) -> Unit,
) {
  val uiState: ItemUiState = if (item == null) {
    ItemUiState(
      AnnotatedString("Placeholder header"),
      AnnotatedString("Placeholder title"),
      AnnotatedString("Placeholder footer")
    )
  } else {
    val resources = LocalContext.current.resources
    val author = "@${item.author}"
    val createdRelativeTime = DateUtils.getRelativeTimeSpanString(
      item.createTimestampMillis ?: currentTimeMillis,
      currentTimeMillis,
      DateUtils.MINUTE_IN_MILLIS
    )
    val website = item.website

    ItemUiState(
      header = buildAnnotatedString {
        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
          append(author)
        }
        if (length > 0 && createdRelativeTime != null) {
          append("・")
        }
        append("$createdRelativeTime")
        if (length > 0 && website != null && website.isNotBlank()) {
          append("・")
          append(website)
        }
      },
      title = buildAnnotatedString {
        append(item.title.orEmpty())
      },
      footer = resources.getStat(item),
    )
  }

  HackerNewsListItem(
    uiState = uiState,
    usePlaceholder = item == null,
    modifier = Modifier
      .fillMaxWidth()
      .wrapContentHeight()
      .let {
        if (item != null) it.clickable { onClick(item) } else it
      },
  )
}

@Composable
private fun HackerNewsListItem(
  uiState: ItemUiState,
  usePlaceholder: Boolean,
  modifier: Modifier = Modifier,
) {
  Surface(
    modifier = modifier,
  ) {
    Column(
      modifier = modifier.padding(keyLine * 2),
      verticalArrangement = Arrangement.spacedBy(keyLine),
    ) {
      Text(
        text = uiState.header,
        style = MaterialTheme.typography.subtitle2,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.placeholder(
          visible = usePlaceholder,
          highlight = PlaceholderHighlight.shimmer()
        )
      )

      HtmlText(
        text = uiState.title,
        textAppearance = R.style.Post_Title,
        modifier = Modifier
          .fillMaxWidth()
          .wrapContentHeight()
          .placeholder(
            visible = usePlaceholder,
            highlight = PlaceholderHighlight.shimmer()
          )
      )

      Text(
        text = uiState.footer,
        style = MaterialTheme.typography.subtitle2,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.placeholder(
          visible = usePlaceholder,
          highlight = PlaceholderHighlight.shimmer()
        )
      )
    }
  }
}

private data class ItemUiState(
  val header: AnnotatedString,
  val title: AnnotatedString,
  val footer: AnnotatedString,
  val thumbnailUrl: String? = null,
  val externalSiteUrl: String? = null,
)

private fun Resources.getStat(item: HackerNewsItem): AnnotatedString {
  val itemStore = item.score ?: 0
  val itemCommentsCount = item.descendants?.takeIf { item.type == HackerNewsItem.Type.STORY } ?: 0
  val point = getQuantityString(R.plurals.stat_point, itemStore, itemStore)
    .takeIf { itemStore > 0 }
  val comments = getQuantityString(R.plurals.stat_comment, itemCommentsCount, itemCommentsCount)

  return buildAnnotatedString {
    if (point != null) append(point)
    if (length > 0) append("・")
    append(comments)
  }
}
