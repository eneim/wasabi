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

package wasabi.service.hnews.dao

import android.net.Uri
import wasabi.service.common.dao.LinkPreview
import wasabi.service.common.model.Keyed

data class HackerNewsStory(
  val id: Long,
  val author: String,
  val createdTimestampMs: Long,
  val title: String?,
  val text: String?,
  val url: String?,
  val commentIds: List<Long>?,
  val commentCount: Int,
  val score: Int,
  val deleted: Boolean = false,
  val dead: Boolean = false,
  val preview: LinkPreview?,
) : Keyed {

  override val key: Number = id

  val website: String? get() = url?.let { Uri.parse(it).host }
}
