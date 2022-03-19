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

import android.net.Uri
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.paging.compose.collectAsLazyPagingItems
import app.wasabi.compose.screens.hnews.HackerNewsFeed
import app.wasabi.compose.screens.hnews.HackerNewsItemPage
import app.wasabi.compose.screens.hnews.HackerNewsItemViewModel
import app.wasabi.compose.screens.hnews.HackerNewsViewModel
import app.wasabi.compose.ui.theme.WasabiTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import wasabi.data.repository.HackerNewsRepository

@Composable
fun WasabiApp(
  navController: NavHostController,
  currentTimeMillis: Long,
  hackerNewsViewModel: HackerNewsViewModel,
) {
  WasabiTheme {
    val systemUiController = rememberSystemUiController()
    val darkIcons = MaterialTheme.colors.isLight
    SideEffect {
      systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = darkIcons)
    }

    NavHost(navController = navController, startDestination = Screen.Home.route) {
      composable(Screen.Home.route) { entry ->
        val topStories = remember { hackerNewsViewModel.topStories }
          .collectAsLazyPagingItems()
        HackerNewsFeed(
          data = topStories,
          currentTimeMillis = currentTimeMillis,
          onClick = { post ->
            if (entry.lifecycleIsResumed()) {
              navController.navigate(Screen.HnStory.createRoute(Uri.encode(post.internalId)))
            }
          }
        )
      }

      composable(Screen.HnStory.route) { entry ->
        val viewModel: HackerNewsItemViewModel = viewModel(
          factory = HackerNewsItemViewModel.provideFactory(
            owner = entry,
            defaultArgs = entry.arguments,
            repositoryProvider = {
              HackerNewsRepository.getInstance()
            }
          )
        )
        HackerNewsItemPage(viewModel = viewModel, currentTimeMillis = currentTimeMillis)
      }
    }
  }
}

/**
 * If the lifecycle is not resumed it means this NavBackStackEntry already processed a nav event.
 *
 * This is used to de-duplicate navigation events.
 */
private fun NavBackStackEntry.lifecycleIsResumed() =
  this.lifecycle.currentState == Lifecycle.State.RESUMED
