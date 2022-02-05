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

package app.wasabi.compose.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import wasabi.data.repository.QiitaRepository
import wasabi.service.qiita.dao.QiitaArticle

class QiitaFeedViewModel(
  // TODO: DI
  private val repository: QiitaRepository = QiitaRepository.getInstance()
) : ViewModel() {

  private val _articles = MutableStateFlow(PagingData.empty<QiitaArticle>())
  val articles: Flow<PagingData<QiitaArticle>> = _articles.asStateFlow()

  init {
    viewModelScope.launch(Dispatchers.IO) {
      repository.getArticles().flow
        .cachedIn(this)
        .onEach { feedData -> _articles.value = feedData }
        .collect()
    }
  }
}
