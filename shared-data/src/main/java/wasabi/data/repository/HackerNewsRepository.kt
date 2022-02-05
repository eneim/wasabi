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

package wasabi.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.squareup.moshi.Moshi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import wasabi.base.getResult
import wasabi.data.model.Post
import wasabi.data.model.Service.HACKER_NEWS
import wasabi.service.common.ZonedDateTimeConverter
import wasabi.service.hnews.ItemTypeAdapter
import wasabi.service.hnews.api.HackerNewsApi
import wasabi.service.hnews.domain.HackerNewsPagingSource
import java.time.Duration

class HackerNewsRepository(
  private val hackerNewsApi: HackerNewsApi,
) {

  // TODO: dependency injection.
  private val postsLoader: suspend (List<Long>) -> List<Post> = { ids ->
    coroutineScope {
      ids.map { id ->
        async {
          hackerNewsApi.getItem(id).getResult().getOrNull()
        }
      }
        .awaitAll()
        .filterNotNull()
        .map { item ->
          Post(
            id = item.id.toString(),
            author = item.author,
            createdMs = checkNotNull(item.createTimestampMillis),
            title = item.title.orEmpty(),
            content = item.text,
            link = item.url.orEmpty(),
            score = item.score ?: 0,
            key = item.id,
            service = HACKER_NEWS,
          )
        }
    }
  }

  suspend fun getTopPosts(
    pageSize: Int = PAGE_SIZE,
    enablePlaceholders: Boolean = true,
  ): Pager<Long, Post> {
    val topStoryIds = hackerNewsApi.getTopStories()
      .getResult()
      .getOrThrow()
      ?: emptyList()
    return Pager(
      config = PagingConfig(
        pageSize = pageSize,
        enablePlaceholders = enablePlaceholders,
        maxSize = pageSize * 5,
      ),
      initialKey = null,
      pagingSourceFactory = {
        HackerNewsPagingSource(topStoryIds, postsLoader)
      }
    )
  }

  companion object {
    private const val PAGE_SIZE = 25

    // TODO: DI
    fun getInstance(): HackerNewsRepository = HackerNewsRepository(
      hackerNewsApi = Retrofit.Builder()
        .baseUrl("https://hacker-news.firebaseio.com")
        .addConverterFactory(
          MoshiConverterFactory.create(
            Moshi.Builder()
              .add(ZonedDateTimeConverter)
              .add(ItemTypeAdapter)
              .build()
          )
        )
        .callFactory(
          OkHttpClient.Builder()
            .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(Level.HEADERS))
            .connectTimeout(Duration.ofSeconds(15))
            .callTimeout(Duration.ofSeconds(15))
            .readTimeout(Duration.ofSeconds(15))
            .build()
        )
        .build()
        .create(HackerNewsApi::class.java)
    )
  }
}
