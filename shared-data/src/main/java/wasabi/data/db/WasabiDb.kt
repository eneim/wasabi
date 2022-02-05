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

package wasabi.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import wasabi.service.common.dao.CommonDao
import wasabi.service.common.dao.LinkPreview
import wasabi.service.hnews.dao.NumberConverters

@Database(
  entities = [
    LinkPreview::class,
  ],
  version = 1,
  exportSchema = false,
)
@TypeConverters(NumberConverters::class)
abstract class WasabiDb : RoomDatabase() {

  abstract fun commonDao(): CommonDao

  companion object {
    private val cache = mutableMapOf<Boolean, WasabiDb>()

    fun Context.getInstance(inMemory: Boolean): WasabiDb {
      val context = applicationContext
      return cache.getOrPut(inMemory) {
        val builder = if (inMemory) {
          Room.inMemoryDatabaseBuilder(context, WasabiDb::class.java)
        } else {
          Room.databaseBuilder(context, WasabiDb::class.java, "wasabi.db")
        }

        builder
          .fallbackToDestructiveMigration()
          .build()
      }
    }
  }
}
