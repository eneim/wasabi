package wasabi.service.qiita.entities

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 現在のアクセストークンで認証中のユーザを表します。通常のユーザ情報よりも詳細な情報を含んでいます。
 */
@Keep
@JsonClass(generateAdapter = true)
data class AuthenticatedUser(
  /**
   * 自己紹介文
   */
  @Json(name = "description")
  val description: String,
  /**
   * Facebook ID
   */
  @Json(name = "facebook_id")
  val facebookId: String,
  /**
   * このユーザがフォローしているユーザの数
   */
  @Json(name = "followees_count")
  val followeesCount: Int,
  /**
   * このユーザをフォローしているユーザの数
   */
  @Json(name = "followers_count")
  val followersCount: Int,
  /**
   * GitHub ID
   */
  @Json(name = "github_login_name")
  val githubLoginName: String,
  /**
   * ユーザID
   */
  @Json(name = "id")
  val id: String,
  /**
   * 1ヶ月あたりにQiitaにアップロードできる画像の総容量
   */
  @Json(name = "image_monthly_upload_limit")
  val imageMonthlyUploadLimit: Int,
  /**
   * その月にQiitaにアップロードできる画像の残りの容量
   */
  @Json(name = "image_monthly_upload_remaining")
  val imageMonthlyUploadRemaining: Int,
  /**
   * このユーザが qiita.com 上で公開している記事の数 (Qiita Teamでの記事数は含まれません)
   */
  @Json(name = "items_count")
  val itemsCount: Int,
  /**
   * LinkedIn ID
   */
  @Json(name = "linkedin_id")
  val linkedinId: String,
  /**
   * 居住地
   */
  @Json(name = "location")
  val location: String,
  /**
   * 設定している名前
   */
  @Json(name = "name")
  val name: String,
  /**
   * 所属している組織
   */
  @Json(name = "organization")
  val organization: String,
  /**
   * ユーザごとに割り当てられる整数のID
   */
  @Json(name = "permanent_id")
  val permanentId: Int,
  /**
   * 設定しているプロフィール画像のURL
   */
  @Json(name = "profile_image_url")
  val profileImageUrl: String,
  /**
   * Qiita Team専用モードに設定されているかどうか
   */
  @Json(name = "team_only")
  val teamOnly: Boolean,
  /**
   * Twitterのスクリーンネーム
   */
  @Json(name = "twitter_screen_name")
  val twitterScreenName: String,
  /**
   * 設定しているWebサイトのURL
   */
  @Json(name = "website_url")
  val websiteUrl: String
)
