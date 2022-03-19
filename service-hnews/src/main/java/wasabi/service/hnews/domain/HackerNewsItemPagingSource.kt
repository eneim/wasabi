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

class HackerNewsItemPagingSource<T : Keyed>(
  private val ids: List<Long>,
  private val loader: suspend (List<Long>) -> List<T>,
) : PagingSource<Long, T>() {

  override val keyReuseSupported: Boolean = true

  // Copied from https://developer.android.com/topic/libraries/architecture/paging/v3-paged-data#pagingsource
  // The refresh key is used for subsequent refresh calls to PagingSource.load after the initial load
  override fun getRefreshKey(state: PagingState<Long, T>): Long? {
    // Try to find the page key of the closest page to anchorPosition, from
    // either the prevKey or the nextKey, but you need to handle nullability
    // here:
    //  * prevKey == null -> anchorPage is the first page.
    //  * nextKey == null -> anchorPage is the last page.
    //  * both prevKey and nextKey null -> anchorPage is the initial page, so
    //    just return null.
    val anchor = state.anchorPosition ?: return null
    val anchorPage = state.closestPageToPosition(anchor)
    return anchorPage?.prevKey?.plus(1)
      ?: anchorPage?.nextKey?.minus(1)
  }

  override suspend fun load(params: LoadParams<Long>): LoadResult<Long, T> {
    if (ids.isEmpty()) return LoadResult.Page(emptyList(), null, null)

    val startIndex = ids.indexOf(params.key).coerceAtLeast(0)
    val endIndex = startIndex + params.loadSize // Exclusive.
    val idsToLoad = ids.subList(startIndex, endIndex.coerceAtMost(ids.size))

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
        itemsAfter = (ids.size - endIndex).coerceAtLeast(0),
      )
    } catch (error: Throwable) {
      error.printStackTrace()
      LoadResult.Error(error)
    }
  }
}
