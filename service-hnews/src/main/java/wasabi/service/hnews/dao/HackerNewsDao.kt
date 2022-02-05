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

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// @Dao
interface HackerNewsDao {

  // @Query("SELECT * FROM hnews_story WHERE id = :id LIMIT 1")
  fun getStory(id: Long): Flow<HackerNewsStory>

  // @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun setStory(story: HackerNewsStory)

  // @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun setStories(stories: Iterable<HackerNewsStory>)

  // @Query("SELECT * FROM hnews_story WHERE id IN (:ids)")
  fun getStories(ids: List<Long>): PagingSource<Int, HackerNewsStory>

  // @Query("SELECT * FROM hnews_comment WHERE id = :id LIMIT 1")
  fun getComment(id: Long): Flow<HackerNewsComment>

  // @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun setComment(comment: HackerNewsComment)

  // @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun setComments(comments: Iterable<HackerNewsComment>)

  // Result of this query doesn't include nested comments/replies.
  // @Query("SELECT * FROM hnews_comment WHERE parentId = :storyId LIMIT 1")
  fun getStoryComments(storyId: Long): PagingSource<Int, HackerNewsComment>
}
