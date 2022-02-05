package wasabi.service.qiita.entities

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Qiita API v2で認証・認可を行うためのアクセストークンを表します。
 */
@Keep
@JsonClass(generateAdapter = true)
data class AccessToken(
  /**
   * 登録されたAPIクライアントを特定するためのID
   */
  @Json(name = "client_id")
  val clientId: String,
  /**
   * アクセストークンに許された操作の一覧
   */
  @Json(name = "scopes")
  val scopes: List<String>,
  /**
   * アクセストークンを表現する文字列
   */
  @Json(name = "token")
  val token: String
)
