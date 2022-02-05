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

package wasabi.service.hnews

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.ToJson
import wasabi.service.hnews.entities.HackerNewsItem

object ItemTypeAdapter {

  private val values = HackerNewsItem.Type.values()

  @FromJson
  fun fromJson(value: String): HackerNewsItem.Type = values.firstOrNull { it.value == value }
    ?: throw JsonDataException("Unknown type: $value")

  @ToJson
  fun toJson(type: HackerNewsItem.Type): String = type.value
}
