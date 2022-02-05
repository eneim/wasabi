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

package wasabi.service.common.internal

import kotlinx.coroutines.suspendCancellableCoroutine
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import wasabi.service.common.api.LinkPreviewApi
import wasabi.service.common.dao.LinkPreview
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

internal class LinkPreviewApiImpl(
  private val extractors: List<LinkMetaExtractor>,
) : LinkPreviewApi {

  private val connectTimeout = 10.seconds.toInt(DurationUnit.MILLISECONDS)

  override suspend fun fetch(url: String): LinkPreview? {
    return try {
      val document = Jsoup.connect(url)
        .followRedirects(true)
        .timeout(connectTimeout)
        .load()

      extractors
        .asSequence()
        .map { it.extract(url = url, document = document) }
        .firstOrNull()
    } catch (error: IOException) {
      error.printStackTrace()
      null
    }
  }

  private suspend fun Connection.load(): Document = suspendCancellableCoroutine { cont ->
    try {
      val result = this.get()
      cont.resume(result)
    } catch (error: Throwable) {
      error.printStackTrace()
      cont.resumeWithException(error)
    }
  }
}
