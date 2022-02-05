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

package wasabi.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import wasabi.service.common.ZonedDateTimeConverter
import wasabi.service.qiita.api.QiitaApi
import wasabi.service.qiita.dao.QiitaArticle
import wasabi.service.qiita.domain.QiitaPagingSource
import java.time.Duration

class QiitaRepository(
  private val qiitaApi: QiitaApi,
) {

  fun getArticles(
    pageSize: Int = PAGE_SIZE,
    enablePlaceholders: Boolean = true,
  ): Pager<Int, QiitaArticle> {
    return Pager(
      config = PagingConfig(
        pageSize = pageSize,
        initialLoadSize = pageSize,
        enablePlaceholders = enablePlaceholders,
        maxSize = pageSize * 5,
      ),
      initialKey = null,
      pagingSourceFactory = {
        QiitaPagingSource(qiitaApi)
      }
    )
  }

  companion object {

    private const val PAGE_SIZE = 24

    // TODO: DI
    fun getInstance(): QiitaRepository = QiitaRepository(
      qiitaApi = Retrofit.Builder()
        .baseUrl("https://qiita.com")
        .addConverterFactory(
          MoshiConverterFactory.create(
            Moshi.Builder()
              .add(ZonedDateTimeConverter)
              .build()
          )
        )
        .callFactory(
          OkHttpClient.Builder()
            .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(Level.HEADERS))
            .connectTimeout(Duration.ofSeconds(15))
            .callTimeout(Duration.ofSeconds(15))
            .readTimeout(Duration.ofSeconds(15))
            .build()
        )
        .build()
        .create(QiitaApi::class.java)
    )
  }
}
