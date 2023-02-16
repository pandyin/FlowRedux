package com.bits.bytes.presentation

import com.bits.bytes.domain.FetchUseCase
import com.freeletics.flowredux.dsl.State
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@ViewModelScoped
class FetchUIStateMachine @Inject constructor(private val fetchUseCase: FetchUseCase) :
    UIStateMachine<FetchUIState, FetchAction>({ FetchUIState.OnFetched }) {

    init {
        spec {
            inState<FetchUIState.OnFetched> {
                on { _: FetchAction.Fetch, state: State<FetchUIState.OnFetched> ->
                    state.override { FetchUIState.Fetching }
                }
            }
            inState<FetchUIState.Fetching> {
                onEnter {
                    try {
                        fetchUseCase.execute()
                        it.override { FetchUIState.OnFetched }
                    } catch (e: Exception) {
                        it.override { FetchUIState.OnError(e = e) }
                    }
                }
            }
            inState<FetchUIState.OnError> {
                on { _: FetchAction.Retry, state: State<FetchUIState.OnError> ->
                    state.override { FetchUIState.Fetching }
                }
                // Retry is allowed for up to 5 seconds.
                collectWhileInState(delayFlow(delayInMillis = 5_000L)) { _: Long, state: State<FetchUIState.OnError> ->
                    // Time is up! now switch back to the initial state.
                    state.override { FetchUIState.OnFetched }
                }
            }
        }
    }

    private fun delayFlow(delayInMillis: Long) = flow {
        delay(delayInMillis)
        emit(delayInMillis)
    }
}