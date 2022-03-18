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

package wasabi.service.qiita.domain

import android.net.Uri
import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadParams.Refresh
import androidx.paging.PagingState
import tel.schich.rfc5988.Link
import tel.schich.rfc5988.Parameter
import tel.schich.rfc5988.parseLink
import tel.schich.rfc5988.parsing.StringSlice
import tel.schich.rfc5988.parsing.getOrNull
import wasabi.base.getResult
import wasabi.service.qiita.api.QiitaApi
import wasabi.service.qiita.entities.Item

@Suppress("unused")
class QiitaPagingSource<T : Any>(
  private val api: QiitaApi,
  private val mapper: (page: Int, item: Item) -> T,
) : PagingSource<Int, T>() {

  override fun getRefreshKey(state: PagingState<Int, T>): Int? {
    val anchor = state.anchorPosition ?: return null
    return state.closestPageToPosition(anchor)?.prevKey
  }

  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
    val page = params.key ?: MIN_PAGE

    return try {
      // Always load PAGE_SIZE so we can calculate the remaining items.
      val response = api.getItems(page = page, count = PAGE_SIZE)
      if (response.isSuccessful) {
        val headers = response.headers()
        /*
         * Sample:
         *
         * <https://qiita.com/api/v2/items?page=1&per_page=25>; rel="first",
         * <https://qiita.com/api/v2/items?page=5&per_page=25>; rel="prev",
         * <https://qiita.com/api/v2/items?page=7&per_page=25>; rel="next",
         * <https://qiita.com/api/v2/items?page=29613&per_page=25>; rel="last"
         */
        val links = parseLink(StringSlice.of(headers.get(HEADER_LINK) ?: "")).getOrNull()

        val items = (response.getResult().getOrThrow() ?: emptyList()).map { item ->
          mapper(page, item)
        }

        val prevPageKey = links?.find { it.rel == LINK_PREV }
          ?.page
          ?: page.minus(1)

        val nextPageKey = links?.find { it.rel == LINK_NEXT }
          ?.page
          ?: page.plus(1)

        val itemsBefore = PAGE_SIZE * page.minus(1).coerceAtLeast(0)
        val itemsAfter = (PAGE_SIZE * MAX_PAGE) - itemsBefore - items.size

        LoadResult.Page(
          data = items,
          prevKey = prevPageKey.takeIf {
            params !is Refresh && params.key != MIN_PAGE
          },
          nextKey = nextPageKey.coerceAtMost(MAX_PAGE),
          itemsBefore = itemsBefore,
          itemsAfter = itemsAfter,
        )
      } else {
        LoadResult.Error(IllegalStateException(""))
      }
    } catch (error: Throwable) {
      LoadResult.Error(error)
    }
  }

  companion object {
    const val PAGE_SIZE = 50

    private const val MIN_PAGE = 1
    private const val MAX_PAGE = 100

    private const val HEADER_LINK = "link"
    private const val LINK_PREV = "prev"
    private const val LINK_NEXT = "next"

    private const val QUERY_PAGE = "page"
    private const val QUERY_PER_PAGE = "per_page"

    private val Link.uri: Uri get() = Uri.parse(target)
    private val Link.rel: String? get() = parameters.map(Parameter::name).firstOrNull()
    private val Link.page: Int? get() = uri.getQueryParameter(QUERY_PAGE)?.toInt()
    private val Link.perPage: Int? get() = uri.getQueryParameter(QUERY_PER_PAGE)?.toInt()
  }
}
