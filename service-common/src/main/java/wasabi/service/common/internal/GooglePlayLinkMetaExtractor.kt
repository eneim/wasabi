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

import org.jsoup.nodes.Document
import wasabi.service.common.dao.LinkPreview
import java.net.URL

internal class GooglePlayLinkMetaExtractor : GenericLinkMetaExtractor() {

  override fun extract(
    url: String,
    document: Document
  ): LinkPreview? {
    return if (!isGooglePlayLink(url)) {
      null
    } else {
      super.extract(url, document)
    }
  }

  override fun readImageUrl(
    url: String,
    document: Document
  ): String {
    // Play Store uses a shitty favicon so I'm using an empty favicon and this image.
    return "https://www.android.com/static/2016/img/icons/why-android/play_2x.png"
  }

  override fun fallbackFaviconUrl(url: String): String? = null

  private companion object {

    private fun isGooglePlayLink(url: String): Boolean {
      return URL(url).host.contains("play.google")
    }
  }
}
