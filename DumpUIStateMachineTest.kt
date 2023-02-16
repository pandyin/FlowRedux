package com.bits.bytes.presentation

import app.cash.turbine.test
import com.bits.bytes.domain.DumpUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class DumpUIStateMachineTest {

    @Test
    fun `should switch to a dumping state after receiving a dump action while a current state is a dumped state`() =
        runTest {
            val mockUseCase = mockk<DumpUseCase>()
            coEvery { mockUseCase.execute() } returns Unit

            val sut = DumpUIStateMachine(dumpUseCase = mockUseCase)
            sut.state.test {
                assertEquals(expected = DumpUIState.OnDumped, actual = awaitItem())
                sut.dispatch(DumpAction.Dump)
                assertEquals(expected = DumpUIState.Dumping(), actual = awaitItem())
                assertEquals(expected = DumpUIState.OnDumped, actual = awaitItem())
            }

            coVerify(exactly = 1) { mockUseCase.execute() }
        }

    @Test
    fun `should switch to a error state when a use case throws an exception and then retry thrice`() = runTest {
        val fakeError = Exception("error")

        val mockUseCase = mockk<DumpUseCase>()
        coEvery { mockUseCase.execute() }.throws(fakeError)

        val sut = DumpUIStateMachine(dumpUseCase = mockUseCase)
        sut.state.test {
            assertEquals(expected = DumpUIState.OnDumped, actual = awaitItem())
            sut.dispatch(DumpAction.Dump)
            repeat(4) {
                assertEquals(
                    expected = DumpUIState.Dumping(retriesCount = it),
                    actual = awaitItem()
                )
                assertEquals(
                    expected = DumpUIState.OnError(e = fakeError, retriesCount = it),
                    actual = awaitItem()
                )
            }
        }

        coVerify(exactly = 4) { mockUseCase.execute() }
    }
}