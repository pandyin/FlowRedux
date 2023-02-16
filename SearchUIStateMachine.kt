package com.bits.bytes.presentation

import com.bits.bytes.domain.SearchUseCase
import com.bits.bytes.domain.model.AssetType
import com.bits.bytes.presentation.model.toDisplayPublicModel
import com.freeletics.flowredux.dsl.State
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@ViewModelScoped
class SearchUIStateMachine @Inject constructor(private val searchUseCase: SearchUseCase) :
    UIStateMachine<SearchUIState, SearchAction>({ SearchUIState.OnSearchResults(publicAssets = emptyList()) }) {

    init {
        spec {
            inState<SearchUIState.OnSearchResults> {
                on { action: SearchAction.Search, state: State<SearchUIState.OnSearchResults> ->
                    state.override { SearchUIState.Searching(keyword = action.keyword) }
                }
            }
            inState<SearchUIState.Searching> {
                onEnter { state ->
                    try {
                        val results = searchUseCase.execute(keyword = state.snapshot.keyword)
                        state.override {
                            SearchUIState.OnSearchResults(results
                                .filter { it.type != AssetType.Unsupported }
                                .sortedBy { it.type.sortOrder }
                                .map { it.toDisplayPublicModel() })
                        }
                    } catch (e: Exception) {
                        state.override { SearchUIState.OnError(e = e) }
                    }
                }
            }
            // Log and switch back to the initial state.
            inState<SearchUIState.OnError> {
                onEnter { it.override { SearchUIState.OnSearchResults(publicAssets = emptyList()) } }
                onEnterEffect { /*Log*/ }
            }
        }
    }
}