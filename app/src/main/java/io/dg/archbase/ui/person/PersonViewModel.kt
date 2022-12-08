/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.dg.archbase.ui.person

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import io.dg.archbase.data.PersonRepository
import io.dg.archbase.ui.person.PersonUiState.Error
import io.dg.archbase.ui.person.PersonUiState.Loading
import io.dg.archbase.ui.person.PersonUiState.Success
import javax.inject.Inject

@HiltViewModel
class PersonViewModel @Inject constructor(
    private val personRepository: PersonRepository
) : ViewModel() {

    val uiState: StateFlow<PersonUiState> = personRepository
        .persons.map(::Success)
        .catch { Error(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)

    fun addPerson(name: String) {
        viewModelScope.launch {
            personRepository.add(name)
        }
    }
}

sealed interface PersonUiState {
    object Loading : PersonUiState
    data class Error(val throwable: Throwable) : PersonUiState
    data class Success(val data: List<String>) : PersonUiState
}
