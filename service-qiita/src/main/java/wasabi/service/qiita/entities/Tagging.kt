package wasabi.service.qiita.entities

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 記事とタグとの関連を表します。
 */
@Keep
@JsonClass(generateAdapter = true)
data class Tagging(
  /**
   * タグを特定するための一意な名前
   */
  @Json(name = "name")
  val name: String,
  @Json(name = "versions")
  val versions: List<String>
)
