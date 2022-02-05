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

package app.wasabi.compose.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import app.wasabi.compose.ui.home.HackerNewsFeedViewModel
import app.wasabi.compose.ui.home.QiitaFeedViewModel
import app.wasabi.compose.ui.theme.WasabiTheme
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import wasabi.data.model.Post

@Composable
fun WasabiApp() {
  WasabiTheme {
    ProvideWindowInsets {
      val systemUiController = rememberSystemUiController()
      val darkIcons = MaterialTheme.colors.isLight
      SideEffect {
        systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = darkIcons)
      }

      val hackerNewsFeedViewModel: HackerNewsFeedViewModel = viewModel()
      val qiitaFeedViewModel: QiitaFeedViewModel = viewModel()

      val topHackerNewsPosts = remember { hackerNewsFeedViewModel.topStories }
        .collectAsLazyPagingItems()

      val qiitaFeed = remember { qiitaFeedViewModel.articles }
        .collectAsLazyPagingItems()

      LazyColumn(
        contentPadding = PaddingValues(4.dp)
      ) {

        itemsIndexed(topHackerNewsPosts) { index: Int, value: Post? ->
          Text(
            text = "[${index + 1}] " + (value?.title ?: "Loading"),
            modifier = Modifier
              .padding(4.dp)
              .border(width = 1.dp, color = Color.Blue)
              .fillMaxWidth()
              .wrapContentHeight()
              .padding(8.dp)
          )
        }

        /* itemsIndexed(qiitaFeed) { index: Int, value: QiitaArticle? ->
          Text(
            text = "[${index + 1}] " + (value?.article?.title ?: "Loading"),
            modifier = Modifier
              .padding(4.dp)
              .border(width = 1.dp, color = Color.Blue)
              .fillMaxWidth()
              .wrapContentHeight()
              .padding(8.dp)
          )
        } */
      }
    }
  }
}
