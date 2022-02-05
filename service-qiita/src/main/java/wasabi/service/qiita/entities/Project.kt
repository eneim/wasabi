package wasabi.service.qiita.entities

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.ZonedDateTime

/**
 * Qiita Team上でのプロジェクトを表します。Qiita Teamでのみ有効です。
 */
@Keep
@JsonClass(generateAdapter = true)
data class Project(
  /**
   * このプロジェクトが進行中かどうか
   */
  @Json(name = "archived")
  val archived: Boolean,
  /**
   * Markdown形式の本文
   */
  @Json(name = "body")
  val body: String,
  /**
   * データが作成された日時
   */
  @Json(name = "created_at")
  val createdAt: ZonedDateTime,
  /**
   * プロジェクトのチーム上での一意なID
   */
  @Json(name = "id")
  val id: Int,
  /**
   * プロジェクト名
   */
  @Json(name = "name")
  val name: String,
  /**
   * 絵文字リアクション数
   */
  @Json(name = "reactions_count")
  val reactionsCount: Int,
  /**
   * HTML形式の本文
   */
  @Json(name = "rendered_body")
  val renderedBody: String,
  /**
   * データが最後に更新された日時
   */
  @Json(name = "updated_at")
  val updatedAt: ZonedDateTime
)
