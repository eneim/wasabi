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

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadParams.Refresh
import androidx.paging.PagingState
import wasabi.service.common.model.Keyed

class HackerNewsPagingSource<T : Keyed>(
  private val ids: List<Long>,
  private val loader: suspend (List<Long>) -> List<T>,
) : PagingSource<Long, T>() {

  override val keyReuseSupported: Boolean = true

  override fun getRefreshKey(state: PagingState<Long, T>): Long? {
    val anchor = state.anchorPosition ?: return null
    return state.closestItemToPosition(anchor)?.key?.toLong()
  }

  override suspend fun load(params: LoadParams<Long>): LoadResult<Long, T> {
    val startIndex = ids.indexOf(params.key).coerceAtLeast(0)
    val endIndex = startIndex + params.loadSize // Exclusive.
    val idsToLoad = ids.subList(startIndex, endIndex)

    return try {
      val items = loader(idsToLoad)
      val lastItemId = items.last().key.toLong()
      val nextPageKey = ids.getOrNull(ids.indexOf(lastItemId) + 1)

      val prevKey = ids[(startIndex - params.loadSize).coerceAtLeast(0)]
        .takeIf { params !is Refresh && startIndex > 0 }

      LoadResult.Page(
        data = items,
        prevKey = prevKey,
        nextKey = nextPageKey,
        itemsBefore = startIndex,
        itemsAfter = ids.size - endIndex,
      )
    } catch (error: Throwable) {
      error.printStackTrace()
      LoadResult.Error(error)
    }
  }
}
