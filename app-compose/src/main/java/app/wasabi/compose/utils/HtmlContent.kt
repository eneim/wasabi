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

package app.wasabi.compose.utils

import android.content.Context
import android.text.Editable
import android.text.Html
import android.text.Spanned
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml
import io.noties.markwon.core.MarkwonTheme
import io.noties.markwon.core.spans.CodeSpan
import org.jsoup.Jsoup
import org.jsoup.safety.Safelist
import org.xml.sax.XMLReader

internal class HtmlTextParser(context: Context) : Html.TagHandler {

  private val theme = MarkwonTheme.create(context)

  override fun handleTag(
    opening: Boolean,
    tag: String?,
    output: Editable?,
    xmlReader: XMLReader?
  ) {
    if (output == null) return
    if (tag == "code") {
      if (opening) {
        start(output, InlineCode)
      } else {
        end(
          output,
          InlineCode.javaClass,
          listOf(CodeSpan(theme))
        )
      }
    } /* else if (tag == "pre") {
      if (opening) {
        start(output, CodeBlock)
      } else {
        end(
          output,
          CodeBlock.javaClass,
          listOf(
            TypefaceSpan("monospace"),
          )
        )
      }
    } */
  }

  private fun start(
    text: Editable,
    mark: Any
  ) {
    val len = text.length
    text.setSpan(mark, len, len, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
  }

  private fun end(
    text: Editable,
    markClass: Class<Any>,
    spans: List<Any>
  ) {
    val len = text.length
    val mark = text.getSpans(0, len, markClass).lastOrNull()
    if (mark != null) {
      val where = text.getSpanStart(mark)
      text.removeSpan(mark)
      if (where != len) {
        for (span in spans) {
          text.setSpan(span, where, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
      }
    }
  }
}

object InlineCode

// TODO: move this file to common module.
fun String.asHtml(
  context: Context,
  flags: Int = HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH
): CharSequence {
  val doc = Jsoup.parse(
    this
      .replace(Regex.fromLiteral("\\*(\\S.*?)\\*")) { "<i>${it.groupValues[1]}</i>" }
      // .replace(Regex.fromLiteral("\\n")) { "<br/>" } // TODO: respect line break character.
      .replace(Regex.fromLiteral("(^|\\s)(http(s?)\\:\\/\\/\\S*[\\w\\/])")) {
        "${it.groupValues[1]}<a href=\"${it.groupValues[2]}\">${it.groupValues[2]}</a>"
      }
  )
  doc.select("pre").forEach { it.tagName("p") }

  return Jsoup.clean(doc.html(), Safelist.basicWithImages())
    .parseAsHtml(flags, imageGetter = null, HtmlTextParser(context))
}
