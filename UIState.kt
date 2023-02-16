package com.bits.bytes.presentation

import com.bits.bytes.presentation.model.DisplayPublicAsset

sealed interface UIState

sealed interface FetchUIState : UIState {

    object Fetching : FetchUIState
    object OnFetched : FetchUIState
    data class OnError(val e: Throwable) : FetchUIState
}

sealed interface DumpUIState : UIState {

    data class Dumping(val retriesCount: Int = 0) : DumpUIState
    object OnDumped : DumpUIState
    data class OnError(val e: Throwable, val retriesCount: Int = 0) : DumpUIState
}

sealed interface SearchUIState : UIState {

    data class Searching(val keyword: String) : SearchUIState
    data class OnSearchResults(val publicAssets: List<DisplayPublicAsset>) : SearchUIState
    data class OnError(val e: Throwable) : SearchUIState
}