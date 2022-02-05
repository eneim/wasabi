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

package wasabi.service.qiita.entities

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

/**
 * ユーザからの投稿を表します。
 */
@Keep
@JsonClass(generateAdapter = true)
data class Item(
  /**
   * Markdown形式の本文
   */
  @Json(name = "body")
  val body: String,
  /**
   * この記事が共同更新状態かどうか (Qiita Teamでのみ有効)
   */
  @Json(name = "coediting")
  val coediting: Boolean,
  /**
   * この記事へのコメントの数
   */
  @Json(name = "comments_count")
  val commentsCount: Int,
  /**
   * データが作成された日時
   */
  @Json(name = "created_at")
  val createdAt: ZonedDateTime,
  /**
   * Qiita Teamのグループを表します。
   */
  @Json(name = "group")
  val group: Group?,
  /**
   * 記事の一意なID
   */
  @Json(name = "id")
  val id: String,
  /**
   * この記事への「LGTM！」の数（Qiitaでのみ有効）
   */
  @Json(name = "likes_count")
  val likesCount: Int,
  /**
   * 閲覧数
   */
  @Json(name = "page_views_count")
  val pageViewsCount: Int? = -1,
  /**
   * 限定共有状態かどうかを表すフラグ (Qiita Teamでは無効)
   */
  @Json(name = "private")
  val isPrivate: Boolean,
  /**
   * 絵文字リアクションの数（Qiita Teamでのみ有効）
   */
  @Json(name = "reactions_count")
  val reactionsCount: Int,
  /**
   * HTML形式の本文
   */
  @Json(name = "rendered_body")
  val renderedBody: String,
  /**
   * 記事に付いたタグ一覧
   */
  @Json(name = "tags")
  val tags: List<Tagging>,
  /**
   * Qiita Team のチームメンバー情報を表します。
   */
  @Json(name = "team_membership")
  val teamMembership: Membership?,
  /**
   * 記事のタイトル
   */
  @Json(name = "title")
  val title: String,
  /**
   * データが最後に更新された日時
   */
  @Json(name = "updated_at")
  val updatedAt: ZonedDateTime,
  /**
   * 記事のURL
   */
  @Json(name = "url")
  val url: String,
  /**
   * Qiita上のユーザを表します。
   */
  @Json(name = "user")
  val user: User,

  @Json(name = "feature_image") // Set by client.
  val featureImage: String? = null,
)

val Item.createdAtMillis: Long get() = TimeUnit.SECONDS.toMillis(createdAt.toEpochSecond())
