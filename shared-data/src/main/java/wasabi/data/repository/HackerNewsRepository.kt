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
import wasabi.data.model.Comment
import wasabi.data.model.Post
import wasabi.data.model.Service.HACKER_NEWS
import wasabi.data.model.User
import wasabi.service.common.ZonedDateTimeConverter
import wasabi.service.common.model.KeyedNode
import wasabi.service.hnews.ItemTypeAdapter
import wasabi.service.hnews.api.HackerNewsApi
import wasabi.service.hnews.domain.HackerNewsCommentPagingSource
import wasabi.service.hnews.domain.HackerNewsItemPagingSource
import wasabi.service.hnews.entities.HackerNewsItem
import wasabi.service.hnews.entities.HackerNewsItem.Type.COMMENT
import wasabi.service.hnews.entities.HackerNewsItem.Type.STORY
import java.io.IOException
import java.time.Duration

class HackerNewsRepository(
  private val hackerNewsApi: HackerNewsApi,
) {

  // private val userCache = mutableMapOf<String, HackerNewsUser>()

  private val postMapper: (HackerNewsItem) -> Post = { item ->
    Post(
      internalId = item.id.toString(),
      author = item.author,
      createdMs = checkNotNull(item.createTimestampMillis),
      title = item.title.orEmpty(),
      content = item.text,
      link = item.url.orEmpty(),
      point = item.score ?: 0,
      commentsCount = item.descendants ?: 0,
      key = item.id,
      service = HACKER_NEWS,
    )
  }

  // TODO: dependency injection.
  private val postsLoader: suspend (List<Long>) -> List<Post> = { ids ->
    coroutineScope {
      ids.map { id ->
        async {
          val item = hackerNewsApi.getItem(id).getResult().getOrNull()
          item?.let(postMapper)
        }
      }
        .awaitAll()
        .filterNotNull()
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
        HackerNewsItemPagingSource(topStoryIds, postsLoader)
      }
    )
  }

  suspend fun getPostDetail(id: String): Pair<Post, Pager<Long, KeyedNode>> {
    val item = hackerNewsApi.getItem(id.toLong())
      .getResult()
      .getOrNull()
      ?: throw IOException("Cannot load item for id=$id.")
    return postMapper(item) to getComments(item)
  }

  private fun getComments(item: HackerNewsItem): Pager<Long, KeyedNode> {
    val rootCommentIds = item.kids.takeIf { item.type == STORY }.orEmpty()
    return Pager(
      config = PagingConfig(
        pageSize = 10,
        enablePlaceholders = true,
        maxSize = 50,
      ),
      initialKey = null,
      pagingSourceFactory = {
        HackerNewsCommentPagingSource(
          total = item.descendants ?: 0,
          rootCommentIds = rootCommentIds
        ) { fetchComment(it, level = 0) }
      }
    )
  }

  private suspend fun fetchComment(
    rootId: Long,
    level: Int = 0
  ): Comment? {
    val rootComment = hackerNewsApi.getItem(rootId)
      .getResult()
      .getOrNull()
      ?.takeIf {
        it.type == COMMENT &&
          !it.isDead &&
          !it.isDeleted &&
          !it.author.isNullOrBlank()
      }
      ?: return null

    val userId = requireNotNull(rootComment.author)
    /* val author = userCache.getOrElse(userId) {
      hackerNewsApi.getUser(requireNotNull(rootComment.author))
        .getResult()
        .getOrNull()
        ?.also { user -> userCache[userId] = user }
    } ?: return null */

    val commentUser = User(
      internalId = userId,
      name = userId,
      createdTimestampMillis = Long.MIN_VALUE, // Not used
      service = HACKER_NEWS,
    )

    val replies = mutableListOf<Comment>()
    val kids = rootComment.kids.orEmpty()
    if (kids.isNotEmpty()) {
      coroutineScope {
        kids.map {
          async { fetchComment(it, level = level + 1) }
        }
          .awaitAll()
          .filterNotNull()
          .let(replies::addAll)
      }
    }

    val parentId = requireNotNull(rootComment.parent)

    return Comment(
      internalId = rootId.toString(),
      link = rootComment.url,
      post = parentId.toString(),
      content = rootComment.text.orEmpty(),
      author = commentUser,
      createdTimestampMillis = requireNotNull(rootComment.createTimestampMillis),
      parentId = parentId,
      replies = replies,
      level = level,
      service = HACKER_NEWS,
    )
  }

  companion object {
    private const val PAGE_SIZE = 10

    private val singleton: HackerNewsRepository by lazy {
      HackerNewsRepository(
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

    // TODO: DI
    fun getInstance(): HackerNewsRepository = singleton
  }
}
