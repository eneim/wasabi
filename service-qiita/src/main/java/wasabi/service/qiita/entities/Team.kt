package wasabi.service.qiita.entities

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Qiita Team上で所属しているチームを表します。Qiita Teamでのみ有効です。
 */
@Keep
@JsonClass(generateAdapter = true)
data class Team(
  /**
   * チームが利用可能な状態かどうか
   */
  @Json(name = "active")
  val active: Boolean,
  /**
   * チームの一意なID
   */
  @Json(name = "id")
  val id: String,
  /**
   * チームに設定されている名前を表します。
   */
  @Json(name = "name")
  val name: String
)
