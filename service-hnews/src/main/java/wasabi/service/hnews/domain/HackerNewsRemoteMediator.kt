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

package wasabi.service.hnews.domain

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.LoadType.APPEND
import androidx.paging.LoadType.PREPEND
import androidx.paging.LoadType.REFRESH
import androidx.paging.PagingSource.LoadResult.Page
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import wasabi.service.hnews.dao.HackerNewsDao
import wasabi.service.hnews.dao.HackerNewsStory

@ExperimentalPagingApi
class HackerNewsRemoteMediator(
  private val ids: List<Long>,
  private val dao: HackerNewsDao,
  private val loader: (List<Long>) -> List<HackerNewsStory>,
) : RemoteMediator<Int, HackerNewsStory>() {

  override suspend fun initialize(): InitializeAction = InitializeAction.LAUNCH_INITIAL_REFRESH

  override suspend fun load(
    loadType: LoadType,
    state: PagingState<Int, HackerNewsStory>
  ): MediatorResult {
    state.anchorPosition

    val idsToLoad: List<Long> = when (loadType) {
      REFRESH -> ids.take(state.config.pageSize)
      PREPEND -> emptyList()
      APPEND -> {
        val loadedIds = state.pages
          .flatMap(Page<Int, HackerNewsStory>::data)
          .mapNotNull(HackerNewsStory::id)
        val remainingIds = ids - loadedIds
        remainingIds.take(state.config.pageSize)
      }
    }

    return try {
      val items = loader(idsToLoad)
      dao.setStories(items)
      MediatorResult.Success(endOfPaginationReached = items.size < state.config.pageSize)
    } catch (error: Throwable) {
      error.printStackTrace()
      MediatorResult.Error(error)
    }
  }
}
