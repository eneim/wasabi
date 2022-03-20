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

import androidx.navigation.NavDeepLink
import androidx.navigation.navDeepLink

sealed class Screen(
  val route: String,
  val deepLinks: List<NavDeepLink> = emptyList(),
) {

  object Home : Screen("home")

  object HnStory : Screen(
    route = "story/{id}",
    deepLinks = listOf(
      navDeepLink { uriPattern = "wasabi://news.ycombinator.com/item?id={id}" },
      navDeepLink { uriPattern = "https://news.ycombinator.com/item?id={id}" },
      navDeepLink { uriPattern = "http://news.ycombinator.com/item?id={id}" }
    )
  ) {
    fun createRoute(storyId: String): String = "story/$storyId"
  }
}
