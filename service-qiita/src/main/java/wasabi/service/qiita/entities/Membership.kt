package wasabi.service.qiita.entities

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Qiita Team のチームメンバー情報を表します。
 */
@Keep
@JsonClass(generateAdapter = true)
data class Membership(
  /**
   * チームに登録しているユーザー名
   */
  @Json(name = "name")
  val name: String
)
