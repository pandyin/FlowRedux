package com.bits.bytes.presentation

import com.bits.bytes.domain.DumpUseCase
import com.freeletics.flowredux.dsl.State
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@ViewModelScoped
class DumpUIStateMachine @Inject constructor(private val dumpUseCase: DumpUseCase) :
    UIStateMachine<DumpUIState, DumpAction>({ DumpUIState.OnDumped }) {

    init {
        spec {
            inState<DumpUIState.OnDumped> {
                on { _: DumpAction.Dump, state: State<DumpUIState.OnDumped> ->
                    state.override { DumpUIState.Dumping() }
                }
            }
            inState<DumpUIState.Dumping> {
                onEnter {
                    try {
                        dumpUseCase.execute()
                        it.override { DumpUIState.OnDumped }
                    } catch (e: Exception) {
                        it.override {
                            DumpUIState.OnError(e = e, retriesCount = it.snapshot.retriesCount)
                        }
                    }
                }
            }
            inState<DumpUIState.OnError> {
                // Auto retry with exponential time off.
                onEnter {
                    if (exponentialBackOff(retriesCount = it.snapshot.retriesCount)) {
                        // Max retries exceeded!
                        it.noChange()
                    } else {
                        // Retry.
                        it.override { DumpUIState.Dumping(retriesCount = it.snapshot.retriesCount + 1) }
                    }
                }
            }
        }
    }

    private val initialDelay = 1_000L
    private var currentDelay = initialDelay
    private val maxRetries = 3
    private val backoffFactor = 2.0
    private suspend fun exponentialBackOff(retriesCount: Int): Boolean {
        if (retriesCount == maxRetries) {
            return true
        }
        delay(currentDelay)
        currentDelay = (currentDelay * backoffFactor).toLong()
        return false
    }
}
