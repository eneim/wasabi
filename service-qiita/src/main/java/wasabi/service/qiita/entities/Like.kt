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
 * <strong>Qiita TeamのLGTMAPIは2020年11月4日より廃止となりました。今後は絵文字リアクションAPIをご利用ください。</strong> 記事につけられた「LGTM！」を表します。
 */
@Keep
@JsonClass(generateAdapter = true)
data class Like(
  /**
   * データが作成された日時
   */
  @Json(name = "created_at")
  val createdAt: ZonedDateTime,
  /**
   * Qiita上のユーザを表します。
   */
  @Json(name = "user")
  val user: User
)
