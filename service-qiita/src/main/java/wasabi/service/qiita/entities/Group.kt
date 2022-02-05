package wasabi.service.qiita.entities

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.ZonedDateTime

/**
 * Qiita Teamのグループを表します。
 */
@Keep
@JsonClass(generateAdapter = true)
data class Group(
  /**
   * データが作成された日時
   */
  @Json(name = "created_at")
  val createdAt: ZonedDateTime,
  /**
   * グループの一意なIDを表します。
   */
  @Json(name = "id")
  val id: Int,
  /**
   * グループに付けられた表示用の名前を表します。
   */
  @Json(name = "name")
  val name: String,
  /**
   * 非公開グループかどうかを表します。
   */
  @Json(name = "private")
  val `private`: Boolean,
  /**
   * データが最後に更新された日時
   */
  @Json(name = "updated_at")
  val updatedAt: ZonedDateTime,
  /**
   * グループのチーム上での一意な名前を表します。
   */
  @Json(name = "url_name")
  val urlName: String
)
