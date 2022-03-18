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

package app.wasabi.compose.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import wasabi.data.model.Post
import wasabi.data.repository.HackerNewsRepository

class HackerNewsFeedViewModel constructor(
  private val repository: HackerNewsRepository = HackerNewsRepository.getInstance(),
) : ViewModel() {

  private val _topStories = MutableStateFlow(PagingData.empty<Post>())
  val topStories: Flow<PagingData<Post>> = _topStories.asStateFlow()

  init {
    viewModelScope.launch(Dispatchers.IO) {
      repository.getTopPosts().flow
        .cachedIn(viewModelScope)
        .onEach { data ->
          _topStories.value = data
        }
        .collect()
    }
  }
}
