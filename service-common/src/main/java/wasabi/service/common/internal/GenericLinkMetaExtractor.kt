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

internal open class GenericLinkMetaExtractor : LinkMetaExtractor {

  override fun extract(
    url: String,
    document: Document
  ): LinkPreview? {
    // Websites seem to give better images for twitter, maybe because Twitter shows smaller
    // thumbnails than Facebook. So we'll prefer Twitter over Facebook.
    val title = readTitle(pageDocument = document)
    val imageUrl = readImageUrl(url = url, document = document)
    val faviconUrl = readFaviconUrl(url = url, document = document)
    return LinkPreview(
      url = url,
      title = title,
      faviconUrl = faviconUrl,
      imageUrl = imageUrl
    )
  }

  private fun readTitle(pageDocument: Document): String? = metaTag(
    document = pageDocument,
    attr = "twitter:title",
    useAbsoluteUrl = false
  ).takeNotEmpty()
    ?: metaTag(
      document = pageDocument,
      attr = "og:title",
      useAbsoluteUrl = false
    ).takeNotEmpty()
    ?: pageDocument.title()

  protected open fun readImageUrl(
    url: String,
    document: Document
  ): String? {
    var linkImage = metaTag(
      document = document,
      attr = "twitter:image",
      useAbsoluteUrl = true
    ).takeNotEmpty()
      ?: metaTag(
        document = document,
        attr = "og:image",
        useAbsoluteUrl = true
      ).takeNotEmpty()
      ?: metaTag(
        document = document,
        attr = "twitter:image:src",
        useAbsoluteUrl = true
      ).takeNotEmpty()
      ?: metaTag(
        document = document,
        attr = "og:image:secure_url",
        useAbsoluteUrl = true
      ).takeNotEmpty()

    // So... scheme-less URLs are also a thing.
    if (linkImage != null && linkImage.startsWith("//")) {
      val imageUrl = URL(url)
      linkImage = imageUrl.protocol + linkImage
    }
    return linkImage
  }

  private fun readFaviconUrl(
    url: String,
    document: Document
  ): String? = linkRelTag(
    document = document,
    rel = "apple-touch-icon"
  ).takeNotEmpty()
    ?: linkRelTag(
      document = document,
      rel = "apple-touch-icon-precomposed"
    ).takeNotEmpty()
    ?: linkRelTag(
      document = document,
      rel = "shortcut icon"
    ).takeNotEmpty()
    ?: linkRelTag(
      document = document,
      rel = "icon"
    ).takeNotEmpty()
    ?: fallbackFaviconUrl(url)

  // Thanks Google for the backup!
  protected open fun fallbackFaviconUrl(url: String): String? =
    "https://www.google.com/s2/favicons?domain_url=$url"

  internal companion object {

    fun metaTag(
      document: Document,
      attr: String,
      useAbsoluteUrl: Boolean
    ): String? {
      var elements = document.select("meta[name=$attr]")
      for (element in elements) {
        val url: String? = element.attr(if (useAbsoluteUrl) "abs:content" else "content")
        if (url != null) {
          return url
        }
      }
      elements = document.select("meta[property=$attr]")
      for (element in elements) {
        val url: String? = element.attr(if (useAbsoluteUrl) "abs:content" else "content")
        if (url != null) {
          return url
        }
      }
      return null
    }

    fun linkRelTag(
      document: Document,
      rel: String
    ): String? {
      val elements = document.head().select("link[rel=$rel]")
      if (elements.isEmpty()) {
        return null
      }
      var largestSizeUrl = elements.first()?.attr("abs:href") ?: return null
      var largestSize = 0
      for (element in elements) { // Some websites have multiple icons for different sizes. Find the largest one.
        val sizes = element.attr("sizes")
        var size: Int
        if (sizes.contains("x")) {
          size = sizes.split("x").toTypedArray()[0].toInt()
          if (size > largestSize) {
            largestSize = size
            largestSizeUrl = element.attr("abs:href")
          }
        }
      }
      return largestSizeUrl
    }

    fun String?.takeNotEmpty(): String? = takeIf {
      it != null && it.isNotEmpty()
    }
  }
}
