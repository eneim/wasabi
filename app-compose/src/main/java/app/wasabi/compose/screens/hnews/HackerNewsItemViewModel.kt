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

import android.net.Uri
import android.os.Bundle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import wasabi.data.repository.HackerNewsRepository

class HackerNewsItemViewModel(
  private val repository: HackerNewsRepository,
  private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

  private val itemId: String = Uri.decode(requireNotNull(savedStateHandle.get<String>("id")))

  var uiState: ItemState? by mutableStateOf(null)
    private set

  init {
    viewModelScope.launch(Dispatchers.IO) {
      val itemData = repository.getPostDetail(itemId)
      uiState = ItemState(
        post = itemData.first,
        comments = itemData.second.flow.cachedIn(this),
      )
    }
  }

  companion object {

    fun provideFactory(
      owner: SavedStateRegistryOwner,
      defaultArgs: Bundle? = null,
      repositoryProvider: () -> HackerNewsRepository,
    ): AbstractSavedStateViewModelFactory = object : AbstractSavedStateViewModelFactory(
      owner,
      defaultArgs
    ) {
      @Suppress("UNCHECKED_CAST")
      override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle,
      ): T {
        val repository = repositoryProvider()
        return HackerNewsItemViewModel(
          repository = repository,
          savedStateHandle = handle
        ) as T
      }
    }
  }
}
