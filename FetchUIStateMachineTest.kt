package com.bits.bytes.presentation

import app.cash.turbine.test
import com.bits.bytes.domain.FetchUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class FetchUIStateMachineTest {

    @Test
    fun `should switch to a fetching state after receiving a fetch action while a current state is a fetched state`() =
        runTest {
            val mockUseCase = mockk<FetchUseCase>()
            coEvery { mockUseCase.execute() } returns Unit

            val sut = FetchUIStateMachine(fetchUseCase = mockUseCase)
            sut.state.test {
                assertEquals(expected = FetchUIState.OnFetched, actual = awaitItem())
                sut.dispatch(FetchAction.Fetch)
                assertEquals(expected = FetchUIState.Fetching, actual = awaitItem())
                assertEquals(expected = FetchUIState.OnFetched, actual = awaitItem())
            }

            coVerify(exactly = 1) { mockUseCase.execute() }
        }

    @Test
    fun `should switch to a error state when a use case throws an exception and then switch back to the initial state`() = runTest {
        val fakeError = Exception("error")

        val mockUseCase = mockk<FetchUseCase>()
        coEvery { mockUseCase.execute() }.throws(fakeError)

        val sut = FetchUIStateMachine(fetchUseCase = mockUseCase)
        sut.state.test {
            assertEquals(expected = FetchUIState.OnFetched, actual = awaitItem())
            sut.dispatch(FetchAction.Fetch)
            assertEquals(expected = FetchUIState.Fetching, actual = awaitItem())
            assertEquals(expected = FetchUIState.OnError(e = fakeError), actual = awaitItem())
            assertEquals(expected = FetchUIState.OnFetched, actual = awaitItem())
        }

        coVerify(exactly = 1) { mockUseCase.execute() }
    }
}