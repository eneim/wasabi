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

/**
 * 記事やプロジェクトに付けられたコメントを表します。プロジェクトへのコメントはQiita Teamでのみ有効です。
 */
@Keep
@JsonClass(generateAdapter = true)
data class Comment(
  /**
   * コメントの内容を表すMarkdown形式の文字列
   */
  @Json(name = "body")
  val body: String,
  /**
   * データが作成された日時
   */
  @Json(name = "created_at")
  val createdAt: ZonedDateTime,
  /**
   * コメントの一意なID
   */
  @Json(name = "id")
  val id: String,
  /**
   * コメントの内容を表すHTML形式の文字列
   */
  @Json(name = "rendered_body")
  val renderedBody: String,
  /**
   * データが最後に更新された日時
   */
  @Json(name = "updated_at")
  val updatedAt: ZonedDateTime,
  /**
   * Qiita上のユーザを表します。
   */
  @Json(name = "user")
  val user: User
)
