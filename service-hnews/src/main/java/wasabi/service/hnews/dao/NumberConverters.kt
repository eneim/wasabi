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

import androidx.room.TypeConverter

object NumberConverters {

  private const val NUMBER_SEPARATOR = ";"
  private val NUMBER_DELIMITERS = NUMBER_SEPARATOR.toCharArray()

  @TypeConverter
  fun numberToString(number: Number): String = number.toString()

  @TypeConverter
  fun stringToNumber(value: String): Number = value.toDouble()

  @TypeConverter
  fun longListToString(numbers: List<Long>): String = numbers
    .joinToString(separator = NUMBER_SEPARATOR)

  @TypeConverter
  fun stringToLongList(rawString: String): List<Long> = rawString
    .splitToSequence(delimiters = NUMBER_DELIMITERS)
    .map(String::toLongOrNull)
    .filterNotNull()
    .toList()
}
