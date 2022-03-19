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

import androidx.collection.LongSparseArray
import androidx.paging.PagingSource
import androidx.paging.PagingState
import wasabi.service.common.model.KeyedNode

class HackerNewsCommentPagingSource(
  private val total: Int,
  private val rootCommentIds: List<Long>,
  private val loader: suspend (Long) -> KeyedNode?,
) : PagingSource<Long, KeyedNode>() {

  private val sizeCache = LongSparseArray<Int>()

  // Copied from https://developer.android.com/topic/libraries/architecture/paging/v3-paged-data#pagingsource
  // The refresh key is used for subsequent refresh calls to PagingSource.load after the initial load
  override fun getRefreshKey(state: PagingState<Long, KeyedNode>): Long? {
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

  override suspend fun load(params: LoadParams<Long>): LoadResult<Long, KeyedNode> {
    if (total == 0 || rootCommentIds.isEmpty()) {
      return LoadResult.Page(emptyList(), null, null)
    }

    var loadedCount = 0
    val comments = mutableListOf<KeyedNode>()

    var lastRootId = params.key ?: Long.MIN_VALUE
    var lastRootIndex: Int = rootCommentIds.indexOf(lastRootId)

    val prevKey = lastRootId.takeIf { it in rootCommentIds }

    do {
      val targetRootIndex = lastRootIndex.plus(1)
      val targetRootId = rootCommentIds.getOrNull(targetRootIndex) ?: break
      lastRootIndex = targetRootIndex
      lastRootId = targetRootId

      val thread = loader(targetRootId) ?: break
      comments.addAll(thread.tree)
      val threadSize = thread.treeSize
      loadedCount += threadSize
      sizeCache.put(lastRootId, threadSize)
    } while (loadedCount < PAGE_SIZE)

    val itemsBefore =
      rootCommentIds.subList(0, lastRootIndex + 1).sumOf { sizeCache[it] ?: 0 } - loadedCount
    val itemsAfter = (total - itemsBefore - comments.size).coerceAtLeast(0)

    return LoadResult.Page(
      data = comments,
      prevKey = prevKey,
      nextKey = lastRootId.takeIf { it != params.key },
      itemsBefore = itemsBefore,
      itemsAfter = itemsAfter,
    )
  }

  private companion object {
    const val PAGE_SIZE = 10
  }
}
