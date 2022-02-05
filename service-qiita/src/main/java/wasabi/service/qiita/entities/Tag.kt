package wasabi.service.qiita.entities

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 記事に付けられた個々のタグを表します。
 */
@Keep
@JsonClass(generateAdapter = true)
data class Tag(
  /**
   * このタグをフォローしているユーザの数
   */
  @Json(name = "followers_count")
  val followersCount: Int,
  /**
   * このタグに設定されたアイコン画像のURL
   */
  @Json(name = "icon_url")
  val iconUrl: String,
  /**
   * タグを特定するための一意な名前
   */
  @Json(name = "id")
  val id: String,
  /**
   * このタグが付けられた記事の数
   */
  @Json(name = "items_count")
  val itemsCount: Int
)
