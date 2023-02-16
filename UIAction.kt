package com.bits.bytes.presentation

sealed interface UIAction

sealed interface FetchAction : UIAction {

    object Fetch : FetchAction
    object Retry : FetchAction
}

sealed interface DumpAction : UIAction {

    object Dump : DumpAction
}

sealed interface SearchAction : UIAction {

    data class Search(val keyword: String) : SearchAction
}