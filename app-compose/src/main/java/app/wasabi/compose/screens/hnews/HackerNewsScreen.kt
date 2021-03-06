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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import app.wasabi.compose.screens.post.PostCell
import wasabi.data.model.Post

@Composable
fun HackerNewsFeed(
  data: LazyPagingItems<Post>,
  currentTimeMillis: Long,
  modifier: Modifier = Modifier,
  onClick: ((Post) -> Unit) = {},
) {
  if (data.itemSnapshotList.isNotEmpty()) {
    LazyColumn(
      modifier = modifier
        .fillMaxSize()
        .systemBarsPadding()
    ) {
      itemsIndexed(data) { _: Int, value: Post? ->
        PostCell(
          post = value,
          currentTimeMillis = currentTimeMillis,
          onClick = onClick,
        )

        Divider()
      }
    }
  } else {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .systemBarsPadding(),
    ) {
      repeat(20) {
        PostCell(
          post = null,
          currentTimeMillis = currentTimeMillis,
        )
      }
    }
  }
}
