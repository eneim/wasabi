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

package wasabi.data.model

import wasabi.service.common.model.KeyedNode

data class Comment(
  val internalId: String,
  val link: String?,
  val post: String,
  val content: String,
  val author: User,
  val createdTimestampMillis: Long,
  val parentId: Long,
  val replies: List<Comment> = emptyList(),
  val level: Int, // TODO: move this to the UiState object instead?
  val service: Service,
) : Unique, KeyedNode {

  override val id: String = "${service.site}::$internalId"

  override val key: Number = parentId

  override val children: List<Comment> = replies

  override val tree: List<KeyedNode> = buildList {
    add(this@Comment)
    replies.filter { it.parentId.toString() == this@Comment.internalId }
      .forEach { addAll(it.tree) }
  }

  override val treeSize: Int = 1 + replies.sumOf(Comment::treeSize)
}
