package com.bits.bytes.presentation

import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@OptIn(ExperimentalCoroutinesApi::class)
@FlowPreview
abstract class UIStateMachine<STATE : UIState, ACTION : UIAction>(initialState: () -> STATE) :
    FlowReduxStateMachine<STATE, ACTION>(initialState)