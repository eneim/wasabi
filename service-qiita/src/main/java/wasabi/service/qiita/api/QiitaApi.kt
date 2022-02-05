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

package wasabi.service.qiita.api

import androidx.annotation.IntRange
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import wasabi.service.qiita.entities.Item

// https://qiita.com/api/v2/docs
// Endpoint: https://qiita.com
interface QiitaApi {

  @GET("/api/v2/authenticated_user/items")
  suspend fun getUserItems(
    @Query("page") page: Int,
    @IntRange(from = 1, to = 100)
    @Query("per_page") count: Int = 20
  ): Response<List<Item>>

  // page: min = 1, max = 100
  // per_page: min = 1, max = 100
  @GET("/api/v2/items")
  suspend fun getItems(
    @Query("query") query: String? = null,
    @IntRange(from = 1, to = 100)
    @Query("page") page: Int,
    @IntRange(from = 1, to = 100)
    @Query("per_page") count: Int = 20
  ): Response<List<Item>>

  @GET("/api/v2/items/{id}")
  suspend fun getItemDetail(
    @Path("id") itemId: String
  ): Response<Item>

  // If status is 204 -> item is stocked
  @GET("/api/v2/items/{id}/stock")
  suspend fun getItemStockState(
    @Path("id") itemId: String
  ): Response<Unit>

  @GET("/api/v2/tags/{id}/items")
  suspend fun getItemsForTag(
    @Path("id") tagId: String,
    @Query("page") page: Int,
    @IntRange(from = 1, to = 100)
    @Query("per_page") count: Int = 20
  ): Response<List<Item>>

  @GET("/api/v2/users/{id}/items")
  suspend fun getItemsForUser(
    @Path("id") userId: String,
    @Query("page") page: Int,
    @IntRange(from = 1, to = 100)
    @Query("per_page") count: Int = 20
  ): Response<List<Item>>

  @GET("/api/v2/users/{id}/stocks")
  suspend fun getUserStockedItems(
    @Path("id") userId: String,
    @Query("page") page: Int,
    @IntRange(from = 1, to = 100)
    @Query("per_page") count: Int = 20
  ): Response<List<Item>>
}
