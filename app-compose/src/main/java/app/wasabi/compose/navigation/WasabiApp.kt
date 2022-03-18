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

package app.wasabi.compose.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import app.wasabi.compose.screens.home.HackerNewsFeedViewModel
import app.wasabi.compose.screens.post.PostCell
import app.wasabi.compose.ui.theme.WasabiTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import wasabi.data.model.Post

@Composable
fun WasabiApp(
  currentTimeMillis: Long,
) {
  WasabiTheme {
    val systemUiController = rememberSystemUiController()
    val darkIcons = MaterialTheme.colors.isLight
    SideEffect {
      systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = darkIcons)
    }

    val hackerNewsFeedViewModel: HackerNewsFeedViewModel = viewModel()
    val topHackerNewsPosts = remember { hackerNewsFeedViewModel.topStories }
      .collectAsLazyPagingItems()

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
    ) {
      itemsIndexed(topHackerNewsPosts) { _: Int, value: Post? ->
        PostCell(
          post = value,
          currentTimeMillis = currentTimeMillis,
        )
      }
    }
  }
}
